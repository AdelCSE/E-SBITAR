package com.example.e_sbitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.e_sbitar.Models.UserModel;
import com.example.e_sbitar.fragments.HealthFragment;
import com.example.e_sbitar.fragments.MedicationFragment;
import com.example.e_sbitar.fragments.NewsFragment;
import com.example.e_sbitar.fragments.ProfileFragment;
import com.example.e_sbitar.fragments.ResourcesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomNavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        userModel = (UserModel) getIntent().getSerializableExtra("UserInfos");

        bottomNav = findViewById(R.id.bottomNavigationLayout);
        bottomNav.setOnItemSelectedListener(navListener);

        //Here we're setting the Home fragment as default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                new HealthFragment()).commit();
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_health:
                            selectedFragment = new HealthFragment();
                            break;
                        case R.id.nav_res:
                            selectedFragment = new ResourcesFragment();
                            break;
                        case R.id.nav_med:
                            selectedFragment = new MedicationFragment();
                            break;
                        case R.id.nav_news:
                            selectedFragment = new NewsFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        finish();
    }
}