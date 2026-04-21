package com.smartbus.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.PassViewHolder> {

    public interface OnPassClickListener {
        void onPassClick(BusPass pass);
    }

    private final List<BusPass> passList;
    private final OnPassClickListener clickListener;
    private final DBHelper dbHelper;

    public BusAdapter(List<BusPass> passList, OnPassClickListener clickListener, DBHelper dbHelper) {
        this.passList = passList;
        this.clickListener = clickListener;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public PassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pass, parent, false);
        return new PassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassViewHolder holder, int position) {
        BusPass pass = passList.get(position);
        holder.tvName.setText(pass.getName());
        holder.tvRoute.setText(pass.getRoute());
        holder.tvValidity.setText(pass.getValidity());

        boolean isExpired = dbHelper.checkExpiry(pass.getValidity());
        holder.tvStatus.setText(isExpired ? holder.itemView.getContext().getString(R.string.pass_status_expired)
                : holder.itemView.getContext().getString(R.string.pass_status_valid));
        int colorRes = isExpired ? R.color.status_expired : R.color.status_valid;
        holder.tvStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        holder.itemView.setOnClickListener(v -> clickListener.onPassClick(pass));
    }

    @Override
    public int getItemCount() {
        return passList.size();
    }

    public static class PassViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvRoute;
        TextView tvValidity;
        TextView tvStatus;

        public PassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_pass_item_name);
            tvRoute = itemView.findViewById(R.id.tv_pass_item_route);
            tvValidity = itemView.findViewById(R.id.tv_pass_item_validity);
            tvStatus = itemView.findViewById(R.id.tv_pass_item_status);
        }
    }
}
