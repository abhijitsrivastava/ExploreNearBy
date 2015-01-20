/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.util;

import android.location.Location;

/**
 * Created by ryaneldridge on 5/5/14.
 */
public class MathUtils {

    /** The number of half winds for boxing the compass. */
    private static final int NUMBER_OF_HALF_WINDS = 16;

    /** The Earth's radius, in kilometers. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    public static enum CompassDirection {
        N, // 337.5 thru 360 or 0 thru 22.5
        NE, // 22.5 thru 67.5
        E, // 67.5 thru 112.5
        SE, // 112.5 thru 157.5
        S, // 157.5 thru 202.5
        SW, // 202.5 thru 247.5
        W, // 247.5 thru 292.5
        NW // 292.5 thru 337.5
    }

    /**
     * Calculates {@code a mod b} in a way that respects negative values (for example,
     * {@code mod(-1, 5) == 4}, rather than {@code -1}).
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a mod b}
     */
    public static int mod(int a, int b) {
        return (a % b + b) % b;
    }

    /**
     * Calculates {@code a mod b} in a way that respects negative values (for example,
     * {@code mod(-1, 5) == 4}, rather than {@code -1}).
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a mod b}
     */
    public static float mod(float a, float b) {
        return (a % b + b) % b;
    }

    /**
     * Converts the specified heading angle into an index between 0-15 that can be used to retrieve
     * the direction name for that heading (known as "boxing the compass", down to the half-wind
     * level).
     *
     * @param heading the heading angle
     * @return the index of the direction name for the angle
     */
    public static int getHalfWindIndex(float heading) {
        float partitionSize = 360.0f / NUMBER_OF_HALF_WINDS;
        float displacedHeading = MathUtils.mod(heading + partitionSize / 2, 360.0f);
        return (int) (displacedHeading / partitionSize);
    }

    /**
     * Gets the relative bearing from one geographical coordinate to another.
     *
     * @param latitude1 the latitude of the source point
     * @param longitude1 the longitude of the source point
     * @param latitude2 the latitude of the destination point
     * @param longitude2 the longitude of the destination point
     * @return the relative bearing from point 1 to point 2, in degrees. The result is guaranteed
     *         to fall in the range 0-360
     */
    public static float getBearing(double latitude1, double longitude1, double latitude2,
                                   double longitude2) {
        latitude1 = Math.toRadians(latitude1);
        longitude1 = Math.toRadians(longitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude2 = Math.toRadians(longitude2);

        double dLon = longitude2 - longitude1;

        double y = Math.sin(dLon) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1)
                * Math.cos(latitude2) * Math.cos(dLon);

        double bearing = Math.atan2(y, x);
        return mod((float) Math.toDegrees(bearing), 360.0f);
    }

    public static float initialBearing(double latitude1, double longitude1, double latitude2, double longitude2) {
        return (float) normalizeBearing(Math.toDegrees(initialBearingInRadians(latitude1, longitude1, latitude2, longitude2)));
    }

    public static double normalizeBearing(double bearing) {
        if (Double.isNaN(bearing) || Double.isInfinite(bearing))
            return Double.NaN;
        double bearingResult = bearing % 360;
        if (bearingResult < 0)
            bearingResult += 360;
        return bearingResult;
    }

    public static double initialBearingInRadians(double latitude1, double longitude1, double latitude2, double longitude2) {
        double lat1R = Math.toRadians(latitude1);
        double lat2R = Math.toRadians(latitude2);
        double dLngR = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(dLngR) * Math.cos(lat2R);
        double b = Math.cos(lat1R) * Math.sin(lat2R) - Math.sin(lat1R) * Math.cos(lat2R)
                * Math.cos(dLngR);
        return Math.atan2(a, b);
    }

    /**
     * Gets the great circle distance in kilometers between two geographical points, using
     * the <a href="http://en.wikipedia.org/wiki/Haversine_formula">haversine formula</a>.
     *
     * @param latitude1 the latitude of the first point
     * @param longitude1 the longitude of the first point
     * @param latitude2 the latitude of the second point
     * @param longitude2 the longitude of the second point
     * @return the distance, in kilometers, between the two points
     */
    public static float getDistance(double latitude1, double longitude1, double latitude2,
                                    double longitude2) {
        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLon = Math.toRadians(longitude2 - longitude1);
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double sqrtHaversineLat = Math.sin(dLat / 2);
        double sqrtHaversineLon = Math.sin(dLon / 2);
        double a = sqrtHaversineLat * sqrtHaversineLat + sqrtHaversineLon * sqrtHaversineLon
                * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS_KM * c);
    }

    public static CompassDirection translateBearing(float bearing) {
        if ( (bearing >= 337.6 && bearing <= 360.0) || (bearing >= 0 && bearing <= 22.5) ) {
            return CompassDirection.N;
        } else if ( bearing >= 22.6 && bearing <= 67.5 ) {
            return CompassDirection.NE;
        } else if ( bearing >= 67.6 && bearing <= 112.5 ) {
            return CompassDirection.E;
        } else if ( bearing >= 112.6 && bearing <= 157.5 ) {
            return CompassDirection.SE;
        } else if ( bearing >= 157.6 && bearing <= 202.5 ) {
            return CompassDirection.S;
        } else if ( bearing >= 202.6 && bearing <=247.5 ) {
            return CompassDirection.SW;
        } else if ( bearing >= 247.6 && bearing <= 292.5 ) {
            return CompassDirection.W;
        } else if ( bearing >= 292.6 && bearing <= 337.5 ) {
            return CompassDirection.NW;
        } else {
            return null;
        }
    }

}
