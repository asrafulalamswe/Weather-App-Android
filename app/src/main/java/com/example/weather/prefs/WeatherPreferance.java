package com.example.weather.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherPreferance {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public WeatherPreferance(Context context){
        preferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public void setTempStatus(boolean status){
        editor.putBoolean("status", status);
        editor.commit();
    }
    public boolean getTempStatus(){
        return preferences.getBoolean("status", false);
    }
}
