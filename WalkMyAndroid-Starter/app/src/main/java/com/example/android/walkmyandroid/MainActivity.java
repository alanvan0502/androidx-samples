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

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.android.walkmyandroid.Constants.REQUEST_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mLocationTextView;
    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;

    private Button mLocationButton;
    private CompositeDisposable bag;
    private Disposable locationUpdateDisposable;
    private Disposable firstLocationDisposable;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationTextView = findViewById(R.id.textview_location);

        mAndroidImageView = findViewById(R.id.imageview_android);
        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate);

        mRotateAnim.setTarget(mAndroidImageView);

        bag = new CompositeDisposable();

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.setLocationServices(new AppLocationServicesImpl());
        mainViewModel.setLocationClient(new AppLocationClient(this));

        mLocationButton = findViewById(R.id.button_location);
        mLocationButton.setOnClickListener($ -> {
            if (!mainViewModel.isTrackingLocation()) {
                startTrackingLocation();
            } else {
                stopTrackingLocation();
            }
        });
    }

    private void getLocation() {
        bag.add(mainViewModel.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    if (location != null) {
                        mLocationTextView.setText(getString(R.string.location_text, location.getLongitude(), location.getLatitude(), location.getTime()));
                    } else {
                        mLocationTextView.setText(getString(R.string.no_location));
                    }
                }));
    }

    private void getAddress() {
        mLocationTextView.setText(getString(R.string.address_text, R.string.loading, System.currentTimeMillis()));
        bag.add(mainViewModel.getAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> mLocationTextView.setText(getString(R.string.address_text, address, System.currentTimeMillis()))));
    }

    private void startTrackingLocation() {
        mRotateAnim.start();
        mainViewModel.setTrackingLocation(true);
        mLocationButton.setText(getString(R.string.stop_tracking_loc));

        firstLocationDisposable = mainViewModel.getLocationUpdate().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).take(1).subscribe(location -> mainViewModel.setOriginalLocation(location), error -> {
                    Log.e("Error", error.getMessage());
                });

        locationUpdateDisposable = mainViewModel.getLocationUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .skip(1)
                .subscribe(
                        location -> {
                            if (location != null) {
                                Float distance = mainViewModel.getDistanceWalked(location);
                                mLocationTextView.setText(getString(R.string.distance_text, distance, location.getTime()));
                            } else {
                                mLocationTextView.setText(getString(R.string.no_location));
                            }
                        },
                        error -> Log.e("Error", error.getMessage()));
    }

    private void stopTrackingLocation() {
        if (mainViewModel.isTrackingLocation()) {
            mainViewModel.setTrackingLocation(false);
            mLocationButton.setText(getString(R.string.start_tracking_loc));
            mLocationTextView.setText(getString(R.string.distance_text, 0, System.currentTimeMillis()));
            locationUpdateDisposable.dispose();
            firstLocationDisposable.dispose();
            mRotateAnim.end();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bag.isDisposed()) {
            bag.dispose();
        }
        if (!locationUpdateDisposable.isDisposed()) {
            locationUpdateDisposable.dispose();
        }
        if (!firstLocationDisposable.isDisposed()) {
            firstLocationDisposable.dispose();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                startTrackingLocation();
            } else {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
