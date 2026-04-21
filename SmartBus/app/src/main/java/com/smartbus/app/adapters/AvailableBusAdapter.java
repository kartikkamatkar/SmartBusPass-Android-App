package com.smartbus.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.models.BusPass;

import java.util.List;
import java.util.Locale;

public class AvailableBusAdapter extends RecyclerView.Adapter<AvailableBusAdapter.ViewHolder> {

    private final List<BusPass> busList;
    private final OnBusClickListener listener;

    public interface OnBusClickListener {
        void onBusClick(BusPass bus);
    }

    public AvailableBusAdapter(List<BusPass> busList, OnBusClickListener listener) {
        this.busList = busList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusPass bus = busList.get(position);
        
        holder.tvType.setText(bus.getName());
        String[] routeParts = bus.getRoute().split(" ↔ ");
        holder.tvSource.setText(routeParts.length > 0 ? routeParts[0] : "");
        holder.tvDest.setText(routeParts.length > 1 ? routeParts[1] : "");
        holder.tvTime.setText(bus.getValidity());
        holder.tvPlate.setText(String.format(Locale.getDefault(), "MH 29 BE %d", 2000 + position));
        holder.tvFare.setText(bus.getCreatedAt());

        holder.itemView.setOnClickListener(v -> listener.onBusClick(bus));
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvSource, tvDest, tvTime, tvPlate, tvFare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_bus_card_type);
            tvSource = itemView.findViewById(R.id.tv_bus_card_source);
            tvDest = itemView.findViewById(R.id.tv_bus_card_dest);
            tvTime = itemView.findViewById(R.id.tv_bus_card_time);
            tvPlate = itemView.findViewById(R.id.tv_bus_card_plate);
            tvFare = itemView.findViewById(R.id.tv_bus_card_fare);
        }
    }
}
