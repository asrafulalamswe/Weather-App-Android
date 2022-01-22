package com.example.weather.viewmodels;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.current.CurrentWeatherModel;
import com.example.weather.forecast.ForecastResponseModel;
import com.example.weather.network.WeatherService;
import com.example.weather.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private Location location;
    private MutableLiveData<CurrentWeatherModel> currentLiveData = new MutableLiveData<>();
    private MutableLiveData<ForecastResponseModel> forecastLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private String unit = Constants.TEMP_UNIT_CELSIUS;
    private String city = "dhaka";
    public void loadData(){
        fetchCurrentData();
        fetchForecastData();
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setUnit(boolean isChecked){
        unit = isChecked ? Constants.TEMP_UNIT_FARENHEIT :
                Constants.TEMP_UNIT_CELSIUS;
    }
    private void fetchCurrentData(){
//        Log.e("latitude", "fetchCurrentData: "+location.getLatitude());
        final String endUrl =  city == null ?
                String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(), location.getLongitude(), unit, Constants.WEATHER_API_KEY) :
                String.format("weather?q=%s&units=%s&appid=%s",
                        city, unit, Constants.WEATHER_API_KEY);
        WeatherService.getService().getCurrentData(endUrl).enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if (response.code()==200){
                    currentLiveData.postValue(response.body());
                } else if (response.code() == 404){
                    errorMessageLiveData.postValue(response.message());
                }
            }
            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {
                Log.e("weather_test", ""+t );
            }
        });
    }
    private void fetchForecastData(){
        final String endUrl = city == null ?
                String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",
                        location.getLatitude(), location.getLongitude(), unit, Constants.WEATHER_API_KEY) :
                String.format("forecast?q=%s&units=%s&appid=%s",
                        city, unit, Constants.WEATHER_API_KEY);
        WeatherService.getService().getForecastData(endUrl).enqueue(new Callback<ForecastResponseModel>() {
            @Override
            public void onResponse(Call<ForecastResponseModel> call, Response<ForecastResponseModel> response) {
                if (response.code()==200){
                    forecastLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponseModel> call, Throwable t) {
                Log.e("weather_test", ""+t.getCause() );
            }
        });
    }


    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public MutableLiveData<CurrentWeatherModel> getCurrentLiveData() {
        return currentLiveData;
    }
    public MutableLiveData<ForecastResponseModel> getForecastLiveData() {
        return forecastLiveData;
    }



}
