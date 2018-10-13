package com.example.easterncourier.easterncourier;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;



import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Book extends AppCompatActivity implements LocationListener {

    private static final int PICK_IMAGE_REQUEST=1;
    private Button mButtonChooseImage;
    //private Button mButtonUpload;
    private ImageView mImageView;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;



    public static String tvLongi;
    public static String tvLati;


    LocationManager locationManager;
    DatabaseReference databaseClientRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        CheckPermission();


        //FirebaseDataBase
        databaseClientRequest = FirebaseDatabase.getInstance().getReference("Client Request");

        mButtonChooseImage=findViewById(R.id.uploadPackageImage);
        mImageView=findViewById(R.id.packageImage);
        final EditText dropingPointLocation=findViewById(R.id.droppingPointEditText);
        final EditText receiverName=findViewById(R.id.receiverNameEditText);
        final EditText packageDescription=findViewById(R.id.requestDescription);
        Button submitRequest=findViewById(R.id.submitRequest),
        getDroppingPointLocation=findViewById(R.id.getDropingPointLocation);
        getDroppingPointLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Book.this, chooseDroppingPointLocation.class);
                intent.putExtra("clientLatitude",tvLati);
                intent.putExtra("clientLongitude",tvLongi);
                startActivity(intent);
            }
        });

        //FireBaseStorage
        mStorageRef= FirebaseStorage.getInstance().getReference("Client Request");
        mDatabaseRef=databaseClientRequest;



        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(receiverName.getText().toString()) && !TextUtils.isEmpty(dropingPointLocation.getText()
                .toString()) && !TextUtils.isEmpty(packageDescription.getText().toString())){

                    uploadFile();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    System.out.println(formatter.format(date));
                    String id=databaseClientRequest.push().getKey();
                    BookRequest bookRequest=new BookRequest(id,tvLati,tvLongi,"none","none",receiverName.getText().toString(),packageDescription.getText().toString(),getIntent().getExtras().getString("username"),
                            getIntent().getExtras().getString("clientFullName"), formatter.format(date)+"",mImageUri+"","Not Assign","Not Assign","Not Assign","Not Yet");
                    databaseClientRequest.child(id).setValue(bookRequest);
                    Toast.makeText(Book.this,"Request Sent",Toast.LENGTH_LONG).show();



                }
                else{
                    Toast.makeText(Book.this,"Please fill up all fields!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getFilextension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){

        if (mImageUri != null) {
            final StorageReference imgReference = mStorageRef;
            UploadTask uploadTask = imgReference.putFile(mImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return imgReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri taskResult = task.getResult();
                        //BookUploadImage bookUploadImage = new BookUploadImage("package", taskResult.toString());
                        //mDatabaseRef.push().setValue(bookUploadImage);
                        Toast.makeText(Book.this,"Uploaded Successfully",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri =data.getData();
            Picasso.get().load(mImageUri).into(mImageView);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
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
    public void onLocationChanged(Location location) {

        TextView senderLocation=findViewById(R.id.senderLOcation);

        tvLongi = String.valueOf(location.getLongitude());
        tvLati = String.valueOf(location.getLatitude());

        senderLocation.setText(tvLongi+" "+tvLati);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(Book.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
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
