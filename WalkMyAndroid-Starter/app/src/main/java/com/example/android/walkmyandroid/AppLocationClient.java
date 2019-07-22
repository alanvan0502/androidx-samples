package com.example.android.walkmyandroid;

import android.app.Activity;
import android.location.Geocoder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.Locale;

import static com.example.android.walkmyandroid.Constants.PRIORITY;
import static com.example.android.walkmyandroid.Constants.REQUEST_FASTEST_INTERVAL;
import static com.example.android.walkmyandroid.Constants.REQUEST_INTERVAL;

public class AppLocationClient {

    private WeakReference<Activity> activityRef;
    private Geocoder geoCoder;
    private LocationRequest locationRequest;

    public AppLocationClient(Activity activity) {
        activityRef = new WeakReference<>(activity);
        geoCoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());

        locationRequest = new LocationRequest();
        locationRequest.setInterval(REQUEST_INTERVAL);
        locationRequest.setFastestInterval(REQUEST_FASTEST_INTERVAL);
        locationRequest.setPriority(PRIORITY);
    }

    public FusedLocationProviderClient getFusedLocationClient() {
        return LocationServices.getFusedLocationProviderClient(activityRef.get());
    }

    public WeakReference<Activity> getActivityRef() {
        return activityRef;
    }

    public Geocoder getGeoCoder() {
        return geoCoder;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
