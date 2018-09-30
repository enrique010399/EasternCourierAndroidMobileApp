package com.example.easterncourier.easterncourier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class admin_couriers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_couriers);

        final Button addCourierAccountBtn=findViewById(R.id.addNewAccount);
        addCourierAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(admin_couriers.this,addCourierAccount.class);
                startActivity(intent);

            }
        });


        /*RecyclerView recyclerView=findViewById(R.id.rv_listCouriers);
        List<admin_couriers_item> mListAdminCourier=new ArrayList<>();


        mListAdminCourier.add(new admin_couriers_item(R.drawable.marlon,"Marlon Tangcoy","5"));
        mListAdminCourier.add(new admin_couriers_item(R.drawable.lawrence,"Lawrence Jay De Guzman","3"));
        mListAdminCourier.add(new admin_couriers_item(R.drawable.reever,"Reever Leis Mercado","3"));


        Adapter_admin_couriers adapter_admin_couriers=new Adapter_admin_couriers(this,mListAdminCourier);
        recyclerView.setAdapter(adapter_admin_couriers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/

    }
}
