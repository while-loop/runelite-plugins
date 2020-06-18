package com.runewatch;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@AllArgsConstructor
public class Case {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // "Zezima"
    @SerializedName("accused_rsn")
    private String rsn;

    // "2019-03-10 13:35:34" GMT
    @SerializedName("published_date")
    private Date date;

    // "a0a6200"
    @SerializedName("short_code")
    private String code;

    // "Accused Of Stealing Borrowed Items"
    private String reason;

    // "4"
    @SerializedName("evidence_rating")
    private String rating;

    public String niceDate() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMAT);
    }
}
