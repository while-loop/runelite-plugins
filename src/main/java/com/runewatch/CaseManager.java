package com.runewatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.annotations.VisibleForDevtools;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Singleton
public class CaseManager {
    private static final HttpUrl RUNEWATCH_LIST_URL = HttpUrl.parse("https://www.runewatch.com/api/cases/list");
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
    private static final Type typeToken = new TypeToken<List<Case>>() {
    }.getType();

    private final OkHttpClient client;
    private final Map<String, Case> runewatchCases = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor;

    @Inject
    private CaseManager(OkHttpClient client, ScheduledExecutorService executor) {
        this.client = client;
        this.executor = executor;
    }

    public void refresh() {
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
                } finally {
                    response.close();
                }

            }
        });
    }

    public Case get(String rsn) {
        return runewatchCases.get(Text.removeTags(Text.toJagexName(rsn)).toLowerCase());
    }

    @VisibleForDevtools
    void put(String rsn) {
        runewatchCases.put(rsn.toLowerCase(), new Case(rsn, new Date(), "aaa", "debugging", "3"));
    }
}
