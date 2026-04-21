package com.smartbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.smartbus.app.R;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.models.BusPass;

import java.util.Locale;

/**
 * Senior Developer Implementation: PassDetailsFragment
 * Refined for stability and end-to-end reliability.
 */
public class PassDetailsFragment extends Fragment {

    private static final String ARG_PASS_ID = "pass_id";
    private int passId;
    private DBHelper dbHelper;

    public static PassDetailsFragment newInstance(int id) {
        PassDetailsFragment fragment = new PassDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PASS_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            passId = getArguments().getInt(ARG_PASS_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pass_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            if (getContext() == null) return;
            dbHelper = new DBHelper(getContext());
            
            setupToolbar(view);
            displayPassData(view);
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Detail view failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_details);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void displayPassData(View view) {
        BusPass pass = dbHelper.getPassById(passId);
        if (pass == null) {
            Toast.makeText(getContext(), "Pass record not found", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView tvName = view.findViewById(R.id.tv_detail_pass_name);
        TextView tvRoute = view.findViewById(R.id.tv_detail_route);
        TextView tvId = view.findViewById(R.id.tv_detail_id);
        TextView tvExpiry = view.findViewById(R.id.tv_detail_expiry);

        String formattedId = String.format(Locale.US, "SB-%04d-%d", pass.getId() % 10000, 1 + (pass.getId() / 100) % 9);

        if (tvName != null) tvName.setText(pass.getName());
        if (tvRoute != null) tvRoute.setText(pass.getRoute());
        if (tvId != null) tvId.setText(formattedId);
        if (tvExpiry != null) tvExpiry.setText(pass.getValidity());
    }
}
