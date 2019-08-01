package com.example.application1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

import java.security.Permission;

public class GPSTracker implements LocationListener {
    Context context;

    public GPSTracker(Context context2) {
        this.context = context2;
    }


    public Location getLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || ContextCompat.checkSelfPermission(this.context, "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, context);

        Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return l;
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}