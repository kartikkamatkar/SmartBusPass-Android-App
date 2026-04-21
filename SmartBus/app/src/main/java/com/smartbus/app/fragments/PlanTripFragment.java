package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.smartbus.app.R;
import com.smartbus.app.activities.MainActivity;
import com.smartbus.app.adapters.AvailableBusAdapter;
import com.smartbus.app.models.BusPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Senior Developer Implementation: PlanTripFragment
 * Optimized with Unique Quick Actions and cleaned layout to prevent text overlaps.
 */
public class PlanTripFragment extends Fragment {

    private static final String ARG_INITIAL_DEST = "initial_dest";
    
    private AutoCompleteTextView etSource, etDest;
    private RecyclerView rvResults;
    private View layoutEmpty;
    private TextView tvResultsTitle;
    private Toolbar toolbar;
    
    private final List<BusPass> busOptions = new ArrayList<>();
    
    private static final String[] LOCATIONS = {
            "Wani", "Yavatmal", "Chandrapur", "Nagpur", "Amravati", "Warora", "Pandharkawada",
            "Maregaon", "Zari", "Mukutban", "Korpana", "Gadchandur", "Rajura", "Kanhalgaon",
            "Bhadravati", "Ghugus", "Ballarpur", "Mul", "Pombhurna", "Gondpipari", "Sindewahi",
            "Chimur", "Nagbhir", "Brahmapuri", "Saoli"
    };

    public static PlanTripFragment newInstance(String destination) {
        PlanTripFragment fragment = new PlanTripFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INITIAL_DEST, destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupToolbar();
        setupAutocomplete();
        setupRecyclerView();
        setupQuickActions(view);
        
        if (getArguments() != null) {
            String initialDest = getArguments().getString(ARG_INITIAL_DEST);
            if (!TextUtils.isEmpty(initialDest)) {
                etDest.setText(initialDest);
                etSource.setText("Wani");
                performSearch();
            }
        }
        
        view.findViewById(R.id.btn_plan_search).setOnClickListener(v -> performSearch());
    }

    private void initializeViews(View view) {
        etSource = view.findViewById(R.id.et_plan_source);
        etDest = view.findViewById(R.id.et_plan_dest);
        rvResults = view.findViewById(R.id.rv_plan_results);
        layoutEmpty = view.findViewById(R.id.layout_plan_empty);
        tvResultsTitle = view.findViewById(R.id.tv_plan_results_title);
        toolbar = view.findViewById(R.id.toolbar_plan_trip);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupAutocomplete() {
        if (getContext() != null) {
            ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), 
                    R.layout.item_dropdown_black, LOCATIONS);
            etSource.setAdapter(dropdownAdapter);
            etDest.setAdapter(dropdownAdapter);
        }
    }

    private void setupRecyclerView() {
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupQuickActions(View view) {
        Chip chipHome = view.findViewById(R.id.chip_quick_home);
        Chip chipOffice = view.findViewById(R.id.chip_quick_office);
        Chip chipReverse = view.findViewById(R.id.chip_quick_recent);

        if (chipHome != null) {
            chipHome.setOnClickListener(v -> {
                etSource.setText("Home Stop");
                etDest.setText("Wani Depot");
                performSearch();
            });
        }

        if (chipOffice != null) {
            chipOffice.setOnClickListener(v -> {
                etSource.setText("Wani");
                etDest.setText("Yavatmal");
                performSearch();
            });
        }

        if (chipReverse != null) {
            chipReverse.setOnClickListener(v -> {
                String s = etSource.getText().toString();
                String d = etDest.getText().toString();
                if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(d)) {
                    etSource.setText(d);
                    etDest.setText(s);
                    performSearch();
                }
            });
        }
    }

    private void performSearch() {
        String source = etSource.getText().toString().trim();
        String dest = etDest.getText().toString().trim();

        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(dest)) {
            Toast.makeText(requireContext(), "Details required for planning", Toast.LENGTH_SHORT).show();
            return;
        }

        generateMSRTCOptions(source, dest);
        
        if (busOptions.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            tvResultsTitle.setVisibility(View.GONE);
            rvResults.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            tvResultsTitle.setVisibility(View.VISIBLE);
            rvResults.setVisibility(View.VISIBLE);
            
            AvailableBusAdapter adapter = new AvailableBusAdapter(new ArrayList<>(busOptions), bus -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadSubFragment(new TrackFragment());
                }
            });
            
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
            rvResults.setLayoutAnimation(controller);
            rvResults.setAdapter(adapter);
            rvResults.scheduleLayoutAnimation();
        }
    }

    private void generateMSRTCOptions(String s, String d) {
        busOptions.clear();
        String route = s + " ↔ " + d;
        
        busOptions.add(new BusPass(1, "MSRTC Ordinary Express", route, "Departure: 08:30 AM (ETA 2.5h)", "Fare: ₹65"));
        busOptions.add(new BusPass(2, "MSRTC Shivshahi AC", route, "Departure: 10:15 AM (ETA 2h)", "Fare: ₹180"));
        busOptions.add(new BusPass(3, "MSRTC Parivartan Day", route, "Departure: 01:45 PM (ETA 3h)", "Fare: ₹75"));
        busOptions.add(new BusPass(4, "MSRTC Asiad Non-AC", route, "Departure: 04:30 PM (ETA 2.5h)", "Fare: ₹90"));
    }
}
