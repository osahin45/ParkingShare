package com.example.android.parkingshare.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class ParkingLocation implements Parcelable {
    private GeoPoint geoPoint;

    public ParkingLocation() {
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((int) geoPoint.getLatitude());
        dest.writeInt((int) geoPoint.getLongitude());
    }
    public static  final Parcelable.Creator<ParkingLocation>CREATOR =
            new Parcelable.Creator<ParkingLocation>(){

                @Override
                public ParkingLocation createFromParcel(Parcel source) {
                    return new ParkingLocation();
                }

                @Override
                public ParkingLocation[] newArray(int size) {
                    return new ParkingLocation[size];
                }
            };
    private ParkingLocation(Parcel in){
        int lat = in.readInt();
        int lon = in.readInt();
        geoPoint = new GeoPoint(lat,lon);
    }


}
