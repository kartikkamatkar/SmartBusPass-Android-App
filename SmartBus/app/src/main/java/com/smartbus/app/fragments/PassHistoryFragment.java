package com.smartbus.app.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.smartbus.app.activities.MainActivity;
import com.smartbus.app.adapters.BusAdapter;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Senior Developer Implementation: PassHistoryFragment
 * Displays travel history with stability and error handling
 */
public class PassHistoryFragment extends Fragment {

    private static final String TAG = "PassHistoryFragment";
    private DBHelper dbHelper;
    private BusAdapter adapter;
    private final List<BusPass> passList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pass_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            if (getContext() == null) {
                showError("Context is null");
                return;
            }

            dbHelper = new DBHelper(requireContext());
            setupRecyclerView(view);
            loadPasses();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            showError("Failed to load history");
        }
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_full_history);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new BusAdapter(passList, pass -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadSubFragment(PassDetailsFragment.newInstance(pass.getId()));
                }
            }, dbHelper);
            recyclerView.setAdapter(adapter);
        } else {
            Log.w(TAG, "RecyclerView not found in layout");
        }
    }

    private void loadPasses() {
        try {
            if (dbHelper == null) {
                Log.w(TAG, "DBHelper is null");
                return;
            }

            passList.clear();
            List<BusPass> allPasses = dbHelper.getAllPasses();
            
            if (allPasses != null && !allPasses.isEmpty()) {
                passList.addAll(allPasses);
            }
            
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading passes: " + e.getMessage(), e);
            showError("Failed to load history");
        }
    }

    private void showError(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPasses();
    }
}
