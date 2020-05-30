package com.example.android.parkingshare.Utils;

import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.parkingshare.Home.HomeActivity;
import com.example.android.parkingshare.Maps.MapsActivity;
import com.example.android.parkingshare.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ParkHolder> {

    private ArrayList<String> addressArrayList;
    private ArrayList<String> distanceList;
    private OnAdresListener mOnAdresListener;


    public RecyclerAdapter(ArrayList<String> addressArrayList, ArrayList<String> distanceList, OnAdresListener onAdresListener) {
        this.addressArrayList = addressArrayList;
        this.distanceList = distanceList;
        this.mOnAdresListener= onAdresListener;
    }

    @NonNull
    @Override
    public ParkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row,parent,false);

        return new ParkHolder(view,mOnAdresListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParkHolder holder, final int position) {

        holder.adresText.setText(addressArrayList.get(position));
        holder.distanceText.setText(distanceList.get(position));

    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }

    class ParkHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView  distanceText;
            TextView adresText;
            ImageView directionIcon;
            OnAdresListener onAdresListener;

        public ParkHolder(@NonNull View itemView,OnAdresListener onAdresListener) {
            super(itemView);

            distanceText = itemView.findViewById(R.id.distance_text);
            adresText  = itemView.findViewById(R.id.adres_text);
            directionIcon = itemView.findViewById(R.id.directions_icon);
            this.onAdresListener = onAdresListener;

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onAdresListener.OnAdresClick(getAdapterPosition());
        }
    }
    public  interface OnAdresListener{
        void OnAdresClick(int position);
    }



}
