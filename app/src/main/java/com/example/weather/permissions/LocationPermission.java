package com.example.weather.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class LocationPermission {
    public static boolean isLocatonPermissionGranted(Context context){
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestLocationPermission(ActivityResultLauncher<String> launcher){
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
