package com.gauntlet;

import lombok.Getter;
import net.runelite.api.ItemID;

import java.util.regex.Pattern;

@Getter
public enum GauntletResource {
    RAW_PADDLEFISH("Raw paddlefish", ItemID.RAW_PADDLEFISH, "You manage to catch a fish\\.", true, true),

    CRYSTAL_SHARDS("Crystal shards", ItemID.CRYSTAL_SHARDS, "You find (\\d+) crystal shards.", false),
    CORRUPTED_SHARDS("Corrupted shards", ItemID.CORRUPTED_SHARDS, "You find (\\d+) corrupted shards.", true),

    CRYSTAL_ORE("Crystal ore", ItemID.CRYSTAL_ORE, "You manage to mine some ore.", false),
    CORRUPTED_ORE("Corrupted ore", ItemID.CORRUPTED_ORE, "You manage to mine some ore.", true),

    PHREN_BARK("Phren bark", ItemID.PHREN_BARK_23878, "You get some bark.", false),
    CORRUPTED_PHREN_BARK("Phren bark", ItemID.PHREN_BARK, "You get some bark.", true),

    LINUM_TIRINUM("Linum tirinum", ItemID.LINUM_TIRINUM_23876, "You pick some fibre from the plant.", false),
    CORRUPTED_LINUM_TIRINUM("Linum tirinum", ItemID.LINUM_TIRINUM, "You pick some fibre from the plant.", true),

    GRYM_LEAF("Grym leaf", ItemID.GRYM_LEAF_23875, "You pick a herb from the roots.", false),
    CORRUPTED_GRYM_LEAF("Grym leaf", ItemID.GRYM_LEAF, "You pick a herb from the roots.", true),
    ;

    private final Pattern pattern;
    private final int itemId;
    private final boolean corrupted;
    private final boolean shared;
    private final String name;

    GauntletResource(String name, int itemId, String pattern, boolean corrupted) {
        this(name, itemId, pattern, corrupted, false);
    }

    GauntletResource(String name, int itemId, String pattern, boolean corrupted, boolean shared) {
        this.name = name;
        this.itemId = itemId;
        this.pattern = Pattern.compile(pattern);
        this.corrupted = corrupted;
        this.shared = shared;
    }

    GauntletResource(String name, int itemId) {
        this(name, itemId, false);
    }

    GauntletResource(String name, int itemId, boolean corrupted) {
        this.name = name;
        this.itemId = itemId;
        this.corrupted = corrupted;
        this.pattern = null;
        this.shared = false;
    }

    static int FROM_DROP(String itemName, boolean corrupted) {
        for (GauntletResource r : values()) {
            if (r.name.equals(itemName) && (r.isShared() || r.corrupted == corrupted)) {
                return r.itemId;
            }
        }

        return 0;
    }
}
