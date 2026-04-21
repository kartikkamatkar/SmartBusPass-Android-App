package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbus.app.R;
import com.smartbus.app.adapters.AvailablePassAdapter;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewPassFragment extends Fragment {

    private com.google.android.material.chip.ChipGroup chipGroup;
    private android.widget.AutoCompleteTextView tvSource, tvDest;
    private RecyclerView recyclerView;
    private View btnSearch, tvResultsTitle;
    private AvailablePassAdapter adapter;
    private final List<BusPass> availablePassList = new ArrayList<>();
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_pass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(requireContext());
        tvSource = view.findViewById(R.id.et_source);
        tvDest = view.findViewById(R.id.et_dest);
        btnSearch = view.findViewById(R.id.btn_search_passes);
        recyclerView = view.findViewById(R.id.recycler_available_passes);
        tvResultsTitle = view.findViewById(R.id.tv_results_title);
        chipGroup = view.findViewById(R.id.chip_group_pass_type);

        setupAutocomplete();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AvailablePassAdapter(availablePassList, pass -> {
            Toast.makeText(requireContext(), "Verifying identity...", Toast.LENGTH_SHORT).show();

            view.postDelayed(() -> {
                if (!isAdded() || getContext() == null) return;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                String today = sdf.format(new Date());

                long insertedId = dbHelper.insertPass(pass.getName(), pass.getRoute(), pass.getValidity(), today);

                // Retry once with normalized values if first insert fails.
                if (insertedId <= 0) {
                    String normalizedName = pass.getName()
                            .replaceAll("[^\\p{L}0-9\\s\\-().&]", "")
                            .trim();
                    if (normalizedName.isEmpty()) {
                        normalizedName = "Smart Pass";
                    }

                    String normalizedRoute = pass.getRoute().trim();
                    if (!normalizedRoute.contains("↔") && !normalizedRoute.contains("-") && !normalizedRoute.contains("/")) {
                        normalizedRoute = normalizedRoute.replace("to", "-");
                    }

                    insertedId = dbHelper.insertPass(normalizedName, normalizedRoute, pass.getValidity(), today);
                }

                if (insertedId > 0) {
                    Toast.makeText(requireContext(), pass.getName() + " activated!", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed - Schema version mismatch or invalid route formatting.", Toast.LENGTH_LONG).show();
                }
            }, 1200);
        });
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> searchPasses());
    }

    private void setupAutocomplete() {
        String[] locations = {
            "Wani", "Yavatmal", "Chandrapur", "Nagpur", "Amravati", "Warora", "Pandharkawada",
            "Maregaon", "Zari", "Mukutban", "Korpana", "Gadchandur", "Rajura", "Kanhalgaon",
            "Bhadravati", "Ghugus", "Ballarpur", "Mul", "Pombhurna", "Gondpipari", "Sindewahi",
            "Chimur", "Nagbhir", "Brahmapuri", "Saoli", "Jiti", "Kayar", "Shindola", "Punwat",
            "Bhalar", "Pardi", "Rajur", "Kolera", "Wadgaon", "Mohada", "Kumbhani"
        };

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
            requireContext(), R.layout.item_dropdown_black, locations);

        tvSource.setAdapter(adapter);
        tvDest.setAdapter(adapter);
    }

    private void searchPasses() {
        String source = tvSource.getText().toString().trim();
        String dest = tvDest.getText().toString().trim();

        if (TextUtils.isEmpty(source)) {
            tvSource.setError("Please select a departure location");
            Toast.makeText(requireContext(), "Select departure location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(dest)) {
            tvDest.setError("Please select a destination");
            Toast.makeText(requireContext(), "Select destination", Toast.LENGTH_SHORT).show();
            return;
        }

        if (source.equalsIgnoreCase(dest)) {
            Toast.makeText(requireContext(), "Source and destination must be different", Toast.LENGTH_SHORT).show();
            return;
        }

        String route = source + " ↔ " + dest;
        availablePassList.clear();

        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

            int selectedChipId = chipGroup.getCheckedChipId();
            String passCategory = "General";
            double multiplier = 1.0;

            if (selectedChipId == R.id.chip_student) {
                passCategory = "Student";
                multiplier = 0.5;
            } else if (selectedChipId == R.id.chip_senior) {
                passCategory = "Senior Citizen";
                multiplier = 0.6;
            } else if (selectedChipId == R.id.chip_msrtc) {
                passCategory = "MSRTC Staff";
                multiplier = 0.1;
            }

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            availablePassList.add(new BusPass(-1, passCategory + " Day Pass", route,
                    sdf.format(cal.getTime()), "₹" + (int) (60 * multiplier)));

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 7);
            availablePassList.add(new BusPass(-2, passCategory + " Weekly Pass", route,
                    sdf.format(cal.getTime()), "₹" + (int) (350 * multiplier)));

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 30);
            availablePassList.add(new BusPass(-3, passCategory + " Monthly Smart Pass", route,
                    sdf.format(cal.getTime()), "₹" + (int) (1100 * multiplier)));

            if (passCategory.equals("MSRTC Staff") || passCategory.equals("Student")) {
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 90);
                availablePassList.add(new BusPass(-4, passCategory + " Semester Pass 90 Days", route,
                        sdf.format(cal.getTime()), "₹" + (int) (2800 * multiplier)));
            }

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 365);
            availablePassList.add(new BusPass(-5, passCategory + " Annual Pass", route,
                    sdf.format(cal.getTime()), "₹" + (int) (3800 * multiplier)));

            if (tvResultsTitle != null) {
                tvResultsTitle.setVisibility(View.VISIBLE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                recyclerView.setAlpha(0f);
                recyclerView.animate().alpha(1f).setDuration(400).start();
            }

        } catch (Exception e) {
            android.util.Log.e("NewPassFragment", "Error in searchPasses: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error loading passes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
