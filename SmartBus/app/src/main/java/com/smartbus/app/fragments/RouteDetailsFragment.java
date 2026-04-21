package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.adapters.AvailableBusAdapter;
import com.smartbus.app.models.BusPass;

import java.util.ArrayList;
import java.util.List;

/**
 * RouteDetailsFragment
 * Shows MSRTC city buses for a specifically searched location or route.
 */
public class RouteDetailsFragment extends Fragment {

    private static final String ARG_ROUTE = "selected_route";
    private String selectedRoute;
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.Toolbar toolbar;

    public static RouteDetailsFragment newInstance(String route) {
        RouteDetailsFragment fragment = new RouteDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedRoute = getArguments().getString(ARG_ROUTE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar_route_details);
        recyclerView = view.findViewById(R.id.rv_route_buses);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
            toolbar.setTitle(TextUtils.isEmpty(selectedRoute) ? "MSRTC Route Details" : selectedRoute);
        }

        List<BusPass> routes = generateMSRTCCityBuses();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new AvailableBusAdapter(routes, bus -> {
            // Re-track selected city bus
            getParentFragmentManager().popBackStack();
        }));
    }

    private List<BusPass> generateMSRTCCityBuses() {
        List<BusPass> list = new ArrayList<>();
        String r = TextUtils.isEmpty(selectedRoute) ? "Local City Route" : selectedRoute;
        
        list.add(new BusPass(10, "MSRTC City Bus #42", r, "Frequency: every 15 min", "Fare: ₹15"));
        list.add(new BusPass(11, "MSRTC City Bus #12A", r, "Frequency: every 30 min", "Fare: ₹10"));
        list.add(new BusPass(12, "MSRTC Mini Bus", r, "Frequency: every 10 min", "Fare: ₹20"));
        list.add(new BusPass(13, "MSRTC Electric 'E-Bus'", r, "Frequency: every 45 min", "Fare: ₹12"));
        
        return list;
    }
}
