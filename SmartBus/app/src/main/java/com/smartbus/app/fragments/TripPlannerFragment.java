package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.smartbus.app.R;

import java.util.Locale;

public class TripPlannerFragment extends Fragment {

    private AutoCompleteTextView etSource, etDest;
    private LinearLayout containerPlans;
    private TextView tvPlansTitle;

    private static final String[] LOCATIONS = {
        "Wani", "Yavatmal", "Warora", "Pandharkawada", "Zari", "Maregaon", "Ralegaon", 
        "Darwha", "Digras", "Pusad", "Ghatanji", "Kelapur", "Ner", "Kalamb", 
        "Babhulgaon", "Arni", "Parwa", "Mukutban", "Kayar", "Shindola", "Majra", 
        "Kumbha", "Punwat", "Rajur", "Ghugus", "Tadali", "Bhadravati", "Chandrapur", 
        "Ballarpur", "Korpana", "Gadchandur", "Jharijamni", "Naigaon", "Shirpur", 
        "Dhanora", "Pardi", "Sonapur", "Bori", "Sawangi", "Hiwri", "Nagpur", 
        "Amravati", "Akola", "Wardha", "Bhandara"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSource = view.findViewById(R.id.et_planner_source);
        etDest = view.findViewById(R.id.et_planner_dest);
        containerPlans = view.findViewById(R.id.container_plans);
        tvPlansTitle = view.findViewById(R.id.tv_plans_title);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_dropdown_black, LOCATIONS);
        etSource.setAdapter(adapter);
        etDest.setAdapter(adapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getFragmentManager().popBackStack());
        view.findViewById(R.id.btn_analyze_trip).setOnClickListener(v -> analyzeTrip());
    }

    private void analyzeTrip() {
        String s = etSource.getText().toString().trim();
        String d = etDest.getText().toString().trim();

        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(d)) {
            Toast.makeText(getContext(), "Please select locations", Toast.LENGTH_SHORT).show();
            return;
        }

        tvPlansTitle.setVisibility(View.VISIBLE);
        containerPlans.removeAllViews();

        addPlanCard("⚡ EXPRESS SELECTION", "Shivshahi AC", "₹240", "2h 10m", 
                "Reduced travel time by 30% via Wani Bypass", R.color.primary_red);
        
        addPlanCard("💰 BUDGET SELECTION", "MSRTC Parivahan", "₹120", "3h 05m", 
                "Save ₹120 per trip + 4.2kg CO2 reduced reward", R.color.status_green);
    }

    private void addPlanCard(String tag, String busType, String price, String time, String insight, int colorResId) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.item_planner_result, containerPlans, false);
        
        TextView tvTag = card.findViewById(R.id.tv_planner_tag);
        TextView tvType = card.findViewById(R.id.tv_planner_bus_type);
        TextView tvPrice = card.findViewById(R.id.tv_planner_price);
        TextView tvTime = card.findViewById(R.id.tv_planner_time);
        TextView tvInsight = card.findViewById(R.id.tv_planner_insight);

        tvTag.setText(tag);
        tvTag.setTextColor(getResources().getColor(colorResId));
        tvType.setText(busType);
        tvPrice.setText(price);
        tvTime.setText(time);
        tvInsight.setText(insight);

        containerPlans.addView(card);
    }
}
