package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.models.SearchBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusSearchFragment extends Fragment {

    private String source, destination;
    private RecyclerView recyclerView;
    private final List<SearchBus> busList = new ArrayList<>();

    public static BusSearchFragment newInstance(String source, String destination) {
        BusSearchFragment fragment = new BusSearchFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvRoute = view.findViewById(R.id.tv_results_route);
        tvRoute.setText(String.format("%s → %s", source, destination));

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_bus_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        generateBusData();
        
        BusSearchAdapter adapter = new BusSearchAdapter(busList);
        recyclerView.setAdapter(adapter);
    }

    private void generateBusData() {
        busList.clear();
        String s = source.toLowerCase().trim();
        String d = destination.toLowerCase().trim();

        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(d)) return;

        // 30 Wani Region & Local Locations
        String[] waniRegion = {
            "wani", "yavatmal", "warora", "pandharkawada", "zari", "maregaon", "ralegaon", 
            "darwha", "digras", "pusad", "ghatanji", "kelapur", "ner", "kalamb", 
            "babhulgaon", "arni", "parwa", "mukutban", "kayar", "shindola", "majra", 
            "kumbha", "punwat", "rajur", "ghugus", "tadali", "bhadravati", "chandrapur", 
            "ballarpur", "korpana"
        };

        // 30 Vidarbha & Major Destinations
        String[] vidarbhaRegion = {
            "nagpur", "amravati", "akola", "wardha", "bhandara", "gadchiroli", "gondia", 
            "washim", "buldhana", "hinganghat", "umred", "katol", "savner", "kamptee", 
            "ramtek", "tumsar", "pauni", "desaiganj", "chamorshi", "aheri", "mul", 
            "warud", "morshi", "achalpur", "anjangaon", "karanja", "shegaon", "khamgaon", 
            "malkapur", "mehkar"
        };

        boolean isSourceWani = false;
        for (String loc : waniRegion) if (s.contains(loc)) { isSourceWani = true; break; }
        
        boolean isDestVidarbha = false;
        for (String loc : vidarbhaRegion) if (d.contains(loc)) { isDestVidarbha = true; break; }

        // Also check reverse (source in vidarbha, dest in wani)
        boolean isSourceVidarbha = false;
        for (String loc : vidarbhaRegion) if (s.contains(loc)) { isSourceVidarbha = true; break; }
        
        boolean isDestWani = false;
        for (String loc : waniRegion) if (d.contains(loc)) { isDestWani = true; break; }

        if ((isSourceWani && isDestVidarbha) || (isSourceVidarbha && isDestWani)) {
            // Highly active routes: 15 buses (6 AM - 9 PM)
            addMSRTCBuses(source + " ↔ " + destination, 15, 6, 21);
        } else if (isSourceWani || isDestWani) {
            // Local region routes: 10 buses (6 AM - 7 PM)
            addMSRTCBuses(source + " ↔ " + destination, 10, 6, 19);
        } else {
            // Generic/Fallback: 5 buses
            addMSRTCBuses("Intercity Express", 5, 8, 17);
        }
    }

    private void addMSRTCBuses(String routeName, int count, int startHour, int endHour) {
        String[] busTypes = {"MSRTC Parivahan", "MSRTC Shivshahi (AC)", "MSRTC Hirkani", "MSRTC Lal Pari"};
        double intervalMinutes = (double) (endHour - startHour) * 60 / (count + 1);
        
        for (int i = 1; i <= count; i++) {
            int totalMinutes = (int) (startHour * 60 + i * intervalMinutes);
            int hour = totalMinutes / 60;
            int minute = totalMinutes % 60;
            String ampm = (hour >= 12) ? "PM" : "AM";
            int h12 = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d %s", h12, minute, ampm);
            
            String randomType = busTypes[i % busTypes.length];
            String busTitle = randomType + " - " + (2000 + i);
            
            busList.add(new SearchBus(busTitle, "Departure: " + timeStr, routeName));
        }
    }

    // Adapter Class
    private class BusSearchAdapter extends RecyclerView.Adapter<BusSearchAdapter.ViewHolder> {
        private final List<SearchBus> buses;

        public BusSearchAdapter(List<SearchBus> buses) { this.buses = buses; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_search, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchBus bus = buses.get(position);
            holder.tvName.setText(bus.getName());
            holder.tvTime.setText(bus.getTime());
            holder.btnTrack.setOnClickListener(v -> {
                if (requireActivity() instanceof com.smartbus.app.activities.MainActivity) {
                    ((com.smartbus.app.activities.MainActivity) requireActivity()).loadFragment(new TrackFragment());
                }
            });
        }

        @Override public int getItemCount() { return buses.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvTime;
            View btnTrack;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_bus_item_name);
                tvTime = itemView.findViewById(R.id.tv_bus_item_time);
                btnTrack = itemView.findViewById(R.id.btn_item_track);
            }
        }
    }
}
