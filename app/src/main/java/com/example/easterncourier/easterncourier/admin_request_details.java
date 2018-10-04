package com.example.easterncourier.easterncourier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class admin_request_details extends AppCompatActivity implements LocationListener {
    TextView requestIdTv, senderNameTv, receiverNameTv, dateRequestedTv, packageDescriptiontv;
    Button viewPackageImageBtn, viewSenderLocationBtn, viewreceiverLocationBtn,assignCourierBtn,onTheWayBtn;
    String fromCourier;
    public static String tvLongi;
    public static String tvLati;
    LocationManager locationManager;
    public static boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_details);
        CheckPermission();


        requestIdTv=findViewById(R.id.requestIdTv);
        senderNameTv=findViewById(R.id.senderFullNameTv);
        receiverNameTv=findViewById(R.id.receiverNameTv);
        dateRequestedTv=findViewById(R.id.dateRequestedTv);
        packageDescriptiontv=findViewById(R.id.packageDescTv);


        viewPackageImageBtn=findViewById(R.id.nshowPackageImageBtn);
        viewSenderLocationBtn=findViewById(R.id.showSenderLocationBtn);
        viewreceiverLocationBtn=findViewById(R.id.showReceiverLocationBtn);
        assignCourierBtn=findViewById(R.id.assignCourierBtn);
        onTheWayBtn=findViewById(R.id.onTheWayBtn);

        /*if (getIntent().getExtras().getString("fromCourier").equals("YES")){
            assignCourierBtn.setVisibility(View.INVISIBLE);
        }
        else {
            assignCourierBtn.setVisibility(View.VISIBLE);
        }*/




        requestIdTv.setText(getIntent().getExtras().getString("Request Id"));
        senderNameTv.setText(getIntent().getExtras().getString("Sender Name"));
        receiverNameTv.setText(getIntent().getExtras().getString("Receiver Name"));
        dateRequestedTv.setText(getIntent().getExtras().getString("Date Requested"));
        packageDescriptiontv.setText(getIntent().getExtras().getString("Package Description"));



        onTheWayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                getLocation();

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Client Request").child(requestIdTv.getText().toString());
                databaseReference.child("requestFinish").setValue("On The Way");







            }
        });


        viewSenderLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(admin_request_details.this,senderMapLocationPrototype.class);
                intent.putExtra("Longitude",getIntent().getExtras().getString("Sender Longitude"));
                intent.putExtra("Latitude",getIntent().getExtras().getString("Sender Latitude"));

                startActivity(intent);
            }
        });

        assignCourierBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_request_details.this,com.example.easterncourier.easterncourier.admin_choose_courier.class);
                intent.putExtra("Request Id",requestIdTv.getText());
                startActivity(intent);
            }
        });






    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);

            final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Courier Accounts").child(getIntent().getExtras().getString("Courier Id"));
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                            /*if (dataSnapshot1.getValue(addCourierAccountItem.class).getCourierId().equals(getIntent().getExtras().get("Courier Id"))){
                                addCourierAccountItem addCourierAccountItem1=dataSnapshot1.getValue(addCourierAccountItem.class);


                            }*/

                        databaseReference1.child("courierLocationLatitude").setValue(tvLati);
                        databaseReference1.child("courierLocationLongitude").setValue(tvLongi);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onLocationChanged( Location location) {
        tvLongi = String.valueOf(location.getLongitude());
        tvLati = String.valueOf(location.getLatitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(admin_request_details.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Enabled new provider!" + provider,
                Toast.LENGTH_SHORT).show();
    }
}
