package com.smartbus.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.models.BusPass;

import java.util.List;

public class AvailablePassAdapter extends RecyclerView.Adapter<AvailablePassAdapter.ViewHolder> {

    public interface OnBuyClickListener {
        void onBuyClick(BusPass pass);
    }

    private final List<BusPass> passList;
    private final OnBuyClickListener listener;
    private int expandedPosition = -1;

    public AvailablePassAdapter(List<BusPass> passList, OnBuyClickListener listener) {
        this.passList = passList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pass_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusPass pass = passList.get(position);
        holder.tvName.setText(pass.getName());
        
        // Price logic based on name
        String price = "₹50";
        String benefits = "• One day validity";
        if (pass.getName().contains("Weekly")) {
            price = "₹250";
            benefits = "• 7 days validity\n• Best for office travel";
        } else if (pass.getName().contains("Monthly")) {
            price = "₹800";
            benefits = "• 30 days validity\n• Students save 40%";
        }
        
        holder.tvPrice.setText(price + (pass.getName().contains("Daily") ? " / Day" : (pass.getName().contains("Weekly") ? " / Week" : " / Month")));
        holder.tvBenefits.setText(benefits);

        // Expand/Collapse logic
        final boolean isExpanded = position == expandedPosition;
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivExpand.setRotation(isExpanded ? 180 : 0);
        
        holder.layoutHeader.setOnClickListener(v -> {
            int prev = expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(prev);
            notifyItemChanged(position);
        });

        holder.btnSelect.setOnClickListener(v -> listener.onBuyClick(pass));
    }

    @Override
    public int getItemCount() {
        return passList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvBenefits;
        ImageView ivIcon, ivExpand;
        View layoutHeader, layoutDetails, btnSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_pass_name);
            tvPrice = itemView.findViewById(R.id.tv_pass_price);
            tvBenefits = itemView.findViewById(R.id.tv_pass_benefits);
            ivIcon = itemView.findViewById(R.id.iv_pass_icon);
            ivExpand = itemView.findViewById(R.id.btn_expand);
            layoutHeader = itemView.findViewById(R.id.layout_header);
            layoutDetails = itemView.findViewById(R.id.layout_expandable_details);
            btnSelect = itemView.findViewById(R.id.btn_select_pass);
        }
    }
}
