package com.ttl.ui;

import com.ttl.RateTTL;
import lombok.Getter;

import java.util.Comparator;

public enum SortOrder {
    SKILL((o1, o2) -> o1.getSkill().ordinal() - o2.getSkill().ordinal()),
    LEVEL((o1, o2) -> o1.getLevel() - o2.getLevel()),
    XP((o1, o2) -> o1.getXpLeft() - o2.getXpLeft()),
    RATE((o1, o2) -> o1.getXpRate() - o2.getXpRate()),
    TTL((o1, o2) -> o1.getSecondsLeft() - o2.getSecondsLeft()),
    ;

    Comparator<RateTTL> comparator;

    SortOrder(Comparator<RateTTL> comparator) {
        this.comparator = comparator;
    }

    @Getter
    boolean asc = true;

    void toggleAsc() {
        asc = !asc;
    }
}
