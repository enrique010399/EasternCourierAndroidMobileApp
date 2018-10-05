package com.example.easterncourier.easterncourier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class admin_request_details extends AppCompatActivity {
    TextView requestIdTv, senderNameTv, receiverNameTv, dateRequestedTv, packageDescriptiontv;
    Button viewPackageImageBtn, viewSenderLocationBtn, viewreceiverLocationBtn, assignCourierBtn, onTheWayBtn;
    String fromCourier;
    public static String tvLongi;
    public static String tvLati;
    public static boolean isRunning;
    LocationManager locationManager;
    LocationListener locationListener;


    TimerTask scanTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_details);
        //CheckPermission();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                //tvLongi = String.valueOf(location.getLongitude());
                //tvLati = String.valueOf(location.getLatitude());
                Log.d("Location", location.toString());
                try{
                    final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Courier Accounts").child(getIntent().getExtras().getString("Courier Id"));
                    databaseReference1.addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){


                                databaseReference1.child("courierLocationLatitude").setValue(String.valueOf(location.getLatitude()));
                                databaseReference1.child("courierLocationLongitude").setValue(String.valueOf(location.getLongitude()));

                                //Toast.makeText(admin_request_details.this,tvLati,Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }catch(Exception e){
                    Log.d("Location", e.getMessage().toString());
                }



                //getLocation();

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
                //Toast.makeText(this, "Enabled new provider!" + provider, Toast.LENGTH_SHORT).show();
                Toast.makeText(admin_request_details.this, "Enable New Provider" + provider, Toast.LENGTH_SHORT).show();
                ;
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
                return;
            }

        }
        else{
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                return;
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);

            }
        }


        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);




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

        //if (getIntent().getExtras().getString("fromCourier").equals("YES")){
            //assignCourierBtn.setVisibility(View.INVISIBLE);
        //}
        //else {
          //  assignCourierBtn.setVisibility(View.VISIBLE);
        //}




        requestIdTv.setText(getIntent().getExtras().getString("Request Id"));
        senderNameTv.setText(getIntent().getExtras().getString("Sender Name"));
        receiverNameTv.setText(getIntent().getExtras().getString("Receiver Name"));
        dateRequestedTv.setText(getIntent().getExtras().getString("Date Requested"));
        packageDescriptiontv.setText(getIntent().getExtras().getString("Package Description"));



        onTheWayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    ==PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);


        }
    }

}
