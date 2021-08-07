package com.app.glucosemonitor.baseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.auth.LoginActivity;
import com.app.glucosemonitor.utils.CommonVariables;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_record_glucose, R.id.nav_display_glucose)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Nav Drawer Header Welcome Message and Email
        View headerView = navigationView.getHeaderView(0);
        TextView txtNavHeaderWelcome = headerView.findViewById(R.id.txt_nav_header_welcome);
        TextView txtNavBodyEmail = headerView.findViewById(R.id.txt_nav_body_email);

        int firstSpace = CommonVariables.loggedInUserDetails.name.indexOf(" ");
        if (firstSpace >= 0) {
            String firstName = CommonVariables.loggedInUserDetails.name.substring(0, firstSpace);
            String welcomeMsgUser = "Welcome " + firstName;
            String userEmail = CommonVariables.loggedInUserDetails.email;
            txtNavHeaderWelcome.setText(welcomeMsgUser);
            txtNavBodyEmail.setText(userEmail);
        } else {
            String welcomeMsgUser = "Welcome " + CommonVariables.loggedInUserDetails.name;
            String userEmail = CommonVariables.loggedInUserDetails.email;
            txtNavHeaderWelcome.setText(welcomeMsgUser);
            txtNavBodyEmail.setText(userEmail);
        }
    }

    private void logout() {
        CommonVariables.loggedInUserDetails = null;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}