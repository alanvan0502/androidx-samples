package com.example.android.walkmyandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.Observable;

import static com.example.android.walkmyandroid.Constants.REQUEST_LOCATION_PERMISSION;

public class AppLocationServicesImpl implements AppLocationServices {

    @Override
    public Observable<Location> getLocation(LocationClient locationClient) {
        return Observable.create(emitter -> {
            OnSuccessListener<Location> listener = location -> {
                if (!emitter.isDisposed()) {
                    emitter.onNext(location);
                }
            };

            if (ActivityCompat.checkSelfPermission(locationClient.getActivityRef().get(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(locationClient.getActivityRef().get(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            }
            locationClient.getLocationClient().getLastLocation().addOnSuccessListener(listener);
        });
    }

}
