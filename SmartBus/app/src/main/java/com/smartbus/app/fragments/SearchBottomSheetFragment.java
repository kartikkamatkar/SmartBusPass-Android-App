package com.smartbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.activities.MainActivity;
import com.smartbus.app.adapters.AvailableBusAdapter;
import com.smartbus.app.models.BusPass;

import java.util.ArrayList;
import java.util.List;

public class SearchBottomSheetFragment extends DialogFragment {

    private static final String ARG_DEST = "initial_dest";
    private AutoCompleteTextView etSource, etDest;
    private RecyclerView rvResults;
    private AvailableBusAdapter busAdapter;
    private final List<BusPass> allBuses = new ArrayList<>();

    private static final String[] LOCATIONS = {
            "Wani", "Yavatmal", "Chandrapur", "Nagpur", "Amravati", "Warora", "Pandharkawada",
            "Maregaon", "Zari", "Mukutban", "Korpana", "Gadchandur", "Rajura", "Kanhalgaon",
            "Bhadravati", "Ghugus", "Ballarpur", "Mul", "Pombhurna", "Gondpipari", "Sindewahi",
            "Chimur", "Nagbhir", "Brahmapuri", "Saoli", "Jiti", "Kayar", "Shindola", "Punwat",
            "Bhalar", "Pardi", "Rajur", "Kolera", "Wadgaon", "Mohada", "Kumbhani", "Nirguda",
            "Mendoli", "Shirpur", "Parwa", "Ralegaon", "Kalamb", "Babhulgaon", "Ner", "Darwha",
            "Digras", "Pusad", "Umarkhed", "Mahagaon", "Arni", "Ghatanji", "Kelapur", "Wani Depot",
            "Zari Jamni", "Wani City", "Wani Railway Stn", "Wani Chowk"
    };

    public static SearchBottomSheetFragment newInstance(String dest) {
        SearchBottomSheetFragment fragment = new SearchBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEST, dest);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_search_bottom_sheet, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SmartBus_Dialog_Center);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSource = view.findViewById(R.id.et_search_source);
        etDest = view.findViewById(R.id.et_search_dest);
        rvResults = view.findViewById(R.id.rv_available_buses);

        // Setup auto complete for source & destination
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown_black, LOCATIONS);
            etSource.setAdapter(adapter);
            etDest.setAdapter(adapter);
        }

        if (getArguments() != null) {
            String initialDest = getArguments().getString(ARG_DEST);
            if (initialDest != null && !initialDest.isEmpty()) {
                etSource.setText("Wani"); // Default source
                etDest.setText(initialDest);
                // Auto trigger search if destination provided
                performSearch();
            }
        }

        setupDummyBuses();

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        busAdapter = new AvailableBusAdapter(new ArrayList<>(), bus -> {
            dismiss();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadSubFragment(new TrackFragment());
            }
        });
        rvResults.setAdapter(busAdapter);

        view.findViewById(R.id.btn_bottom_search).setOnClickListener(v -> performSearch());
    }

    private void setupDummyBuses() {
        allBuses.clear();
        String route = "Wani ↔ Yavatmal";
        String validity = "Today";
        String fare = "Fare: ₹45";

        for (int i = 0; i < 10; i++) {
            BusPass bus = new BusPass(i + 1, "MSRTC Express " + (101 + i), route, validity, fare);
            allBuses.add(bus);
        }
    }

    private void performSearch() {
        String source = etSource.getText().toString().trim();
        String dest = etDest.getText().toString().trim();

        if (source.isEmpty() || dest.isEmpty()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Please select Source and Destination", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Show results
        busAdapter = new AvailableBusAdapter(new ArrayList<>(allBuses), bus -> {
            dismiss();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadSubFragment(new TrackFragment());
            }
        });
        rvResults.setAdapter(busAdapter);
    }
}
