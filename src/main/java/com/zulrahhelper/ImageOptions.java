package com.zulrahhelper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ImageOptions {
    private boolean attackIcons = false;
    private boolean prayerIcons = false;
    private boolean darkMode = true;
    private ImageOrientation orientation = ImageOrientation.SOUTH;

    public ImageOptions() {}

    public ImageOptions(ZulrahHelperConfig config) {
        attackIcons = config.displayAttackIcons();
        prayerIcons = config.displayPrayerIcons();
        darkMode = config.darkMode();
        orientation = config.imageOrientation();
    }
}
