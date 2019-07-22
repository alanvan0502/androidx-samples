package com.example.android.walkmyandroid;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;

public interface AppLocationServices {

    Observable<Location> getLocation(LocationClient locationClient);

}
