package com.runewatch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import joptsimple.internal.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "RuneWatch",
        description = "",
        tags = {},
        enabledByDefault = false
)
public class RuneWatchPlugin extends Plugin {
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

    private static final int COX_PARTY_LIST_GROUP_ID = 499;
    private static final int TOB_PARTY_LIST_GROUP_ID = 364;
    private static final int COX_PARTY_DETAILS_GROUP_ID = 507;
    private static final int TOB_PARTY_DETAILS_GROUP_ID = 50;

    // https://raw.githubusercontent.com/RuneStar/cache-names/master/names.tsv
    private static final int SCRIPT_ID_TOB_HUD_DRAW = 2297;
    private static final int SCRIPT_ID_RAIDS_SIDEPANEL_ENTRY_SETUP = 1550;

    private static final List<Integer> TRADE_SCREEN_GROUP_IDS = Arrays.asList(
            PLAYER_TRADE_OFFER_GROUP_ID,
            PLAYER_TRADE_CONFIRMATION_GROUP_ID
    );

    private static final List<Integer> MENU_WIDGET_IDS = ImmutableList.of(
            WidgetInfo.FRIENDS_LIST.getGroupId(),
            WidgetInfo.IGNORE_LIST.getGroupId(),

            WidgetInfo.CHATBOX.getGroupId(),
            WidgetInfo.FRIENDS_CHAT.getGroupId(),
            WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId(),

            WidgetInfo.RAIDING_PARTY.getGroupId(),
            COX_PARTY_DETAILS_GROUP_ID,
            TOB_PARTY_DETAILS_GROUP_ID,

            WidgetInfo.CLAN_MEMBER_LIST.getGroupId(),
            WidgetInfo.CLAN_GUEST_MEMBER_LIST.getGroupId()
    );

    private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of(
            "Message", "Add ignore", "Remove friend", "Delete", "Kick", "Reject"
    );

    private enum AlertType {
        NONE(""),
        FRIENDS_CHAT("Friends chat member, "),
        CLAN_CHAT("Clan chat member, "),
        NEARBY("Nearby player, ");

        private final String message;

        AlertType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

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
    private RuneWatchOverlay screenshotOverlay;

    @Getter(AccessLevel.PACKAGE)
    private BufferedImage reportButton;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    CaseManager caseManager;

    @Inject
    ClientThread clientThread;

    @Inject
    @Named("developerMode")
    boolean developerMode;

    @Inject
    RuneWatchInputListener hotkeyListener;

    @Inject
    KeyManager keyManager;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean hotKeyPressed;

    private Image tradeImage;
    private String trader;

    private static Map<String, Instant> lastNotified = new ConcurrentHashMap<>();

    @Provides
    RuneWatchConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RuneWatchConfig.class);
    }

    @Override
    protected void startUp() {
        if (config.playerOption() && client != null) {
            menuManager.addPlayerMenuItem(INVESTIGATE);
        }

        keyManager.registerKeyListener(hotkeyListener);
        spriteManager.getSpriteAsync(SpriteID.CHATBOX_REPORT_BUTTON, 0, s -> reportButton = s);
        overlayManager.add(screenshotOverlay);
        caseManager.refresh(this::colorAll);
    }

    @Override
    protected void shutDown() {
        if (config.playerOption() && client != null) {
            menuManager.removePlayerMenuItem(INVESTIGATE);
        }
        keyManager.unregisterKeyListener(hotkeyListener);
        overlayManager.remove(screenshotOverlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(RuneWatchConfig.CONFIG_GROUP)) {
            return;
        }

        if (event.getKey().equals(RuneWatchConfig.PLAYER_OPTION)) {
            if (!Boolean.parseBoolean(event.getOldValue()) && Boolean.parseBoolean(event.getNewValue())) {
                menuManager.addPlayerMenuItem(INVESTIGATE);
            } else if (Boolean.parseBoolean(event.getOldValue()) && !Boolean.parseBoolean(event.getNewValue())) {
                menuManager.removePlayerMenuItem(INVESTIGATE);
            }
        } else if (event.getKey().equals(RuneWatchConfig.PLAYER_TEXT_COLOR)) {
            colorAll();
        }
    }

    @Subscribe
    public void onFocusChanged(FocusChanged focusChanged) {
        if (!focusChanged.isFocused()) {
            hotKeyPressed = false;
        }
    }

    private void colorAll() {
        clientThread.invokeLater(() -> {
            colorFriendsChat();
            colorClanChat();
            colorGuestClanChat();

            colorRaidsSidePanel();
            colorRaidsPartyList();
            colorRaidsParty();

            colorTobHud();
            colorTobParty();
            colorTobPartyList();
        });
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned playerSpawned) {
        if (!config.notifyOnNearby()) {
            return;
        }
        String name = playerSpawned.getPlayer().getName();
        this.alertPlayerWarning(name, false, AlertType.NEARBY);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!config.menuOption() || (!hotKeyPressed && config.useHotkey())) {
            return;
        }

        int groupId = WidgetInfo.TO_GROUP(event.getActionParam1());
        String option = event.getOption();

        if (!MENU_WIDGET_IDS.contains(groupId) || !AFTER_OPTIONS.contains(option)) {
            return;
        }

        for (MenuEntry me : client.getMenuEntries()) {
            // don't add menu option if we've already added investigate
            if (INVESTIGATE.equals(me.getOption())) {
                return;
            }
        }


        client.createMenuEntry(-1)
                .setOption(INVESTIGATE)
                .setTarget(event.getTarget())
                .setType(MenuAction.RUNELITE)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier());
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        Runnable color = null;
        switch (event.getScriptId()) {
            case ScriptID.FRIENDS_CHAT_CHANNEL_REBUILD:
                color = this::colorFriendsChat;
                break;
            case SCRIPT_ID_TOB_HUD_DRAW:
                color = this::colorTobHud;
                break;
            case SCRIPT_ID_RAIDS_SIDEPANEL_ENTRY_SETUP:
                color = this::colorRaidsSidePanel;
                break;
        }

        if (color != null) {
            clientThread.invokeLater(color);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // check trade screens on tick. When a player removes/adds items, the warning message gets recreated
        for (int gid : TRADE_SCREEN_GROUP_IDS) {
            Widget w = client.getWidget(gid, 0);
            if (w != null) {
                showTradeWarning(gid);
            }
        }
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event) {
        if (!config.notifyOnJoin()) {
            return;
        }
        String rsn = Text.toJagexName(event.getMember().getName());
        String local = client.getLocalPlayer().getName();
        if (rsn.equals(local)) {
            return;
        }

        alertPlayerWarning(rsn, false, AlertType.FRIENDS_CHAT);
    }

    @Subscribe
    public void onClanMemberJoined(ClanMemberJoined event) {
        if (!config.notifyOnJoin()) {
            return;
        }
        String rsn = Text.toJagexName(event.getClanMember().getName());
        String local = client.getLocalPlayer().getName();
        if (rsn.equals(local)) {
            return;
        }

        alertPlayerWarning(rsn, false, AlertType.CLAN_CHAT);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        int groupId = WidgetInfo.TO_GROUP(event.getWidgetId());
        String option = event.getMenuOption();
        MenuAction action = event.getMenuAction();

        if (TRADE_SCREEN_GROUP_IDS.contains(groupId)) {
            if (option.equals(DECLINE_MSG)) {
                clearScreenshot();
            }
        }

        // https://github.com/runelite/runelite/blob/f6c68eefc8a3c5415451bdbd6190b1f745ed5489/runelite-client/src/main/java/net/runelite/client/plugins/hiscore/HiscorePlugin.java#L178
        if ((action == MenuAction.RUNELITE || action == MenuAction.RUNELITE_PLAYER) && option.equals(INVESTIGATE)) {
            final String target;
            if (action == MenuAction.RUNELITE_PLAYER) {
                // The player id is included in the event, so we can use that to get the player name,
                // which avoids having to parse out the combat level and any icons preceding the name.
                Player player = client.getCachedPlayers()[event.getId()];
                if (player != null) {
                    target = player.getName();
                } else {
                    target = null;
                }
            } else {
                target = Text.removeTags(event.getMenuTarget());
            }

            if (target != null) {
                caseManager.get(event.getMenuTarget(), (rwCase) -> alertPlayerWarning(target, true, AlertType.NONE));
            }
        }

    }

    @Subscribe
    public void onWidgetLoaded(final WidgetLoaded event) {
        int groupId = event.getGroupId();
        Runnable task = null;
        switch (groupId) {
            case PLAYER_TRADE_CONFIRMATION_GROUP_ID:
                task = this::takeScreenshot;
                break;
            case COX_PARTY_LIST_GROUP_ID:
                task = this::colorRaidsPartyList;
                break;
            case COX_PARTY_DETAILS_GROUP_ID:
                task = this::colorRaidsParty;
                break;
            case TOB_PARTY_DETAILS_GROUP_ID:
                task = this::colorTobParty;
                break;
            case TOB_PARTY_LIST_GROUP_ID:
                task = this::colorTobPartyList;
                break;
        }

        if (task != null) {
            clientThread.invokeLater(task);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {

        ChatMessageType chatType = event.getType();
        if (chatType == ChatMessageType.TRADE) {
            String msg = event.getMessage();
            switch (msg) {
                case ACCEPTED_TRADE_MSG:
                    saveScreenshot();
                    break;
                case DECLINED_TRADE_MSG:
                    clearScreenshot();
            }
        }
    }

    @Schedule(period = 15, unit = ChronoUnit.MINUTES)
    public void refreshList() {
        caseManager.refresh(this::colorAll);
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted ce) {
        // ::rwd rw <username>
        if (developerMode && ce.getCommand().equals("rwd")) {
            caseManager.refresh(() -> {
                if (ce.getArguments().length > 0) {
                    // refresh is async, so wait a bit before adding the test rsn
                    caseManager.put(
                            String.join(" ", Arrays.copyOfRange(ce.getArguments(), 1, ce.getArguments().length)),
                            ce.getArguments()[0].toUpperCase()
                    );
                }
                colorAll();
            });
        } else if (ce.getCommand().equals("rw")) {
            final String rsn = String.join(" ", ce.getArguments());
            caseManager.get(rsn, (c) -> alertPlayerWarning(rsn, true, AlertType.NONE));
        }
    }

    private void clearScreenshot() {
        tradeImage = null;
        trader = null;
    }

    private void takeScreenshot() {
        screenshotOverlay.queueForTimestamp(image -> {
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
        if (config.screenshotTrades()) {
            executor.submit(() -> {
                BufferedImage screenshot = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = screenshot.getGraphics();
                int gameOffsetX = 0;
                int gameOffsetY = 0;

                graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
                imageCapture.takeScreenshot(screenshot, otherRsn, FOLDER_NAME, config.notifyWhenScreenshotTaken(), config.uploadTradeScreenshot());
            });
        } else {
            clearScreenshot();
        }
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
        Case rwCase = caseManager.get(trader);
        if (rwCase == null) {
            return;
        }

        String wText = tradeModified.getText();
        if (!wText.contains("WARNING")) {
            String warningMsg = String.format("<br>WARNING: %s is on %s list.", trader, rwCase.niceSourcePossessive());
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

    private void alertPlayerWarning(String rsn, boolean playerExecuted, AlertType alertType) {
        rsn = Text.toJagexName(rsn);
        Case rwCase = caseManager.get(rsn);
        ChatMessageBuilder response = new ChatMessageBuilder();
        response.append(alertType.getMessage())
                .append(ChatColorType.HIGHLIGHT)
                .append(rsn)
                .append(ChatColorType.NORMAL);

        if (rwCase == null && !playerExecuted) {
            return;
        } else if (rwCase == null) {
            response.append(" is not on any watchlist.");
        } else {
            response.append(String.format(" is on %s list for ", rwCase.niceSourcePossessive()))
                    .append(ChatColorType.HIGHLIGHT)
                    .append(rwCase.getReason());
            if (rwCase.getDate().getTime() > 0) {
                response.append(" ")
                        .append(ChatColorType.NORMAL)
                        .append("on " + rwCase.niceDate())
                        .append(".");
            } else {
                response.append(ChatColorType.NORMAL)
                        .append(".");
            }

            // Don't send a message in chat if the investigate action was a background task
            // and we've already notified about this player.
            if (!playerExecuted) {
                if (isNotifyOnCooldown(rsn)) {
                    return;
                } else {
                    lastNotified.put(rsn, Instant.now());
                }
            }
        }

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(response.build())
                .build());
    }

    boolean isNotifyOnCooldown(String rsn) {
        Instant last = lastNotified.get(rsn);
        if (last == null) {
            return false;
        }


        return last.plus(config.notificationCooldown(), ChronoUnit.MINUTES).isAfter(Instant.now());
    }

    private void colorFriendsChat() {
        Widget ccList = client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST);
        if (ccList != null) {
            illiteratePlayerWidgets(ccList);
        }
    }

    private void colorClanChat() {
        Widget clanChatList = client.getWidget(WidgetInfo.CLAN_MEMBER_LIST);
        if (clanChatList != null) {
            illiteratePlayerWidgets(clanChatList);
        }
    }

    private void colorGuestClanChat() {
        Widget guestClanChatList = client.getWidget(WidgetInfo.CLAN_MEMBER_LIST);
        if (guestClanChatList != null) {
            illiteratePlayerWidgets(guestClanChatList);
        }
    }

    private void illiteratePlayerWidgets(Widget chatWidget) {
        Widget[] players = chatWidget.getDynamicChildren();
        for (int i = 0; i < players.length; i += 3) {
            Widget player = players[i];
            if (player == null) {
                continue;
            }

            Case rwCase = caseManager.get(player.getText());
            if (rwCase == null) {
                continue;
            }

            player.setTextColor(config.playerTextColor().getRGB());
            player.revalidate();
        }
    }

    private void colorRaidsSidePanel() {
        Widget raidsList = client.getWidget(WidgetID.RAIDING_PARTY_GROUP_ID, 10);
        if (raidsList != null) {
            colorTable(raidsList, 4);
        }
    }

    private void colorRaidsParty() {
        Widget table = client.getWidget(COX_PARTY_DETAILS_GROUP_ID, 11);
        if (table != null) {
            colorTable(table, 5);
        }
    }

    private void colorRaidsPartyList() {
        Widget list = client.getWidget(COX_PARTY_LIST_GROUP_ID, 14);
        if (list != null) {
            colorList(list, 3);
        }
    }

    private void colorTobHud() {
        // top right corner widget
        Widget hud = client.getWidget(28, 9);
        if (hud == null) {
            return;
        }

        String names = hud.getText(); // user1<br>user2<br>-<br>-<br>-
        if (!Strings.isNullOrEmpty(names)) {
            List<String> newNames = new ArrayList<>();
            for (String name : names.split("<br>")) {
                if (name.equals("-")) {
                    newNames.add("-");
                    continue;
                }

                String stripped = Text.removeTags(name);
                Case rwCase = caseManager.get(stripped);
                if (rwCase == null) {
                    newNames.add(name);
                    continue;
                }

                String colored = new ChatMessageBuilder().append(config.playerTextColor(), stripped).build();
                newNames.add(colored);
            }
            hud.setText(String.join("<br>", newNames));
        }
    }

    private void colorTobParty() {
        // current party members
        Widget table = client.getWidget(TOB_PARTY_DETAILS_GROUP_ID, 26);
        if (table != null) {
            // idx = 0 whole row, 1/12/23/... = rsn cell
            colorTable(table, 11);
        }

        // get current party application list
        table = client.getWidget(TOB_PARTY_DETAILS_GROUP_ID, 41);
        if (table != null) {
            // idx = 0 whole row, 0-18 = cells,1 = name
            colorTable(table, 18);
        }
    }

    private void colorTobPartyList() {
        Widget list = client.getWidget(TOB_PARTY_LIST_GROUP_ID, 16);
        if (list != null) {
            colorList(list, 3);
        }
    }

    private void colorTable(Widget table, int inc) {
        Widget[] players = table.getDynamicChildren();
        for (int i = 1; i < players.length; i += inc) {
            Widget player = players[i];
            if (player == null) {
                continue;
            }

            Case rwCase = caseManager.get(Text.removeTags(player.getText()));
            if (rwCase == null) {
                continue;
            }

            player.setTextColor(config.playerTextColor().getRGB());
            player.setText(Text.removeTags(player.getText()));
            player.revalidate();
        }
    }

    private void colorList(Widget list, int nameIdx) {
        for (Widget row : list.getStaticChildren()) {
            Widget name = row.getChild(nameIdx);
            if (name == null) {
                continue;
            }

            if (caseManager.get(name.getText()) != null) {
                name.setTextColor(config.playerTextColor().getRGB());
                name.setText(Text.removeTags(name.getText())); // `col` is set on the name too
                name.revalidate();
            }
        }
    }

}
