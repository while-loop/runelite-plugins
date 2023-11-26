package com.runewatch;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.annotations.VisibleForDevtools;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Singleton
public class CaseManager {
    private static final HttpUrl RUNEWATCH_LIST_URL = HttpUrl.parse("https://raw.githubusercontent.com/while-loop/runelite-plugins/runewatch-updater/mixedlist.json");

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final Type typeToken = new TypeToken<List<Case>>() {
    }.getType();

    private final OkHttpClient client;
    private final Gson gson;
    private final Map<String, Case> rwCases = new ConcurrentHashMap<>();
    private final Map<String, Case> wdrCases = new ConcurrentHashMap<>();
    private final ClientThread clientThread;
    private final RuneWatchConfig config;

    @Inject
    private CaseManager(OkHttpClient client, ClientThread clientThread, RuneWatchConfig config, Gson gson) {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
        this.gson = gson.newBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            try {
                // Allow handling of the occasional empty string as a date.
                return df.parse(json.getAsString());
            } catch (ParseException e) {
                return Date.from(Instant.ofEpochSecond(0));
            }
        }).create();;
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
                    if (response.code() < 200 || response.code() >= 400) {
                        log.error("failed to get watchlist. status: {}", response.code());
                    } else {
                        List<Case> cases = gson.fromJson(new InputStreamReader(response.body().byteStream()), typeToken);
                        rwCases.clear();
                        wdrCases.clear();
                        for (Case c : cases) {
                            String rsn = c.getRsn().toLowerCase();
                            Map<String, Case> sourceCases = (c.getSource().toLowerCase().equals("wdr")) ? wdrCases : rwCases;

                            Case old = sourceCases.get(rsn);
                            // keep the newest case
                            if (old == null || old.getDate().before(c.getDate())) {
                                sourceCases.put(rsn, c);
                            }
                        }
                        log.debug("saved {}/{} rw cases", rwCases.size(), cases.size());
                        log.debug("saved {}/{} wdr cases", wdrCases.size(), cases.size());
                    }
                } finally {
                    response.close();
                    if (onComplete != null) {
                        clientThread.invokeLater(onComplete);
                    }
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
        String cleanRsn = Text.removeTags(Text.toJagexName(rsn)).toLowerCase();
        if (config.useRW()) {
            Case c = rwCases.get(cleanRsn);
            if (c != null) {
                return c;
            }
        }

        if (config.useWDR()) {
            Case c = wdrCases.get(cleanRsn);
            if (c != null) {
                return c;
            }
        }

        return null;
    }

    /**
     * Lookup a non-cached RuneWatch case.
     *
     * @param rsn
     * @param onComplete function returning the Case (mullable) if found. Called on the client thread
     * @return
     */
    public void get(String rsn, Consumer<Case> onComplete) {
        refresh(() -> onComplete.accept(get(rsn)));
    }

    @VisibleForDevtools
    void put(String rsn, String source) {
        if (source.toLowerCase().equals("rw")) {
            rwCases.put(rsn.toLowerCase(), new Case(rsn, new Date(), "aaa", "debugging", "3", source));
        } else {
            wdrCases.put(rsn.toLowerCase(), new Case(rsn, new Date(), "aaa", "debugging", "3", source));
        }
    }
}
