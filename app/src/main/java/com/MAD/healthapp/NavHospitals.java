package com.MAD.healthapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NavHospitals extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    double currentLatitude,currentLongitude;
    Location myLocation;

    private static final int REQUEST_CHECK_SETTING_GPS=0x1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_hospitals);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUPGClient();
    }

    private void setUPGClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this,0,this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
    }

    private void checkPermission() {
        int permissionLocation= ContextCompat.checkSelfPermission(NavHospitals.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listpermission= new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED){
            listpermission.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if(!listpermission.isEmpty()){
                ActivityCompat.requestPermissions(this,
                        listpermission.toArray(new String[listpermission.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);

            }
        }else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionLocation=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionLocation==PackageManager.PERMISSION_GRANTED){

            getMyLocation();

        }else {
            checkPermission();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        myLocation=location;
        if(myLocation!=null){
            currentLatitude=location.getLatitude();
            currentLongitude=location.getLongitude();


            BitmapDescriptor icon=  BitmapDescriptorFactory.fromResource(R.drawable.navigation);


            // Add a marker in Sydney and move the camera
            mMap.clear();
            LatLng me = new LatLng(currentLatitude, currentLongitude);
            mMap.addMarker(new MarkerOptions().position(me).title("Me"));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,currentLongitude), 15.0f));


            getNearByHospital();
        }



    }



    private void getNearByHospital() {

        StringBuilder stringBuilder= new StringBuilder("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?");
        stringBuilder.append("location="+String.valueOf(currentLatitude)+","+String.valueOf(currentLongitude)) ;

        stringBuilder.append("&radius=10000");
        stringBuilder.append("&type=hospital");
        stringBuilder.append("&key="+getResources().getString(R.string.google_maps_key));
        String url=stringBuilder.toString();
        Object dataTransfer[]=new Object[2];
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;

        NearPlaces nearPlaces=new NearPlaces();
        nearPlaces.execute(dataTransfer);
    }

    private void getMyLocation(){
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(NavHospitals.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(NavHospitals.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {


                                        myLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(mGoogleApiClient);


                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(NavHospitals.this,
                                                REQUEST_CHECK_SETTING_GPS);


                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });

                }
            }
        }
    }
}
