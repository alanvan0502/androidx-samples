package com.example.android.walkmyandroid;

import android.location.Location;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface AppLocationServices {

    Observable<Location> getLocation(AppLocationClient locationClient);

    Observable<String> getAddress(AppLocationClient locationClient);

    Flowable<Location> getLocationUpdates(AppLocationClient locationClient);

    Flowable<String> getAddressUpdates(AppLocationClient locationClient);
}
