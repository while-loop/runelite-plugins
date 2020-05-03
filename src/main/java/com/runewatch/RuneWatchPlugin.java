package com.runewatch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
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
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.client.util.ImageUploadStyle.CLIPBOARD;

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

    private static final String ACCEPTED_TRADE_MSG = "Accepted trade.";
    private static final String DECLINED_TRADE_MSG = "Other player declined trade.";
    private static final String DECLINE_MSG = "Decline";

    private static final Pattern TRADING_WITH_PATTERN = Pattern.compile("Trading with:<br>(.*)");

    private static final int TRADE_CONFIRMATION_GROUP_ID = 334;
    private static final int TRADE_CONFIRMATION_TRADING_WITH_ID = 30;

    private static final List<Integer> MENU_WIDGET_IDS = ImmutableList.of(
            WidgetInfo.FRIENDS_LIST.getGroupId(),
            WidgetInfo.CLAN_CHAT.getGroupId(),
            WidgetInfo.CHATBOX.getGroupId(),
            WidgetInfo.RAIDING_PARTY.getGroupId(),
            WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId(),
            WidgetInfo.IGNORE_LIST.getGroupId(),
            507 // RAIDING_RECRUITING_BOARD
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
        RuneWatchCase rwCase = getUserCase(rsn);
        ChatMessageBuilder response = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(rsn)
                .append(ChatColorType.NORMAL);

        if (rwCase == null) {
            response = response.append(" is not on RuneWatch's list.");
        } else {
            response = response
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

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        int groupId = WidgetInfo.TO_GROUP(event.getWidgetId());
        String option = event.getMenuOption();

        if (groupId != TRADE_CONFIRMATION_GROUP_ID) {
            return;
        }

        if (option.equals(DECLINE_MSG)) {
            clearScreenshot();
        }
    }

    @Subscribe
    public void onWidgetLoaded(final WidgetLoaded event) {
        if (event.getGroupId() != TRADE_CONFIRMATION_GROUP_ID) {
            return;
        }

        takeScreenshot();
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
            Widget nameWidget = client.getWidget(TRADE_CONFIRMATION_GROUP_ID, TRADE_CONFIRMATION_TRADING_WITH_ID);
            trader = "unknown";
            if (nameWidget != null) {
                Matcher m = TRADING_WITH_PATTERN.matcher(nameWidget.getText());
                if (m.matches()) {
                    trader = m.group(1);
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
            imageCapture.takeScreenshot(screenshot, otherRsn, FOLDER_NAME, true, CLIPBOARD);
        });
    }

    private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries) {
        MenuEntry[] newMenu = ObjectArrays.concat(entries, newEntry);
        int menuEntryCount = newMenu.length;
        ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
        client.setMenuEntries(newMenu);
    }

    private RuneWatchCase getUserCase(String rsn) {
        Random rnd = new Random();
        if (rnd.nextBoolean()) {
            return null;
        }

        return new RuneWatchCase(rsn, Calendar.getInstance(), Math.abs(rnd.nextInt()), "LOAN");
    }
}




















