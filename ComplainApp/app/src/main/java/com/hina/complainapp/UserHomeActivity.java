package com.hina.complainapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.navigation.NavigationView;
import com.hina.complainapp.fragments.ComplainListFragment;
import com.hina.complainapp.fragments.MainFragment;
import com.hina.complainapp.fragments.RegsiterComplainFragment;
import com.hina.complainapp.fragments.ProfileFragment;
import com.hina.complainapp.session.SessionManager;

import java.util.HashMap;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MainFragment.OnFragmentItemSelectedListener {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MainFragment mainFragment;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        final TextView headerFullName = headerView.findViewById(R.id.header_fullname);
        final TextView headerEmail = headerView.findViewById(R.id.header_email);

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();

        String full_name = userDetails.get(SessionManager.KEY_FULLNAME);
        String email_text = userDetails.get(SessionManager.KEY_EMAIL);

        headerEmail.setText(email_text);
        headerFullName.setText(full_name);

        mainFragment = new MainFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,mainFragment);
        fragmentTransaction.commit();// add the fragment

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.nav_home:
                loadFragment(new MainFragment());
                break;
            case R.id.nav_add_complain:
                loadFragment(new RegsiterComplainFragment());
                break;
            case R.id.nav_list_complain:
                loadFragment(new ComplainListFragment());
                break;
            case R.id.nav_profile:
                loadFragment(new ProfileFragment());
                break;
            case R.id.nav_logout:
                SessionManager userSession = new SessionManager(UserHomeActivity.this);
                userSession.logout();
                startActivity(new Intent(UserHomeActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                    //

        }
        return true;
    }

    private void loadFragment(Fragment secondFragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,secondFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPostComplainButtonSelected() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,new RegsiterComplainFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onListComplainButtonSelected() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,new ComplainListFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
