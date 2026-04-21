package com.smartbus.app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smartbus.app.R;
import com.smartbus.app.utils.SessionManager;

/**
 * Senior Developer Implementation: EditProfileFragment
 * Merged from EditProfileActivity for Single Activity Architecture.
 */
public class EditProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private EditText etName, etEmail, etPhone;
    private View btnSave;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_profile, container, false); // Still using activity_edit_profile layout temporarily
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        etName = view.findViewById(R.id.et_edit_name);
        etEmail = view.findViewById(R.id.et_edit_email);
        etPhone = view.findViewById(R.id.et_edit_phone);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnBack = view.findViewById(R.id.btn_back);

        // Pre-fill data
        etName.setText(sessionManager.getUserName());
        etEmail.setText(sessionManager.getUserEmail());
        etPhone.setText("9876543210"); // Simulated data

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Name required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Email required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update session
                sessionManager.setUserName(name);
                sessionManager.setUserEmail(email);

                Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                
                // Return to profile hub
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
}
