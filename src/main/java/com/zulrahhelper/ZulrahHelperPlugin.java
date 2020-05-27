package com.zulrahhelper;

import com.google.inject.Provides;
import com.zulrahhelper.ui.ZulrahHelperPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Zulrah Helper",
        description = "Panel to show Zulrah rotations",
        tags = {"zulrah", "pvm"}
)
public class ZulrahHelperPlugin extends Plugin {
    static final String CONFIG_GROUP = "zulrahhelper";
    static final String DARK_MODE_KEY = "darkMode";
    static final String IMAGE_ORIENTATION_KEY = "imageOrientation";

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    @Getter
    private ZulrahHelperConfig config;

    private ZulrahHelperPanel panel;
    private NavigationButton navButton;

    private State state;

    private HotkeyListener resetPhasesHotkey;
    private HotkeyListener nextPhaseHotkey;
    private HotkeyListener[] phaseSelectionHotKeys = new HotkeyListener[3];

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

        clientToolbar.addNavigation(navButton);
        initHotkeys();
        panel.update(state);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
        keyManager.unregisterKeyListener(resetPhasesHotkey);
        keyManager.unregisterKeyListener(nextPhaseHotkey);
        for (HotkeyListener phaseSelectionHotKey : phaseSelectionHotKeys) {
            keyManager.unregisterKeyListener(phaseSelectionHotKey);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(CONFIG_GROUP)) {
            return;
        }

        if (event.getKey().equals(DARK_MODE_KEY) || event.getKey().equals(IMAGE_ORIENTATION_KEY)) {
            panel.update(state);
        }
    }

    @Provides
    ZulrahHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ZulrahHelperConfig.class);
    }

    public void reset() {
        setState(new Phase(Phase.Rotation.START, 1));
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
        nextPhaseHotkey = new HotkeyListener(() -> config.nextPhaseHotkey()) {
            @Override
            public void hotkeyPressed() {
                Phase p = state.getPhase();
                setState(new Phase(p.getRotation(), p.getNumber() + 1));
            }
        };

        resetPhasesHotkey = new HotkeyListener(() -> config.resetPhasesHotkey()) {
            @Override
            public void hotkeyPressed() {
                reset();
            }
        };

        phaseSelectionHotKeys[0] = new HotkeyListener(() -> config.phaseSelection1Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(0);
            }
        };

        phaseSelectionHotKeys[1] = new HotkeyListener(() -> config.phaseSelection2Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(1);
            }
        };

        phaseSelectionHotKeys[2] = new HotkeyListener(() -> config.phaseSelection3Hotkey()) {
            @Override
            public void hotkeyPressed() {
                selectOption(2);
            }
        };

        keyManager.registerKeyListener(nextPhaseHotkey);
        keyManager.registerKeyListener(resetPhasesHotkey);
        for (HotkeyListener phaseSelectionHotKey : phaseSelectionHotKeys) {
            keyManager.registerKeyListener(phaseSelectionHotKey);
        }
    }
}
