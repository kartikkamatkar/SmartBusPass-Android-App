package com.smartbus.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartbus.app.R;
import com.smartbus.app.fragments.HomeFragment;
import com.smartbus.app.fragments.PassFragment;
import com.smartbus.app.fragments.ProfileFragment;
import com.smartbus.app.fragments.TrackFragment;
import com.smartbus.app.utils.LocaleHelper;
import com.smartbus.app.utils.SessionManager;

import android.content.Context;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager sessionManager = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, sessionManager.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                loadFragment(new HomeFragment(), false);
                return true;
            } else if (id == R.id.menu_track) {
                loadFragment(new TrackFragment(), false);
                return true;
            } else if (id == R.id.menu_pass) {
                loadFragment(new PassFragment(), false);
                return true;
            } else if (id == R.id.menu_profile) {
                loadFragment(new ProfileFragment(), false);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            String tabToOpen = getIntent().getStringExtra("open_tab");
            if ("pass".equals(tabToOpen)) {
                bottomNavigationView.setSelectedItemId(R.id.menu_pass);
            } else if ("track".equals(tabToOpen)) {
                bottomNavigationView.setSelectedItemId(R.id.menu_track);
            } else if ("profile".equals(tabToOpen)) {
                bottomNavigationView.setSelectedItemId(R.id.menu_profile);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.menu_home);
            }
        }

        // Handle system back with OnBackPressedDispatcher instead of deprecated onBackPressed()
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
                    if (bnv.getSelectedItemId() != R.id.menu_home) {
                        bnv.setSelectedItemId(R.id.menu_home);
                    } else {
                        // On Home and no sub-fragments, finish app
                        finish();
                    }
                }
            }
        });
    }

    public void loadHomeFragment() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }

    public void loadTrackFragment() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_track);
    }

    public void loadPassFragment() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_pass);
    }

    public void loadProfileFragment() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
    }

    public void loadFragment(Fragment fragment) {
        loadFragment(fragment, true); // Default keep sub-fragments in backstack
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, tag);
        
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        } else {
            // Clear backstack when switching main tabs
            try {
                getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception e) {
                // Ignore state loss during tab switching
            }
        }
        transaction.commit();
    }

    public void loadSubFragment(Fragment fragment) {
        loadFragment(fragment, true);
    }
}
