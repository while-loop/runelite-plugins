package com.runewatch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import joptsimple.internal.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.client.util.ImageUploadStyle.NEITHER;

@Slf4j
@PluginDescriptor(
        name = "RuneWatch",
        description = "",
        tags = {},
        enabledByDefault = false
)
public class RuneWatchPlugin extends Plugin {
    static final String CONFIG_GROUP = "runewatch";
    private static final String INVESTIGATE = "Investigate";
    private static final String FOLDER_NAME = "Trades";

    private static final String NBSP = Character.toString((char) 160);

    private static final String ACCEPTED_TRADE_MSG = "Accepted trade.";
    private static final String DECLINED_TRADE_MSG = "Other player declined trade.";
    private static final String DECLINE_MSG = "Decline";

    private static final Pattern TRADING_WITH_PATTERN = Pattern.compile("Trading [W|w]ith:(<br>|\\s)(.*)");

    private static final int PLAYER_TRADE_OFFER_GROUP_ID = 335;
    private static final int PLAYER_TRADE_OFFER_TRADING_WITH = 31;
    private static final int PLAYER_TRADE_OFFER_TRADE_MODIFIED_ME = 26;
    private static final int PLAYER_TRADE_OFFER_TRADE_MODIFIED_THEM = 29;

    private static final int PLAYER_TRADE_CONFIRMATION_GROUP_ID = 334;
    private static final int PLAYER_TRADE_CONFIRMATION_TRADING_WITH = 30;
    private static final int PLAYER_TRADE_CONFIRMATION_TRADE_MODIFIED_THEM = 31;

    private static final int RAIDING_PARTY_RECRUITMENT_BOARD_GROUP_ID = 507;

    private static final List<Integer> TRADE_SCREEN_GROUP_IDS = Arrays.asList(
            PLAYER_TRADE_OFFER_GROUP_ID,
            PLAYER_TRADE_CONFIRMATION_GROUP_ID
    );

    private static final List<Integer> MENU_WIDGET_IDS = ImmutableList.of(
            WidgetInfo.FRIENDS_LIST.getGroupId(),
            WidgetInfo.CLAN_CHAT.getGroupId(),
            WidgetInfo.CHATBOX.getGroupId(),
            WidgetInfo.RAIDING_PARTY.getGroupId(),
            WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId(),
            WidgetInfo.IGNORE_LIST.getGroupId(),
            RAIDING_PARTY_RECRUITMENT_BOARD_GROUP_ID
    );

    private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of(
            "Message", "Add ignore", "Remove friend", "Delete", "Kick"
    );

    @Inject
    private Client client;

    @Inject
    private RuneWatchConfig config;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private MenuManager menuManager;

    @Inject
    private ImageCapture imageCapture;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private RuneWatchOverlay runeWatchOverlay;

    @Getter(AccessLevel.PACKAGE)
    private BufferedImage reportButton;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private OverlayManager overlayManager;

    private Image tradeImage;
    private String trader;

    @Provides
    RuneWatchConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RuneWatchConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        if (config.playerOption() && client != null) {
            menuManager.addPlayerMenuItem(INVESTIGATE);
        }

        spriteManager.getSpriteAsync(SpriteID.CHATBOX_REPORT_BUTTON, 0, s -> reportButton = s);
        overlayManager.add(runeWatchOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        if (config.playerOption() && client != null) {
            menuManager.removePlayerMenuItem(INVESTIGATE);
        }

        overlayManager.remove(runeWatchOverlay);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!config.menuOption()) {
            return;
        }

        int groupId = WidgetInfo.TO_GROUP(event.getActionParam1());
        String option = event.getOption();

        if (!MENU_WIDGET_IDS.contains(groupId) || !AFTER_OPTIONS.contains(option)) {
            return;
        }

        if (option.equals("Message") && groupId == WidgetInfo.FRIENDS_LIST.getGroupId()) {
            return;
        }

        final MenuEntry lookup = new MenuEntry();
        lookup.setOption(INVESTIGATE);
        lookup.setTarget(event.getTarget());
        lookup.setType(MenuAction.RUNELITE.getId());
        lookup.setParam0(event.getActionParam0());
        lookup.setParam1(event.getActionParam1());
        lookup.setIdentifier(event.getIdentifier());

        insertMenuEntry(lookup, client.getMenuEntries());
    }

    @Subscribe
    public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event) {
        if (!event.getMenuOption().equals(INVESTIGATE)) {
            return;
        }

        String rsn = event.getMenuTarget();
        showPlayerWarning(rsn, true, false);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == ScriptID.CLAN_CHAT_CHANNEL_BUILD) {
            colorClanChat();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        for (int gid : TRADE_SCREEN_GROUP_IDS) {
            Widget w = client.getWidget(gid, 0);
            if (w != null) {
                showTradeWarning(gid);
            }
        }

        colorRaidsList();
        colorTob();
    }

    /**
     * [Client] INFO com.runewatch.RuneWatchPlugin - 1004
     * [Client] INFO com.runewatch.RuneWatchPlugin - 3351
     * [Client] INFO com.runewatch.RuneWatchPlugin - 3350
     * [Client] INFO com.runewatch.RuneWatchPlugin - 3175
     * [Client] INFO com.runewatch.RuneWatchPlugin - 3174
     *
     * @param event
     */

    @Subscribe
    public void onClanMemberJoined(ClanMemberJoined event) {
        String rsn = Text.toJagexName(event.getMember().getName());
        String local = client.getLocalPlayer().getName();
        if (rsn.equals(local)) {
            return;
        }

        showPlayerWarning(rsn, false, true);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        int groupId = WidgetInfo.TO_GROUP(event.getWidgetId());
        String option = event.getMenuOption();

        if (!TRADE_SCREEN_GROUP_IDS.contains(groupId)) {
            return;
        }

        if (option.equals(DECLINE_MSG)) {
            clearScreenshot();
        }
    }

    @Subscribe
    public void onWidgetLoaded(final WidgetLoaded event) {
        int groupId = event.getGroupId();
        if (groupId == PLAYER_TRADE_CONFIRMATION_GROUP_ID) {
            takeScreenshot();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.TRADE) {
            return;
        }

        String msg = event.getMessage();
        switch (msg) {
            case ACCEPTED_TRADE_MSG:
                saveScreenshot();
                break;
            case DECLINED_TRADE_MSG:
                clearScreenshot();
        }
    }

    private void clearScreenshot() {
        tradeImage = null;
        trader = null;
    }

    private void takeScreenshot() {
        runeWatchOverlay.queueForTimestamp(image -> {
            Widget nameWidget = client.getWidget(PLAYER_TRADE_CONFIRMATION_GROUP_ID, PLAYER_TRADE_CONFIRMATION_TRADING_WITH);
            trader = "unknown";
            if (nameWidget != null) {
                Matcher m = TRADING_WITH_PATTERN.matcher(nameWidget.getText());
                if (m.matches()) {
                    trader = m.group(2);
                }
            }

            tradeImage = image;
        });
    }

    private void saveScreenshot() {
        String otherRsn = trader;
        Image image = tradeImage;

        // Draw the game onto the screenshot off of the game thread
        executor.submit(() -> {
            BufferedImage screenshot = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = screenshot.getGraphics();
            int gameOffsetX = 0;
            int gameOffsetY = 0;

            graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
            imageCapture.takeScreenshot(screenshot, otherRsn, FOLDER_NAME, true, NEITHER);
        });
    }

    private void showTradeWarning(int groupId) {
        int tradeModifiedId = PLAYER_TRADE_CONFIRMATION_TRADE_MODIFIED_THEM;
        int tradeModifiedMeId = PLAYER_TRADE_CONFIRMATION_TRADING_WITH;
        int tradingWithId = PLAYER_TRADE_CONFIRMATION_TRADING_WITH;
        if (groupId == PLAYER_TRADE_OFFER_GROUP_ID) {
            tradeModifiedId = PLAYER_TRADE_OFFER_TRADE_MODIFIED_THEM;
            tradingWithId = PLAYER_TRADE_OFFER_TRADING_WITH;
            tradeModifiedMeId = PLAYER_TRADE_OFFER_TRADE_MODIFIED_ME;
        }

        Widget tradeModified = client.getWidget(groupId, tradeModifiedId);
        Widget tradingWith = client.getWidget(groupId, tradingWithId);
        Widget tradeModifiedMe = client.getWidget(groupId, tradeModifiedMeId);
        if (tradingWith == null || tradeModified == null) {
            log.warn("no trading with widget found");
            return;
        }

        Matcher m = TRADING_WITH_PATTERN.matcher(tradingWith.getText());
        if (!m.matches()) {
            log.warn("no rsn found in trading with widget: " + tradingWith.getText());
            return;
        }
        String trader = m.group(2);
        RuneWatchCase rwCase = getUserCase(trader);
        if (rwCase == null) {
            return;
        }

        String wText = tradeModified.getText();
        if (!wText.contains("WARNING")) {
            String warningMsg = String.format("<br>WARNING: %s is on RuneWatch's list.", trader);
            String msg = wText + warningMsg;
            tradeModified.setText(msg);

            // check if this is the first time we've offset x/y
            if (tradeModified.getOriginalY() == tradeModifiedMe.getOriginalY()) {
                tradeModified.setOriginalY(tradeModified.getOriginalY() - 10);
                tradeModified.setOriginalX(tradeModified.getOriginalX() - 20);
            }
            tradeModified.setHidden(false);
            tradeModified.revalidate();
        }
    }

    private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries) {
        MenuEntry[] newMenu = ObjectArrays.concat(entries, newEntry);
        int menuEntryCount = newMenu.length;
        ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
        client.setMenuEntries(newMenu);
    }

    private void showPlayerWarning(String rsn, boolean notifyClear, boolean clan) {
        rsn = Text.toJagexName(rsn);
        RuneWatchCase rwCase = getUserCase(rsn);
        ChatMessageBuilder response = new ChatMessageBuilder();
        if (clan) {
            response.append("Clan member, ");
        }
        response.append(ChatColorType.HIGHLIGHT)
                .append(rsn)
                .append(ChatColorType.NORMAL);

        if (rwCase == null && !notifyClear) {
            return;
        } else if (rwCase == null) {
            response.append(" is not on RuneWatch's list.");
        } else {
            response
                    .append(" is on RuneWatch's list for ")
                    .append(ChatColorType.HIGHLIGHT)
                    .append(rwCase.getType() + " " + rwCase.niceValue() + " ")
                    .append(ChatColorType.NORMAL)
                    .append("on " + rwCase.niceDate())
                    .append(".");
        }

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(response.build())
                .build());
    }

    private RuneWatchCase getUserCase(String rsn) {
        rsn = Text.toJagexName(rsn);
        Random rnd = new Random();
//        if (rnd.nextBoolean()) {
//            return null;
//        }

        return new RuneWatchCase(rsn, Calendar.getInstance(), Math.abs(rnd.nextInt()), "LOAN");
    }

    private void colorClanChat() {
        Widget ccList = client.getWidget(WidgetInfo.CLAN_CHAT_LIST);
        if (ccList == null) {
            return;
        }

        Widget[] players = ccList.getDynamicChildren();
        for (int i = 0; i < players.length; i += 3) {
            Widget player = players[i];
            if (player == null) {
                continue;
            }

            RuneWatchCase rwCase = getUserCase(Text.toJagexName(player.getText()));
            if (rwCase == null) {
                continue;
            }

            player.setTextColor(Color.RED.getRGB());
            player.revalidate();
        }
    }

    private void colorRaidsList() {
        Widget raidsList = client.getWidget(WidgetID.RAIDING_PARTY_GROUP_ID, 10);
        if (raidsList == null) {
            return;
        }
        Widget[] players = raidsList.getDynamicChildren();
        for (int i = 1; i < players.length; i += 4) {
            Widget player = players[i];
            if (player == null) {
                continue;
            }

            RuneWatchCase rwCase = getUserCase(Text.removeTags(Text.toJagexName(player.getText())));
            if (rwCase == null) {
                continue;
            }

            player.setTextColor(Color.RED.getRGB());
            player.setText(Text.removeTags(player.getText()));
            player.revalidate();
        }
    }

    private void colorTob() {
        Widget tobList = client.getWidget(28, 9);
        if (tobList != null) {
            String names = tobList.getText(); // whileloop The Olmlet<br>-<br>-<br>-
            if (Strings.isNullOrEmpty(names)) {
                return;
            }

            List<String> newNames = new ArrayList<>();
            for (String name : names.split("<br>")) {
                if (name.equals("-")) {
                    newNames.add("-");
                    continue;
                }

                String stripped = Text.removeTags(name);
                RuneWatchCase rwCase = getUserCase(stripped);
                if (rwCase == null) {
                    newNames.add(name);
                    continue;
                }

                String colored = new ChatMessageBuilder().append(Color.RED, stripped).build();
                newNames.add(colored);
            }

            tobList.setText(String.join("<br>", newNames));
        }

        // get current party list
        tobList = client.getWidget(28, 9);

        // get current party application list
        tobList = client.getWidget(28, 9);
    }
}




















