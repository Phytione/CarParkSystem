package com.example.parkingsystem.CustomItems;

import android.location.Location;

import java.util.Locale;

public class DistanceCalculator {
    public String calculateDistance(double userLatitude, double userLongitude, double otoparkLatitude, double otoparkLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(userLatitude, userLongitude, otoparkLatitude, otoparkLongitude, results);

        // results[0] mesafeyi metre cinsinden içerir, bunu kilometreye çevirelim
        double distanceInKm = results[0] / 1000.0;

        // İsteğe bağlı olarak mesafeyi bir string olarak formatlayabilirsiniz
        String formattedDistance = String.format(Locale.getDefault(), "%.2f km", distanceInKm);

        return formattedDistance;
    }
}
