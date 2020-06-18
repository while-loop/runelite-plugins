package com.runewatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.annotations.VisibleForDevtools;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Singleton
public class CaseManager {
    private static final HttpUrl RUNEWATCH_LIST_URL = HttpUrl.parse("https://www.runewatch.com/api/cases/list");
    private static final HttpUrl RUNEWATCH_CASES_URL = HttpUrl.parse("https://www.runewatch.com/api/cases");
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
    private static final Type typeToken = new TypeToken<List<Case>>() {
    }.getType();

    private final OkHttpClient client;
    private final Map<String, Case> runewatchCases = new ConcurrentHashMap<>();
    private final ClientThread clientThread;

    @Inject
    private CaseManager(OkHttpClient client, ClientThread clientThread) {
        this.client = client;
        this.clientThread = clientThread;
    }

    /**
     * @param onComplete called once the list has been refreshed. Called on the client thread
     */
    public void refresh(Runnable onComplete) {
        Request rwReq = new Request.Builder().url(RUNEWATCH_LIST_URL).build();

        // call on background thread
        client.newCall(rwReq).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("failed to get runewatch list: {}", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    List<Case> cases = GSON.fromJson(new InputStreamReader(response.body().byteStream()), typeToken);
                    runewatchCases.clear();
                    for (Case c : cases) {
                        String rsn = c.getRsn().toLowerCase();
                        Case old = runewatchCases.get(rsn);
                        // keep the newest case
                        if (old == null || old.getDate().before(c.getDate())) {
                            runewatchCases.put(rsn, c);
                        }
                    }
                    log.info("saved {}/{} runewatch cases", runewatchCases.size(), cases.size());
                    if (onComplete != null) {
                        clientThread.invokeLater(onComplete);
                    }
                } finally {
                    response.close();
                }

            }
        });
    }

    /**
     * Get a RuneWatch case from the cached list
     *
     * @param rsn
     * @return
     */
    public Case get(String rsn) {
        return runewatchCases.get(Text.removeTags(Text.toJagexName(rsn)).toLowerCase());
    }

    /**
     * Lookup a non-cached RuneWatch case.
     *
     * @param rsn
     * @param onComplete function returning the Case (mullable) if found. Called on the client thread
     * @return
     */
    public void get(String rsn, Consumer<Case> onComplete) {
        final String cleanRsn = Text.removeTags(Text.toJagexName(rsn)).toLowerCase();
        Request req = new Request.Builder().url(RUNEWATCH_CASES_URL.newBuilder().addPathSegment(cleanRsn).build()).build();

        // call on background thread
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("failed to get runewatch case for {}: {}", cleanRsn, e.toString());
                if (onComplete != null) {
                    clientThread.invokeLater(() -> onComplete.accept(null));
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    List<Case> cases = GSON.fromJson(new InputStreamReader(response.body().byteStream()), typeToken);
                    log.debug("got runewatch case for {} {}", cleanRsn, cases.size());
                    // get the latest case
                    cases.sort(Comparator.comparing(Case::getDate));
                    Case rwCase = (cases.size() <= 0) ? null : cases.get(cases.size() - 1);
                    if (rwCase != null) {
                        runewatchCases.put(cleanRsn, rwCase);
                    } else {
                        // case where an appeal process was successful
                        runewatchCases.remove(cleanRsn);
                    }

                    if (onComplete != null) {
                        clientThread.invokeLater(() -> onComplete.accept(rwCase));
                    }
                } finally {
                    response.close();
                }

            }
        });
    }

    @VisibleForDevtools
    void put(String rsn) {
        runewatchCases.put(rsn.toLowerCase(), new Case(rsn, new Date(), "aaa", "debugging", "3"));
    }
}
