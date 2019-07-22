/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.walkmyandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.android.walkmyandroid.Constants.REQUEST_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Location mLocation;
    private TextView mLocationTextView;
    private LocationClient mLocationClient;
    private AppLocationServices mLocationServices;
    private CompositeDisposable bag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button locButton = findViewById(R.id.button_location);
        locButton.setOnClickListener($ -> getLocation());

        mLocationTextView = findViewById(R.id.textview_location);
        mLocationServices = new AppLocationServicesImpl();
        mLocationClient = new LocationClient(this);

        bag = new CompositeDisposable();
    }

    private void getLocation() {
        bag.add(mLocationServices.getLocation(mLocationClient).map(location -> {
            if (location != null) {
                mLocationTextView.setText(getString(R.string.location_text, location.getLongitude(), location.getLatitude(), location.getTime()));
            } else {
                mLocationTextView.setText(getString(R.string.no_location));
            }
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
