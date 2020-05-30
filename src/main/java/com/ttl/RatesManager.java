package com.ttl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Singleton
public class RatesManager {
    private static final Gson gson = new Gson();

    private List<Rates> cachedRates;

    ConfigManager configManager;

    @Inject
    public RatesManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public synchronized List<Rates> getRates() {
        return getRates(true);
    }

    private synchronized List<Rates> getRates(boolean useConfig) {
        if (useConfig && cachedRates != null) {
            return cachedRates;
        }

        if (useConfig && !Strings.isNullOrEmpty(getRatesConfig())) {
            cachedRates = getConfigRates();
        } else {
            cachedRates = getDefaultRates();
        }

        return cachedRates;
    }

    private List<Rates> getConfigRates() {
        return gson.fromJson(getRatesConfig(), tt());
    }

    private List<Rates> getDefaultRates() {
        return gson.fromJson(new InputStreamReader(TimeToLevelPlugin.class.getResourceAsStream("/rates.json")), tt());
    }

    public Rates getRates(Skill skill) {
        return getRates(skill, true);

    }

    private Rates getRates(Skill skill, boolean useConfig) {
        return getRates(useConfig)
                .stream()
                .filter(r -> r.getSkill().equals(skill.name().toLowerCase()))
                .findFirst()
                .map(Rates::new) // clone this Rates
                .orElse(null);
    }

    public synchronized void saveRates(Rates r) {
        List<Rates> allRates = getRates();

        for (int i = 0; i < allRates.size(); i++) {
            Rates allR = allRates.get(i);
            if (allR.getSkill().equals(r.getSkill())) {
                allRates.set(i, r);
                cachedRates = allRates;
                configManager.setConfiguration(TimeToLevelConfig.CONFIG_GROUP, TimeToLevelConfig.RATES_KEY, gson.toJson(cachedRates));
                break;
            }
        }
    }

    public void resetRates() {
        InputStream ratesStream = TimeToLevelPlugin.class.getResourceAsStream("/rates.json");
        cachedRates = gson.fromJson(new InputStreamReader(ratesStream), new TypeToken<List<Rates>>() {
        }.getType());
        configManager.setConfiguration(TimeToLevelConfig.CONFIG_GROUP, TimeToLevelConfig.RATES_KEY, gson.toJson(cachedRates));
        configManager.setConfiguration(TimeToLevelConfig.CONFIG_GROUP, TimeToLevelConfig.BUYABLE_KEY, TimeToLevelConfig.DEFAULT_BUYABLES);
    }

    public String getRatesConfig() {
        return configManager.getConfiguration(TimeToLevelConfig.CONFIG_GROUP, TimeToLevelConfig.RATES_KEY);
    }

    public boolean setRatesConfig(String rates) {
        if (rates == null || rates.isEmpty()) {
            return false;
        }

        cachedRates = gson.fromJson(rates, tt());
        configManager.setConfiguration(TimeToLevelConfig.CONFIG_GROUP, TimeToLevelConfig.RATES_KEY, gson.toJson(cachedRates));
        return true;
    }

    private static Type tt() {
        return new TypeToken<List<Rates>>() {
        }.getType();
    }

    public Rates resetRates(Skill skill) {
        Rates r = getRates(skill, false);
        saveRates(r);
        return r;
    }
}
