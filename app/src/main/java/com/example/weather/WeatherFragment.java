package com.example.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.weather.adapters.ForecastAdapter;
import com.example.weather.current.CurrentWeatherModel;
import com.example.weather.databinding.FragmentWeatherBinding;
import com.example.weather.forecast.ForecastResponseModel;
import com.example.weather.permissions.LocationPermission;
import com.example.weather.prefs.WeatherPreferance;
import com.example.weather.utils.Constants;
import com.example.weather.viewmodels.WeatherViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WeatherFragment extends Fragment {
    private WeatherPreferance preferance;
    private FusedLocationProviderClient providerClient;
    private WeatherViewModel viewModel;
    private FragmentWeatherBinding binding;
    private String unitSymbol = "C";
    private ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            detectUserLocation();
        } else {
            //show a dialog and explain user why you need this permission
        }
    });

    @SuppressLint("MissingPermission")
    private void detectUserLocation() {
        providerClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) return;
                    viewModel.setLocation(location);
                    viewModel.loadData();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.e("WeatherApp", "lat: "+latitude+",lon: "+longitude);
                });
    }

    public WeatherFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weather_menu, menu);
        final SearchView searchView = (SearchView) menu.
                findItem(R.id.item_search)
                .getActionView();
        searchView.setQueryHint("Search a city");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setCity(query);
                viewModel.loadData();
                searchView.setQuery(null, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_myLocation) {
            viewModel.setCity(null);
            viewModel.loadData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        providerClient = LocationServices.getFusedLocationProviderClient(getActivity());
        preferance = new WeatherPreferance(getActivity());
        binding.tempUnitSwitch.setChecked(preferance.getTempStatus());
        viewModel.setUnit(preferance.getTempStatus());
        unitSymbol = preferance.getTempStatus() ? "F" : "C";
        final ForecastAdapter adapter = new ForecastAdapter();
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);
        binding.forecastRV.setLayoutManager(llm);
        binding.forecastRV.setAdapter(adapter);
        if (LocationPermission.isLocatonPermissionGranted(getActivity())) {
            detectUserLocation();
        }else {
            LocationPermission.requestLocationPermission(launcher);
        }

        viewModel.loadData();
        viewModel.getCurrentLiveData().observe(getViewLifecycleOwner(), currentWeatherModel -> {
            final String iconUrl = Constants.ICON_PREFIX+currentWeatherModel.getWeather().get(0).getIcon()+Constants.ICON_SUFFIX;
            Picasso.get().load(iconUrl)
                    .fit()
                    .into(binding.iconIV);
            binding.temperatureTV.setText(String.format("%.0f\u00B0%S", currentWeatherModel.getMain().getTemp(), unitSymbol));
            binding.dateTV.setText(
                    new SimpleDateFormat("MMM dd, yyyy").format(new Date(currentWeatherModel.getDt() * 1000L))
            );
            binding.countryTV.setText(currentWeatherModel.getName()+", "+currentWeatherModel.getSys().getCountry());
            binding.feelsLikeTV.setText(String.format("Feels Like : %.0f\u00B0%S",currentWeatherModel.getMain().getFeelsLike(), unitSymbol));
            binding.humidityTV.setText(currentWeatherModel.getMain().getHumidity()+"%");
            binding.pressureTV.setText(currentWeatherModel.getMain().getPressure()+"hPa");
            binding.minmaxtempTV.setText(String.format("Max: %.0f\u00B0%S Min: %.0f\u00B0%S", currentWeatherModel.getMain().getTempMax(),unitSymbol,currentWeatherModel.getMain().getTempMin(), unitSymbol));
            binding.weatherConditionTV.setText(currentWeatherModel.getWeather().get(0).getDescription().toUpperCase(Locale.ROOT));
            binding.windTV.setText(String.format("%.0f km/h", currentWeatherModel.getWind().getSpeed()));
            binding.sunriseTV.setText(new SimpleDateFormat("hh:mm a").format(new Date(currentWeatherModel.getSys().getSunrise() * 1000L)));
            binding.sunsetTV.setText(new SimpleDateFormat("hh:mm a").format(new Date(currentWeatherModel.getSys().getSunset() * 1000L)));
            binding.windDirectionTV.setText(String.format("%d\u00B0",currentWeatherModel.getWind().getDeg()));
            binding.visibilityTV.setText(String.format("%dkm",currentWeatherModel.getVisibility()/1000));
        });

        viewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        });
        viewModel.getForecastLiveData().observe(getViewLifecycleOwner(), forecastResponseModel -> {
            adapter.submitList(forecastResponseModel.getList());
        });

        binding.tempUnitSwitch.setOnCheckedChangeListener((compoundButton, isChekced) -> {
            preferance.setTempStatus(isChekced);
            viewModel.setUnit(isChekced);
            unitSymbol = isChekced ? "F" : "C";
            viewModel.loadData();
        });
        return binding.getRoot();
    }
}