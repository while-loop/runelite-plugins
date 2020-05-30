package com.ttl;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Skill;
import net.runelite.client.util.QuantityFormatter;

@Data
@AllArgsConstructor
public class RateTTL {
    Skill skill;
    int level;
    int xpLeft;
    int xpRate;
    int secondsLeft;
    RateMethod info;

    public String ttl() {
        if (secondsLeft < 0) {
            return "\u221e";
        }

        long durationHours = secondsLeft / (60 * 60);
        long durationMinutes = (secondsLeft % (60 * 60)) / 60;
        long durationSeconds = secondsLeft % 60;
        if (durationHours > 0) {
            return String.format("%02d:%02d:%02d", durationHours, durationMinutes, durationSeconds);
        }

        // Minutes and seconds will always be present
        return String.format("%02d:%02d", durationMinutes, durationSeconds);
    }

    public String toolTip() {
        return String.format("<html>Lvl %d<br>%s xp/hr<br>%s</html>", info.getLevel(), QuantityFormatter.quantityToRSDecimalStack(info.getRate()), info.getMethod());
    }
}
