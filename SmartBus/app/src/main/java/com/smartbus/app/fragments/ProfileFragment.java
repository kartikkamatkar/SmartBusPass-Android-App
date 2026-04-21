package com.smartbus.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartbus.app.R;
import com.smartbus.app.activities.LoginActivity;
import com.smartbus.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        View btnLogout = view.findViewById(R.id.btn_profile_logout);

        // Populate dynamic data
        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());

        // Language Selection logic
        TextView tvCurrentLang = view.findViewById(R.id.tv_current_language);
        View btnLanguage = view.findViewById(R.id.btn_language_selection);
        
        String currentLang = sessionManager.getLanguage();
        if ("hi".equals(currentLang)) tvCurrentLang.setText("Hindi >");
        else if ("mr".equals(currentLang)) tvCurrentLang.setText("Marathi >");
        else tvCurrentLang.setText("English >");

        btnLanguage.setOnClickListener(v -> showLanguageDialog(tvCurrentLang));

        // Edit Profile click
        View btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            if (requireActivity() instanceof com.smartbus.app.activities.MainActivity) {
                ((com.smartbus.app.activities.MainActivity) requireActivity()).loadSubFragment(new EditProfileFragment());
            }
        });

        // Help and About clicks
        view.findViewById(R.id.btn_help_support).setOnClickListener(v -> {
            if (requireActivity() instanceof com.smartbus.app.activities.MainActivity) {
                ((com.smartbus.app.activities.MainActivity) requireActivity()).loadSubFragment(new HelpFragment());
            }
        });

        view.findViewById(R.id.btn_about_us).setOnClickListener(v -> {
            if (requireActivity() instanceof com.smartbus.app.activities.MainActivity) {
                ((com.smartbus.app.activities.MainActivity) requireActivity()).loadSubFragment(new AboutFragment());
            }
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void showLanguageDialog(TextView tvCurrentLang) {
        String[] languages = {"English", "Hindi", "Marathi"};
        String[] codes = {"en", "hi", "mr"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Language");
        builder.setItems(languages, (dialog, which) -> {
            String selectedLang = codes[which];
            sessionManager.setLanguage(selectedLang);
            tvCurrentLang.setText(languages[which] + " >");
            
            // Restart activity to apply language change
            Toast.makeText(requireContext(), "Changing language to " + languages[which], Toast.LENGTH_SHORT).show();
            requireActivity().recreate();
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data in case it was updated in EditProfileActivity
        if (sessionManager != null && getView() != null) {
            TextView tvName = getView().findViewById(R.id.tv_profile_name);
            TextView tvEmail = getView().findViewById(R.id.tv_profile_email);
            if (tvName != null) tvName.setText(sessionManager.getUserName());
            if (tvEmail != null) tvEmail.setText(sessionManager.getUserEmail());
        }
    }
}
