package com.smartbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.smartbus.app.R;
import com.smartbus.app.activities.MainActivity;
import com.smartbus.app.utils.SessionManager;

import java.util.Random;

public class HomeFragment extends Fragment {

    private SessionManager sessionManager;
    private AutoCompleteTextView etHomeSearch;
    private TextView tvWelcome, tvAiTip1, tvAiTip2;
    private View cardSearch, btnLiveTrackNav, btnPlanTripNav, btnHomeGetPass;
    private ImageView btnScan, btnNotification;
    private MaterialCardView cardLiveTracking, cardGetPassCta;
    private View btnViewOnMap;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            sessionManager = new SessionManager(requireContext());

            tvWelcome = view.findViewById(R.id.tv_home_welcome);
            tvAiTip1 = view.findViewById(R.id.tv_ai_tip_1);
            tvAiTip2 = view.findViewById(R.id.tv_ai_tip_2);

            etHomeSearch = view.findViewById(R.id.et_home_search);
            cardSearch = view.findViewById(R.id.card_search);
            btnScan = view.findViewById(R.id.iv_profile_pic);
            btnNotification = view.findViewById(R.id.btn_notification);
            btnViewOnMap = view.findViewById(R.id.btn_view_on_map);
            cardLiveTracking = view.findViewById(R.id.card_live_tracking);
            cardGetPassCta = view.findViewById(R.id.card_get_pass_cta);
            
            btnLiveTrackNav = view.findViewById(R.id.btn_live_track);
            btnPlanTripNav = view.findViewById(R.id.btn_plan_trip);
            btnHomeGetPass = view.findViewById(R.id.btn_home_get_pass);

            setupAutocomplete();
            loadDynamicData();
            setupClickListeners();
        } catch (Exception e) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupAutocomplete() {
        if (etHomeSearch != null && getContext() != null) {
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    requireContext(), R.layout.item_dropdown_black, LOCATIONS);
            etHomeSearch.setAdapter(adapter);
            etHomeSearch.setOnItemClickListener((parent, view, position, id) -> {
                String destination = (String) parent.getItemAtPosition(position);
                openSearchDialog(destination);
            });
        }
    }

    private void loadDynamicData() {
        try {
            if (tvWelcome != null && sessionManager != null) {
                String name = sessionManager.getUserName();
                if (name == null || name.isEmpty()) {
                    name = "User";
                }
                tvWelcome.setText(getString(R.string.hello_user, name));
            }

            if (tvAiTip1 != null && tvAiTip2 != null) {
                String[] tips = {
                        getString(R.string.home_ai_tip_1), getString(R.string.home_ai_tip_2), getString(R.string.home_ai_tip_3),
                        getString(R.string.home_ai_tip_4), getString(R.string.home_ai_tip_5), getString(R.string.home_ai_tip_6),
                        getString(R.string.home_ai_tip_7), getString(R.string.home_ai_tip_8), getString(R.string.home_ai_tip_9),
                        getString(R.string.home_ai_tip_10), getString(R.string.home_ai_tip_11), getString(R.string.home_ai_tip_12),
                        getString(R.string.home_ai_tip_13), getString(R.string.home_ai_tip_14), getString(R.string.home_ai_tip_15),
                        getString(R.string.home_ai_tip_16), getString(R.string.home_ai_tip_17), getString(R.string.home_ai_tip_18),
                        getString(R.string.home_ai_tip_19), getString(R.string.home_ai_tip_20)
                };

                Random random = new Random();
                tvAiTip1.setText(tips[random.nextInt(tips.length)]);
                tvAiTip2.setText(tips[random.nextInt(tips.length)]);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private void setupClickListeners() {
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v ->
                    Toast.makeText(getContext(), getString(R.string.home_no_notifications), Toast.LENGTH_SHORT).show());
        }

        if (btnScan != null) {
            btnScan.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadProfileFragment();
                }
            });
        }

        if (cardSearch != null) {
            cardSearch.setOnClickListener(v -> openSearchDialog(""));
        }

        if (etHomeSearch != null) {
            etHomeSearch.setOnClickListener(v -> openSearchDialog(""));
        }

        if (cardLiveTracking != null) {
            cardLiveTracking.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadTrackFragment();
                }
            });
        }

        if (btnViewOnMap != null) {
            btnViewOnMap.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadTrackFragment();
                }
            });
        }

        if (btnLiveTrackNav != null) {
            btnLiveTrackNav.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadTrackFragment();
                }
            });
        }

        if (btnPlanTripNav != null) {
            btnPlanTripNav.setOnClickListener(v -> openSearchDialog(""));
        }

        if (btnHomeGetPass != null) {
            btnHomeGetPass.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadPassFragment();
                }
            });
        }
    }

    private void openSearchDialog(String initialDest) {
        if (isAdded() && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadSubFragment(PlanTripFragment.newInstance(initialDest));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDynamicData();
    }
}
