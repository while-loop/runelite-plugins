package com.zulrahhelper;

import com.google.inject.Provides;
import com.zulrahhelper.ui.ZulrahHelperPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Zulrah Helper",
        description = "Panel to show Zulrah rotations",
        tags = {"zulrah", "pvm"}
)
public class ZulrahHelperPlugin extends Plugin {
    static final String CONFIG_GROUP = "zulrahhelper";
    static final String SECTION_IMAGE_OPTIONS = "Image Options";
    static final String SECTION_HOTKEYS = "Hotkeys";
    static final String SECTION_MISC = "Miscellaneous";

    static final String DARK_MODE_KEY = "darkMode";
    static final String DISPLAY_PRAYER_KEY = "displayPrayer";
    static final String DISPLAY_ATTACK_KEY = "displayAttack";
    static final String IMAGE_ORIENTATION_KEY = "imageOrientation";
    static final String AUTO_HIDE_KEY = "autoHide";

    private static final int ZULANDRA_REGION_ID = 8751;
    private static final int ZULRAH_SPAWN_REGION_ID = 9007;
    private static final int ZULRAH_REGION_ID = 9008;

    private static final List<Integer> REGION_IDS = Arrays.asList(
            ZULANDRA_REGION_ID,
            ZULRAH_SPAWN_REGION_ID,
            ZULRAH_REGION_ID
    );

    private static final List<Integer> INSTANCE_IDS = Arrays.asList(
            ZULRAH_SPAWN_REGION_ID,
            ZULRAH_REGION_ID
    );

    private static final List<String> OPTION_KEYS = Arrays.asList(
            DARK_MODE_KEY,
            DISPLAY_PRAYER_KEY,
            DISPLAY_ATTACK_KEY,
            IMAGE_ORIENTATION_KEY
    );

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private Client client;

    @Inject
    @Getter
    private ZulrahHelperConfig config;

    private ZulrahHelperPanel panel;
    private NavigationButton navButton;

    private State state;
    private HotkeyListener[] hotkeys = new HotkeyListener[5];

    private int lastRegionId = -1;
    private boolean hotkeysEnabled = false;
    private boolean panelEnabled = false;

    @Override
    protected void startUp() throws Exception {
        panel = new ZulrahHelperPanel(this);
        state = new State();
        navButton = NavigationButton.builder()
                .tooltip("Zulrah Helper")
                .icon(ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png"))
                .priority(70)
                .panel(panel)
                .build();

        initHotkeys();
        togglePanel(!config.autoHide(), false);
        panel.update(state);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
        if (hotkeysEnabled) {
            toggleHotkeys();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(CONFIG_GROUP)) {
            return;
        }

        if (OPTION_KEYS.contains(event.getKey())) {
            panel.update(state);
        }

        if (event.getKey().equals(AUTO_HIDE_KEY)) {
            boolean atZul = REGION_IDS.contains(getRegionId());
            togglePanel(atZul || !config.autoHide(), false);
        }
    }

    @Provides
    ZulrahHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ZulrahHelperConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        checkRegion();
    }

    private void checkRegion() {
        final int regionId = getRegionId();
        if (hotkeysEnabled && !INSTANCE_IDS.contains(regionId)) {
            toggleHotkeys();
        }

        if (!REGION_IDS.contains(regionId)) {
            if (config.autoHide()) {
                togglePanel(false, false);
            }

            lastRegionId = regionId;
            return;
        }

        // in a zulrah region
        if (!REGION_IDS.contains(lastRegionId)) {
            // just got to zulandra region, show panel
            if (!panelEnabled && config.autoHide()) {
                togglePanel(true, true);
            }
        }

        if (INSTANCE_IDS.contains(regionId) && !hotkeysEnabled) {
            toggleHotkeys();
        }
        lastRegionId = regionId;
    }

    private int getRegionId() {
        Player player = client.getLocalPlayer();
        if (player == null) {
            return -1;
        }

        return WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
    }

    public void reset() {
        setState(State.START_PHASE);
    }

    private void selectOption(int choice) {
        List<List<Phase>> tree = state.buildTree();
        if (tree == null || tree.size() <= 0) {
            log.error("no state tree found");
            return;
        }

        List<Phase> choices = tree.get(tree.size() - 1);
        if (choice >= choices.size()) {
            log.error("trying to select nonexistent phase: {} {}", choice, choices.size());
            return;
        }

        setState(choices.get(choice));
    }

    public void setState(Phase phase) {
        state.setPhase(phase);
        log.debug("setting state: " + state.toString());
        panel.update(state);
    }

    private void initHotkeys() {


        hotkeys[0] = new HotkeyListener(() -> config.phaseSelection1Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(0);
            }
        };

        hotkeys[1] = new HotkeyListener(() -> config.phaseSelection2Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(1);
            }
        };

        hotkeys[2] = new HotkeyListener(() -> config.phaseSelection3Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(2);
            }
        };

        hotkeys[3] = new HotkeyListener(() -> config.nextPhaseHotkey()) {
            @Override
            public void hotkeyPressed() {
                setState(state.getRotationTree().get(state.getNumber()).get(0));
            }
        };

        hotkeys[4] = new HotkeyListener(() -> config.resetPhasesHotkey()) {
            @Override
            public void hotkeyPressed() {
                reset();
            }
        };
    }

    private void toggleHotkeys() {
        for (HotkeyListener hotkey : hotkeys) {
            if (hotkeysEnabled) {
                keyManager.unregisterKeyListener(hotkey);
            } else {
                keyManager.registerKeyListener(hotkey);
            }
        }
        hotkeysEnabled = !hotkeysEnabled;
    }

    private void togglePanel(boolean enable, boolean show) {
        panelEnabled = enable;
        if (enable) {
            clientToolbar.addNavigation(navButton);
            if (show) {
                SwingUtilities.invokeLater(() -> {
					clientToolbar.openPanel(navButton);
                });
            }
        } else {
            clientToolbar.removeNavigation(navButton);
        }
    }
}
