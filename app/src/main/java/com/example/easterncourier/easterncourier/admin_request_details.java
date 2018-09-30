package com.example.easterncourier.easterncourier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class admin_request_details extends AppCompatActivity {
    TextView requestIdTv, senderNameTv, receiverNameTv, dateRequestedTv, packageDescriptiontv;
    Button viewPackageImageBtn, viewSenderLocationBtn, viewreceiverLocationBtn,assignCourierBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_details);

        requestIdTv=findViewById(R.id.requestIdTv);
        senderNameTv=findViewById(R.id.senderFullNameTv);
        receiverNameTv=findViewById(R.id.receiverNameTv);
        dateRequestedTv=findViewById(R.id.dateRequestedTv);
        packageDescriptiontv=findViewById(R.id.packageDescTv);

        viewPackageImageBtn=findViewById(R.id.showPackageImageBtn);
        viewSenderLocationBtn=findViewById(R.id.showSenderLocationBtn);
        viewreceiverLocationBtn=findViewById(R.id.showReceiverLocationBtn);
        assignCourierBtn=findViewById(R.id.assignCourierBtn);

        requestIdTv.setText(getIntent().getExtras().getString("Request Id"));
        senderNameTv.setText(getIntent().getExtras().getString("Sender Name"));
        receiverNameTv.setText(getIntent().getExtras().getString("Receiver Name"));
        dateRequestedTv.setText(getIntent().getExtras().getString("Date Requested"));
        packageDescriptiontv.setText(getIntent().getExtras().getString("Package Description"));


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
}
