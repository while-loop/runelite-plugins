package com.zulrahhelper;

/**
 * Orientation of the phase images in Cardinal directions
 */
public enum ImageOrientation {
    SOUTH,
    NORTH,
    EAST,
    WEST;

    public double getRotation() {
        switch (this) {
            case NORTH:
                return Math.PI;
            case EAST:
                return Math.PI / 2;
            case WEST:
                return Math.PI * 1.5f;
            case SOUTH:
            default:
                return 0.0;
        }
    }
}