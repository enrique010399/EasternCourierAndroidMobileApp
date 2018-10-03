package com.example.easterncourier.easterncourier;

import android.location.Location;
import android.location.LocationListener;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class courierLocation extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {
    ArrayList<admin_request_item> list;
    ArrayList<addCourierAccountItem> list1;
    DatabaseReference reference,reference1;
    RecyclerView recyclerView;
    String assignCourierId;
    String clientUserName;
    String courierLocLatitude,courierLocLongitude;
    public static boolean isRunning;
    private GoogleMap mMap;
    public LatLng courierLocation;


    private static final String TAG = "courierLocation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_location);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Courier Accounts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    if (dataSnapshot1.getValue(addCourierAccountItem.class).getCourierId().equals(getIntent().getExtras().get("Courier Id"))){
                        addCourierAccountItem addCourierAccountItem1=dataSnapshot1.getValue(addCourierAccountItem.class);

                        courierLocation= new LatLng(Double.parseDouble(addCourierAccountItem1.getCourierLocationLatitude().toString()),Double.parseDouble(addCourierAccountItem1.getCourierLocationLongitude().toString()) );
                        mMap.addMarker(new MarkerOptions().position(courierLocation).title("Courier Location"));
                        mMap.setMaxZoomPreference(30.0f);
                        mMap.setMinZoomPreference(18.0f);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(courierLocation));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // Add a marker in Sydney and move the camera


        /*ExecutorService executerService = Executors.newCachedThreadPool();
        executerService.execute(new Runnable() {
            @Override
            public void run() {
                isRunning=true;
                while (isRunning){
                    reference= FirebaseDatabase.getInstance().getReference().child("Client Request");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                //yung
                                //(requestAssignCourierId ay hindi null)=Done and yung (clientUserName ay equal sa userName
                                //ni Client)=Done
                                if (dataSnapshot1.getValue(admin_request_item.class).getClientUserName().equals(clientUserName)
                                        && !dataSnapshot1.getValue(admin_request_item.class).getRequestAssignedCourierId().equals("Not Assign")){
                                    admin_request_item admin_request_item1= dataSnapshot1.getValue(admin_request_item.class);
                                    list.add(admin_request_item1);
                                    assignCourierId=dataSnapshot1.getValue(admin_request_item.class).getRequestAssignedCourierId();

                                    list1=new ArrayList<addCourierAccountItem>();
                                    reference1= FirebaseDatabase.getInstance().getReference().child("Courier Accounts");
                                    reference1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                                if (dataSnapshot1.getValue(addCourierAccountItem.class).getCourierId().equals(assignCourierId)){
                                                    addCourierAccountItem addCourierAccountItem1=dataSnapshot1.getValue(addCourierAccountItem.class);
                                                    list1.add(addCourierAccountItem1);

                                                    //check if yung value nung requestFinish ay "On The Way" na sa ClientRequest
                                                    if (dataSnapshot1.getValue(admin_request_item.class).getRequestFinish().equals("On The Way")){

                                                        final ArrayList<addCourierAccountItem> courierAccounts;
                                                        courierAccounts=new ArrayList<addCourierAccountItem>();
                                                        DatabaseReference blah;
                                                        blah= FirebaseDatabase.getInstance().getReference().child("Courier Accounts");
                                                        blah.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (final DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){


                                                                    ExecutorService executerService = Executors.newCachedThreadPool();
                                                                    executerService.execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {



                                                                            addCourierAccountItem addCourierAccountItem1=dataSnapshot1.getValue(addCourierAccountItem.class);
                                                                            courierLocLatitude=addCourierAccountItem1.getCourierLocationLatitude()+"";
                                                                            courierLocLongitude=addCourierAccountItem1.getCourierLocationLongitude()+"";
                                                                            courierLocation = new LatLng(Double.parseDouble(courierLocLatitude), Double.parseDouble(courierLocLongitude));
                                                                            Log.i(TAG,courierLocLatitude+" "+courierLocLongitude);
                                                                            //courierLocLatitude=addCourierAccountItem1.getCourierLocationLatitude()+"";
                                                                            //courierLocLongitude=addCourierAccountItem1.getCourierLocationLongitude()+"";




                                                                        }
                                                                    });

                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }



                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
