package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.adapters.BusAdapter;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TripResultsFragment extends Fragment {

    private String source, destination;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private List<BusPass> busList = new ArrayList<>();
    private com.smartbus.app.adapters.AvailableBusAdapter adapter;

    public static TripResultsFragment newInstance(String source, String destination) {
        TripResultsFragment fragment = new TripResultsFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        args.putString("destination", destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = getArguments().getString("source");
            destination = getArguments().getString("destination");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_trip_results_title);
        recyclerView = view.findViewById(R.id.recycler_trip_results);

        tvTitle.setText(source + " ↔ " + destination);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new com.smartbus.app.adapters.AvailableBusAdapter(busList, bus -> {
            // Track this bus
            if (requireActivity() instanceof com.smartbus.app.activities.MainActivity) {
                ((com.smartbus.app.activities.MainActivity) requireActivity()).loadSubFragment(new TrackFragment());
            }
        });
        recyclerView.setAdapter(adapter);

        loadDummyBuses();
    }

    private void loadDummyBuses() {
        busList.clear();
        // MSRTC Service Types
        String[] busTypes = {
            "Shivshahi AC (Sleeper)", 
            "Asiad Semi-Luxury (70:30)", 
            "Parivartan (Lal Pari)", 
            "Hirkani (Non-AC Luxury)",
            "Shivneri (Intercity AC)",
            "Ordinary (Nim-Aaram)"
        };
        
        String[] depots = {
            "Wani Depot", "Yavatmal Depot", "Chandrapur Depot", "Nagpur Ganeshpeth", "Amravati Central"
        };
        
        String[] times = {"06:15 AM", "07:45 AM", "09:30 AM", "11:00 AM", "01:15 PM", "03:40 PM", "05:55 PM", "08:20 PM"};
        String[] fares = {"₹65", "₹85", "₹140", "₹195", "₹220", "₹45"};
        
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            String type = busTypes[r.nextInt(busTypes.length)];
            String time = times[i % times.length];
            String fare = fares[r.nextInt(fares.length)];
            String depot = depots[r.nextInt(depots.length)];
            
            // Format: MH 29 BE [RANDOM]
            String busNumber = "MH 29 BE " + (1000 + r.nextInt(8999));
            
            // Reusing BusPass model as a Bus data holder for the adapter
            // name=Type, route=Source ↔ Dest, validity=Time, purchaseDate=Fare (with depot)
            busList.add(new BusPass(i, type, source + " ↔ " + destination, time, fare + " (" + depot + ")"));
        }
        adapter.notifyDataSetChanged();
    }
}
