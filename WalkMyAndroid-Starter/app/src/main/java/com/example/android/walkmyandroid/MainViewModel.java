package com.example.android.walkmyandroid;

import android.location.Location;

import androidx.lifecycle.ViewModel;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class MainViewModel extends ViewModel {

    private AppLocationClient locationClient;
    private AppLocationServices locationServices;
    private boolean trackingLocation = false;
    private Location originalLocation;

    void setLocationClient(AppLocationClient locationClient) {
        this.locationClient = locationClient;
    }

    void setLocationServices(AppLocationServices locationServices) {
        this.locationServices = locationServices;
    }

    void setOriginalLocation(Location location) {
        originalLocation = location;
    }

    Float getDistanceWalked(Location newLocation) {
        return originalLocation.distanceTo(newLocation);
    }

    Observable<Location> getLocation() {
        return locationServices.getLocation(locationClient);
    }

    Observable<String> getAddress() {
        return locationServices.getAddress(locationClient);
    }

    Flowable<Location> getLocationUpdate() {
        return locationServices.getLocationUpdates(locationClient);
    }

    Flowable<String> getAddressUpdate() {
        return locationServices.getAddressUpdates(locationClient);
    }

    boolean isTrackingLocation() {
        return trackingLocation;
    }

    void setTrackingLocation(boolean track) {
        trackingLocation = track;
    }

    public Flowable<String> getAddressUpdates() {
        return locationServices.getAddressUpdates(locationClient);
    }
}
