package com.MAD.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Toolbar tolbar = findViewById(R.id.toolbar);
        setSupportActionBar(tolbar);
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, tolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CovidStats()).commit();
            navigationView.setCheckedItem(R.id.nav_stat);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            Intent startIntent = new Intent(MainActivity.this, Login.class);
            startActivity(startIntent);
            finish();
        }
        else
        {
            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(currentUser!=null){
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_stat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CovidStats()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MsgFragment()).commit();
                break;
            case R.id.nav_map:
                Intent intent=new Intent(this,NavHospitals.class);
                startActivity(intent);
                break;
            case R.id.nav_mail:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReportFragment()).commit();
                break;
            case R.id.nav_logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                onStart();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                onStart();
                                break;
                        }
                    }
                };



                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case R.id.accountSettings:

                Intent startIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(startIntent);

                break;
            case R.id.allUsers:
                Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(userIntent);

                break;

            case R.id.contact:
                Toast.makeText(this, "Call us \t 0774\t78\t79\t79", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_share:
                Intent in=new Intent(Intent.ACTION_SEND);
                in.setType("text/Plain");
                String shareBody="E:\\Note\\Notes\\MAD\\HealthApp\\app\\build\\outputs\\apk\\debug";
                String shareSub="Hi, Do you like to get newest information about COVID-19";
                in.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                in.putExtra(Intent.EXTRA_TEXT,shareSub);
                startActivity(in.createChooser(in,"shareUsing"));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
