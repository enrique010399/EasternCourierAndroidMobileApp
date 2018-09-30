package com.example.easterncourier.easterncourier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter_admin_couriers extends RecyclerView.Adapter<Adapter_admin_couriers.myViewHolder>{
    Context mContextAdminCourier;
    List<admin_couriers_item> mDataAdminCourier;

    public Adapter_admin_couriers(Context mContextAdminCourier, List<admin_couriers_item> mDataAdminCourier) {
        this.mContextAdminCourier = mContextAdminCourier;
        this.mDataAdminCourier = mDataAdminCourier;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContextAdminCourier);
        View v=inflater.inflate(R.layout.admin_couriers,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.courierPicture.setImageResource(mDataAdminCourier.get(position).getCourierPicture());
        holder.courierName.setText(mDataAdminCourier.get(position).getCourierName());
        holder.numOfDesignatedClients.setText(mDataAdminCourier.get(position).getNumOfDesignatedClients());
    }

    @Override
    public int getItemCount() {
        return mDataAdminCourier.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView courierPicture;
        TextView courierName,numOfDesignatedClients;
        public myViewHolder(View itemView) {
            super(itemView);
            courierPicture=itemView.findViewById(R.id.courierPicture);
            courierName=itemView.findViewById(R.id.courierName);
            numOfDesignatedClients=itemView.findViewById(R.id.numOfDesignatedClients);
        }
    }
}
