package com.example.android.walkmyandroid;

import android.app.Activity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

public class LocationClient {

    private WeakReference<Activity> activityRef;

    public LocationClient(Activity activity) {
        activityRef = new WeakReference<>(activity);
    }

    public FusedLocationProviderClient getLocationClient() {
        return LocationServices.getFusedLocationProviderClient(activityRef.get());
    }

    public WeakReference<Activity> getActivityRef() {
        return activityRef;
    }
}
