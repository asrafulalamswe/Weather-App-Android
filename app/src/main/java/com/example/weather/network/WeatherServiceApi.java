package com.example.weather.network;

import com.example.weather.current.CurrentWeatherModel;
import com.example.weather.forecast.ForecastResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {
    @GET
    Call<CurrentWeatherModel> getCurrentData(@Url String endUrl);
    @GET
    Call<ForecastResponseModel> getForecastData(@Url String endUrl);
}
