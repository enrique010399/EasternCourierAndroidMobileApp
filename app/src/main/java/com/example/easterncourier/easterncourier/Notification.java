package com.example.easterncourier.easterncourier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



        Window w=getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RecyclerView recyclerView=findViewById(R.id.rv_list);
        List<notification_item> mList=new ArrayList<>();
        mList.add(new notification_item("Your package for Christian Bautista was delivered...","Just Now",R.drawable.deliverd_30px));
        mList.add(new notification_item("Your package for Christian Bautista  was on the way...","18 minutes ago",R.drawable.on_the_way_30px));
        mList.add(new notification_item("Your book request was accepted...","2 hours ago",R.drawable.request_accepted30px));
        Adapter adapter=new Adapter(this,mList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Button viewButton= (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewNotificationIntent=new Intent(getApplicationContext(),viewCurrentLocation.class);
                startActivity(viewNotificationIntent);
            }

        });*/

    }
}
