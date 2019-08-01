package com.example.application1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class GPSUtils implements LocationListener {
    private Context context;

    public GPSUtils(Context context) {
        this.context = context;
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Enable location permission", Toast.LENGTH_SHORT).show();

            if (isGPSEnabled) {
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    return location;
                } else {
                    Toast.makeText(context, "Please enable location service.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please enable location service.", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
        return null;
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
