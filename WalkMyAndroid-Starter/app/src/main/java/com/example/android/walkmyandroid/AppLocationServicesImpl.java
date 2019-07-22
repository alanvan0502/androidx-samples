package com.example.android.walkmyandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Looper;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static com.example.android.walkmyandroid.Constants.REQUEST_LOCATION_PERMISSION;

public class AppLocationServicesImpl implements AppLocationServices {

    @SuppressLint("MissingPermission")
    @Override
    public Observable<Location> getLocation(AppLocationClient locationClient) {
        return Observable.create(emitter -> {
            OnSuccessListener<Location> listener = location -> {
                if (!emitter.isDisposed()) {
                    emitter.onNext(location);
                }
            };
            checkAndRequestPermissions(locationClient, () -> locationClient.getFusedLocationClient().getLastLocation().addOnSuccessListener(listener));
        });
    }

    private void checkAndRequestPermissions(AppLocationClient locationClient, Runnable onGranted) {
        if (ActivityCompat.checkSelfPermission(locationClient.getActivityRef().get(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(locationClient.getActivityRef().get(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            onGranted.run();
        }
    }

    @Override
    public Observable<String> getAddress(AppLocationClient locationClient) {
        return getLocation(locationClient).map(location -> getAddressString(locationClient, location));
    }

    private String getAddressString(AppLocationClient locationClient, Location location) {
        Activity activity = locationClient.getActivityRef().get();
        String resultMessage = "";
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = locationClient.getGeoCoder().getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            resultMessage = activity.getString(R.string.service_unvailable);
        } catch (IllegalArgumentException e) {
            resultMessage = activity.getString(R.string.invalid_lat_long);
        }
        if (addresses.size() == 0 && resultMessage.isEmpty()) {
            resultMessage = activity.getString(R.string.no_address_found);
        } else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n", addressParts);
        }
        return resultMessage;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Flowable<Location> getLocationUpdates(AppLocationClient locationClient) {
        return Flowable.create(emitter -> {

            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (!emitter.isCancelled()) {
                        emitter.onNext(locationResult.getLastLocation());
                    }
                }
            };

            FusedLocationProviderClient client = locationClient.getFusedLocationClient();
            LocationRequest request = locationClient.getLocationRequest();

            checkAndRequestPermissions(locationClient, () ->
                    client.requestLocationUpdates(request, callback, Looper.getMainLooper()));

            emitter.setCancellable(() -> client.removeLocationUpdates(callback));

        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<String> getAddressUpdates(AppLocationClient locationClient) {
        return getLocationUpdates(locationClient).map(location -> getAddressString(locationClient, location));
    }
}
