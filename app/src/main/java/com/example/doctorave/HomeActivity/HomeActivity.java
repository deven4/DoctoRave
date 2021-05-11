package com.example.doctorave.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.doctorave.MainActivity;
import com.example.doctorave.ModelClasses.User;
import com.example.doctorave.R;
import com.example.doctorave.Utils.HelperMethods;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private static final String TAG = "HOME_ACTIVITY";

    Menu menu;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    HomeFragmentSearchCommunicator mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar1);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);


        menu = toolbar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Patient by name...");
        searchView.setOnQueryTextListener(this);


        // Using ActionBarDrawerToggleClass to connect DrawerLayout, toolbar with NavigationView.
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.Open_Drawer, R.string.Close_Drawer);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment_holder, homeFragment)
                    .commit();
        }


        navigationView.setNavigationItemSelectedListener(this);
        getUserData();
    }


    private void inflateNavigationHeader(User user) {

        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.textView4);
        TextView email = headerView.findViewById(R.id.textView5);

        name.setText(user.getName());
        email.setText(user.getEmail());
    }


    public void getUserData() {

        FirebaseFirestore.getInstance().collection(getString(R.string.DB_USERS))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                if (user != null)
                    inflateNavigationHeader(user);
            } else
                HelperMethods.showToastInCenter(HomeActivity.this, task.getException().getMessage());
        });
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.homeIcon) {
            menu.findItem(R.id.search).setVisible(true);
            toolbar.setTitle("Patients");
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_holder, new HomeFragment())
                    .commit();
        } else if (itemId == R.id.signOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setMessage("Confirm Log Out?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            });
            builder.create().show();
        } else {
            menu.findItem(R.id.search).setVisible(false);
            toolbar.setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_holder, new SettingsFragment())
                    .commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void passCommunicator(HomeFragmentSearchCommunicator mListener) {
        this.mListener = mListener;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mListener != null)
            mListener.search(newText);
        else
            HelperMethods.showToastInCenter(this);
        return false;
    }


    public interface HomeFragmentSearchCommunicator {
        void search(String text);
    }
}