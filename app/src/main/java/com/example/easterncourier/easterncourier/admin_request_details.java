package com.example.easterncourier.easterncourier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Date;
import java.util.TimerTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

public class admin_request_details extends AppCompatActivity implements courier_enter_requestid_dialog.ExampleDialogListener {
    TextView requestIdTv, senderNameTv, receiverNameTv, dateRequestedTv, packageDescriptiontv;
    Button viewPackageImageBtn, viewSenderLocationBtn, viewreceiverLocationBtn, assignCourierBtn, onTheWayBtn, finishDeliveryBtn, payBtn;
    String fromCourier;
    public static String tvLongi;
    public static String tvLati;
    public static boolean isRunning;
    LocationManager locationManager;
    LocationListener locationListener;
    public static String finish;

    TimerTask scanTask;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //LocationRequest mLocationRequest;


    private static final String TAG = admin_request_details.class.getSimpleName();
    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_details);
        //CheckPermission();


        requestIdTv = findViewById(R.id.requestIdTv);
        senderNameTv = findViewById(R.id.senderFullNameTv);
        receiverNameTv = findViewById(R.id.receiverNameTv);
        dateRequestedTv = findViewById(R.id.dateRequestedTv);
        packageDescriptiontv = findViewById(R.id.packageDescTv);


        viewPackageImageBtn = findViewById(R.id.nshowPackageImageBtn);
        viewSenderLocationBtn = findViewById(R.id.showSenderLocationBtn);
        viewreceiverLocationBtn = findViewById(R.id.showReceiverLocationBtn);
        assignCourierBtn = findViewById(R.id.assignCourierBtn);
        onTheWayBtn = findViewById(R.id.onTheWayBtn);
        finishDeliveryBtn = findViewById(R.id.finishDeliveryBtn);
        Button stopLocationBtn=findViewById(R.id.stopBtn);



        String ifCourier = getIntent().getExtras().getString("ifCourier");
        if (ifCourier.equals("Courier")) {
            assignCourierBtn.setVisibility(View.INVISIBLE);
        } else if (ifCourier.equals("Admin")) {
            assignCourierBtn.setVisibility(View.VISIBLE);
            onTheWayBtn.setVisibility(View.INVISIBLE);
            finishDeliveryBtn.setVisibility(View.INVISIBLE);
            stopLocationBtn.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(this, "Unknown account type", Toast.LENGTH_LONG);
        }



        ButterKnife.bind(this);

        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);



        requestIdTv.setText(getIntent().getExtras().getString("Request Id"));
        senderNameTv.setText(getIntent().getExtras().getString("Sender Name"));
        receiverNameTv.setText(getIntent().getExtras().getString("Receiver Name"));
        dateRequestedTv.setText(getIntent().getExtras().getString("Date Requested"));
        packageDescriptiontv.setText(getIntent().getExtras().getString("Package Description"));


        finishDeliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestIdTv.setVisibility(View.INVISIBLE);
                courier_enter_requestid_dialog courier_enter_requestid_dialog1 = new courier_enter_requestid_dialog();
                //courier_enter_requestid_dialog1.trueRequestId=requestIdTv.getText().toString();
                courier_enter_requestid_dialog1.show(getSupportFragmentManager(), "Request Id");
            }


        });


        onTheWayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Client Request").child(requestIdTv.getText().toString());
                databaseReference.child("requestFinish").setValue("On The Way");

                startLocationButtonClick();

            }
        });


        stopLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
            }
        });





        viewSenderLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_request_details.this, senderMapLocationPrototype.class);
                intent.putExtra("Longitude", getIntent().getExtras().getString("Sender Longitude"));
                intent.putExtra("Latitude", getIntent().getExtras().getString("Sender Latitude"));
                startActivity(intent);
            }
        });

        viewreceiverLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_request_details.this, receiverLocation.class);
                intent.putExtra("receiverLatitude", getIntent().getExtras().getString("Receiver Latitude"));
                intent.putExtra("receiverLongitude", getIntent().getExtras().getString("Receiver Longitude"));
                startActivity(intent);
            }
        });

        assignCourierBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_request_details.this, com.example.easterncourier.easterncourier.admin_choose_courier.class);
                intent.putExtra("Request Id", requestIdTv.getText());
                startActivity(intent);
            }
        });
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //location is received
                mCurrentLocation = locationResult.getLastLocation();
                locationResult.getLastLocation().getLatitude();
                mLastUpdateTime= java.text.DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };


        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }



    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);
    }

    private void toggleButtons() {
        if (mRequestingLocationUpdates) {
            //btnStartUpdates.setEnabled(false);
            //btnStopUpdates.setEnabled(true);
        } else {
            //btnStartUpdates.setEnabled(true);
            //btnStopUpdates.setEnabled(false);
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            //latitude.setText("Latitude: " + mCurrentLocation.getLatitude());
            //longitude.setText("Longitude: " + mCurrentLocation.getLongitude());
            Toast.makeText(admin_request_details.this,mCurrentLocation.getLatitude()+"",Toast.LENGTH_LONG).show();
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Courier Accounts Location");
            GeoFire geoFire= new GeoFire(databaseReference);
            geoFire.setLocation(getIntent().getExtras().getString("Courier Id"),new GeoLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));


        }

        toggleButtons();
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(admin_request_details.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(admin_request_details.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        toggleButtons();
                    }
                });
    }

    public void startLocationButtonClick(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            //stopLocationUpdates();
        }
    }








    @Override
    public void applyTexts(String requestId) {
        if (requestId.equals(requestIdTv.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(admin_request_details.this);
            builder.setMessage("The request Id is correct")
                    .create().show();
            final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Client Request").child(requestIdTv.getText().toString());
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        databaseReference1.child("requestFinish").setValue("Finished");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(admin_request_details.this);
            builder.setMessage("The request Id is wrong please try again")
                    .create().show();
        }
    }

    /*protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Courier Accounts");
        GeoFire geoFire= new GeoFire(databaseReference);
        geoFire.setLocation(getIntent().getExtras().getString("Courier Id"),new GeoLocation(location.getLatitude(),location.getLongitude()));
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Courier Accounts");
        GeoFire geoFire= new GeoFire(databaseReference);
        geoFire.removeLocation(getIntent().getExtras().getString("Courier Id"));
    }*/



    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    ==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            }
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }*/
}
