package com.gauntlet;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.Counter;

public class GauntletResourceCounter extends Counter {
    private final String name;
    private final int itemId;
    private final GauntletPlugin plugin;

    public GauntletResourceCounter(ItemManager itemManager, GauntletPlugin plugin, int itemId, int count) {
        super(itemManager.getImage(itemId), plugin, count);
        this.itemId = itemId;
        this.plugin = plugin;
        this.name = itemManager.getItemComposition(itemId).getName();
    }

    @Override
    public int getCount() {
        return plugin.getItemCounts().getOrDefault(itemId, 0);
    }

    @Override
    public boolean render() {
        return this.plugin.isInGauntlet();
    }

    @Override
    public String getTooltip() {
        return name;
    }
}
