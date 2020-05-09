package com.gauntlet;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "Gauntlet",
        description = "Track collected resources in the Gauntlet",
        tags = {"tracker","gauntlet","resources", "corrupted"}
)
public class GauntletPlugin extends Plugin {
    static final String CONFIG_GROUP = "gauntlet";

    private static final String GAUNTLET_ENTER = "You enter the Gauntlet.";

    private static final String DROP_MESSAGE = "Untradeable drop:";
    private static final Pattern DROP_PATTERN = Pattern.compile("((?<quantity>\\d+) x )?(?<item>.*)");

    private static final int GAUNTLET_VARBIT = 9178;

    private static final int GAUNTLET_REGION = 7512;
    private static final int CORRUPTED_GAUNTLET_REGION = 7768;
    private static final int GAUNTLET_LOBBY_REGION = 12127;

    private static final List<Integer> GAUNTLET_REGION_IDS = Arrays.asList(
            GAUNTLET_REGION,
            CORRUPTED_GAUNTLET_REGION
    );


    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    ItemManager itemManager;

    @Getter
    private final Map<Integer, Integer> itemCounts = new HashMap<>();


    @Provides
    GauntletConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GauntletConfig.class);
    }

    @Override
    protected void startUp() {
        reset();
    }

    @Override
    protected void shutDown() {
        reset();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if ((event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
                || client.getLocalPlayer() == null) {
            return;
        }

        String message = Text.removeTags(event.getMessage());
        if (message.equals(GAUNTLET_ENTER) || !isInGauntlet()) {
            reset();
            return;
        }

        boolean corrupted = getRegionId() == CORRUPTED_GAUNTLET_REGION;

        if (message.startsWith(DROP_MESSAGE)) {
            processDrop(message, corrupted);
            return;
        }

        for (GauntletResource resource : GauntletResource.values()) {
            if (resource.getPattern() == null) {
                continue;
            }

            Matcher m = resource.getPattern().matcher(message);
            int itemId = resource.getItemId();
            if (m.matches() && (resource.isShared() || (corrupted == resource.isCorrupted()))) {
                if (m.groupCount() > 0) {
                    incrementItem(itemId, Integer.parseInt(m.group(1)));
                } else {
                    incrementItem(itemId, 1);
                }
                break;
            }
        }
    }

    private void processDrop(String dropMessage, boolean corrupted) {
        dropMessage = dropMessage.replace(DROP_MESSAGE, "").trim();
        Matcher m = DROP_PATTERN.matcher(dropMessage);
        if (!m.matches()) {
            return;
        }

        int quantity = 1;
        String itemName = null;
        if (m.group("quantity") != null) {
            quantity = Integer.parseInt(m.group("quantity"));
        }

        if (m.group("item") != null) {
            itemName = m.group("item");
        }

        if (itemName != null) {
            int itemId = GauntletResource.FROM_DROP(itemName, corrupted);
            if (itemId > 0) {
                incrementItem(itemId, quantity);
            }
        }
    }

    private void reset() {
        if (itemCounts.size() > 0) {
            itemCounts.clear();
            infoBoxManager.removeIf(c -> c instanceof GauntletResourceCounter);
        }
    }

    private void incrementItem(int itemId, int delta) {
        int count = itemCounts.computeIfAbsent(itemId, (id) -> 0);
        if (count == 0) {
            infoBoxManager.addInfoBox(new GauntletResourceCounter(itemManager, this, itemId, delta));
        }
        itemCounts.put(itemId, count + delta);
    }

    boolean isInGauntlet() {
        return GAUNTLET_REGION_IDS.contains(getRegionId());
    }

    int getRegionId() {
        if (client.getLocalPlayer() == null) {
            return -1;
        }

        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
    }
}
