package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedViewModel model = ViewModelProviders.of(this).get(SharedViewModel.class);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        model.setFragmentManager(fragmentManager);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        final Fragment homeFragment = new homeFragment();
        final Fragment tab2Fragment = new Tab2Fragment();
        final Fragment tab3Fragment = new Tab3Fragment();
        final Fragment tab4Fragment = new Tab4Fragment();
        ft.replace(R.id.flContainer, homeFragment);
        ft.commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.home:
                        FragmentTransaction homeFragmentTransaction = fragmentManager.beginTransaction();
                        homeFragmentTransaction.replace(R.id.flContainer, homeFragment).commit();
                        return true;
                    case  R.id.Tab2:
                        FragmentTransaction tab2FragmentTransaction = fragmentManager.beginTransaction();
                        tab2FragmentTransaction.replace(R.id.flContainer, tab2Fragment).commit();
                        return true;
                    case R.id.Tab3:
                        FragmentTransaction tab3FragmentTransaction = fragmentManager.beginTransaction();
                        tab3FragmentTransaction.replace(R.id.flContainer, tab3Fragment).commit();
                        return true;
                    case R.id.Gallery:
                        FragmentTransaction tab4FragmentTransaction = fragmentManager.beginTransaction();
                        tab4FragmentTransaction.replace(R.id.flContainer, tab4Fragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
