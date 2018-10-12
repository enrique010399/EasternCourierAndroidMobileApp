package com.example.easterncourier.easterncourier;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class courierLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, ResultCallback<Status> {
    ArrayList<admin_request_item> list;
    ArrayList<addCourierAccountItem> list1;
    DatabaseReference reference, reference1;
    RecyclerView recyclerView;
    String assignCourierId;
    String clientUserName;
    String courierLocLatitude, courierLocLongitude;
    public static boolean isRunning;
    private GoogleMap mMap;
    public LatLng courierLocation;
    DatabaseReference ref;
    GeofencingRequest geoRequest;
    GoogleApiClient client;
    public static final String CHANNEL_1_ID="channel1";
    public static final String CHANNEL_2_ID="channel2";
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

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1=new NotificationChannel(CHANNEL_1_ID,"Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Eastern Courier");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Double clientLatitude = Double.parseDouble(getIntent().getExtras().getString("Sender Latitude"));
        Double clientLongitude = Double.parseDouble(getIntent().getExtras().getString("Sender Longitude"));
        final LatLng clientLocation = new LatLng(clientLatitude, clientLongitude);

        //ref=FirebaseDatabase.getInstance().getReference("Courier Accounts").child("").child("");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Courier Accounts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getValue(addCourierAccountItem.class).getCourierId().equals(getIntent().getExtras().get("Courier Id"))) {
                        addCourierAccountItem addCourierAccountItem1 = dataSnapshot1.getValue(addCourierAccountItem.class);

                        Marker client;
                        //client = mMap.addMarker(new MarkerOptions().position(courierLocation).title("Courier Location"));
                        mMap.clear();
                        courierLocation = new LatLng(Double.parseDouble(addCourierAccountItem1.getCourierLocationLatitude().toString()), Double.parseDouble(addCourierAccountItem1.getCourierLocationLongitude().toString()));
                        mMap.addMarker(new MarkerOptions().position(courierLocation).title("Courier Location"));
                        mMap.addMarker(new MarkerOptions().position(clientLocation).title("Your Requested Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        //mMap.addPolyline(new PolylineOptions().add(courierLocation).width(8f).color(Color.BLUE));
                        mMap.setMaxZoomPreference(40.0f);
                        mMap.setMinZoomPreference(18.0f);
                        mMap.addCircle(new CircleOptions().center(clientLocation).radius(25.0).strokeWidth(3f).strokeColor(Color.CYAN).fillColor(Color.argb(70,0,255,255)));
                        if (distance(clientLocation.latitude,clientLocation.longitude,courierLocation.latitude,courierLocation.longitude)<0.0155343){
                            /*Notification notification=new NotificationCompat.Builder(courierLocation.this,CHANNEL_1_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Eastern Courier")
                                    .setContentText("The courier is within 25 meters away!!!!")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE).build();*/

                            android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(courierLocation.this);
                            builder.setMessage("The Courier is within 25 meters away from your requested location")
                                    .create().show();



                        }

                        /*Geofence geofence = createGeoFence(clientLocation, 0.025f);
                        geoRequest = createGeoRequest(geofence);
                        addGeofence(geofence);*/
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(courierLocation));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    private void addGeofence(Geofence geofence) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(client, geoRequest, createGeofencingPendingIntent())
                .setResultCallback(this);

    }
    PendingIntent geoFencePendingIntent;
    private PendingIntent createGeofencingPendingIntent() {
        if (geoFencePendingIntent!=null){
            return geoFencePendingIntent;
        }
        Intent intent=new Intent(this,GeofenceTransitionService.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private GeofencingRequest createGeoRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence).build();
    }


    private Geofence createGeoFence(LatLng position, float v) {
        return new Geofence.Builder()
                .setRequestId("Client Location Geofence").setCircularRegion(position.latitude,position.longitude,v)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER| Geofence.GEOFENCE_TRANSITION_EXIT).build();
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

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        drawGeofence();
    }
    Circle geoFenceLimits;
    private void drawGeofence() {
        Double clientLatitude = Double.parseDouble(getIntent().getExtras().getString("Sender Latitude"));
        Double clientLongitude = Double.parseDouble(getIntent().getExtras().getString("Sender Longitude"));
        final LatLng clientLocation = new LatLng(clientLatitude, clientLongitude);
        if (geoFenceLimits!=null){
            geoFenceLimits.remove();
        }
        CircleOptions circleOptions=new CircleOptions().center(clientLocation).strokeColor(Color.CYAN).fillColor(Color.argb(70,0,255,255))
                .radius(0.025f);
        geoFenceLimits=mMap.addCircle(circleOptions);

    }
}
