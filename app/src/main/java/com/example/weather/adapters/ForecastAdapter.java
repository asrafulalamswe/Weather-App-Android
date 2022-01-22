package com.example.weather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.databinding.RowForecastBinding;
import com.example.weather.forecast.ListItem;
import com.example.weather.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private List<ListItem> forecastItems = new ArrayList<>();


    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RowForecastBinding binding = RowForecastBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ForecastViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final ListItem item = forecastItems.get(position);
        holder.bind(item);
    }
    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public void submitList(List<ListItem> items) {
        forecastItems = items;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        RowForecastBinding binding ;
        public ForecastViewHolder(RowForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ListItem item){
            binding.rowDatetimeTV.setText(
                    new SimpleDateFormat("EEE hh:mm a")
                            .format(new Date(item.getDt() * 1000L))
            );

            final String iconUrl = Constants.ICON_PREFIX+
                    item.getWeather().get(0).getIcon()+ Constants.ICON_SUFFIX;
            Picasso.get().load(iconUrl).into(binding.rowIconIV);

            binding.rowMaxminTV.setText(
                    String.format("%.0f\u00B0/%.0f\u00B0",
                            item.getMain().getTempMax(),
                            item.getMain().getTempMin()));
        }
    }
}
