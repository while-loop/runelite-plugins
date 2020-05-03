package com.runewatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.client.util.QuantityFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Data
@AllArgsConstructor
public class RuneWatchCase {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    private String username;
    private Calendar incidentDate;
    private int value;
    private String type;

    public String niceDate() {
        return DATE_FORMAT.format(incidentDate.getTime());
    }

    public String niceValue() {
        return QuantityFormatter.quantityToRSDecimalStack(value);
    }
}
