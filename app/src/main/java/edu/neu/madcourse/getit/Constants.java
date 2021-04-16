package edu.neu.madcourse.getit;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */

final class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 1609 * 3; // 3 mile, 1.6*3 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    static final HashMap<String, LatLng> STOP_N_SHOP_LANDMARKS = new HashMap<>();

    static {
        // Stop n Shop store in Tremont Street
        STOP_N_SHOP_LANDMARKS.put("Stop n Shop, Tremont Street", new LatLng(42.333238, -71.1044184));
//        STOP_N_SHOP_LANDMARKS.put("Stop n Shop, Tremont Street", new LatLng(42.333, -71.104));

        // Stop n Shop store in Jamaica Street
//        STOP_N_SHOP_LANDMARKS.put("Stop n Shop, Jamaica Street", new LatLng(42.324, -71.103));
        STOP_N_SHOP_LANDMARKS.put("Stop n Shop, Jamaica Street", new LatLng(42.32402, -71.10321));

        STOP_N_SHOP_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
        STOP_N_SHOP_LANDMARKS.put("GOOGLE", new LatLng(37.422611, -122.0840577));
    }
}
