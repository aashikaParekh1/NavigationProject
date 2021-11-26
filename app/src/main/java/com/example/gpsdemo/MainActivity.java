package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.location.Location;
import android.location.Geocoder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView coord;
    TextView add;
    TextView each;
    TextView total;
    TextView time;

    LocationManager locationManager;
    LocationListener locationListenerGPS;
    final static int REQUEST_LOC = 1234;
    double latitude;
    double longitude;
    float dis;
    double round;
    double round2;
    double current;
    Location last;
    Long sTime = SystemClock.elapsedRealtime();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coord = findViewById(R.id.id_coord);
        add = findViewById(R.id.id_add);
        each = findViewById(R.id.id_each);
        total = findViewById(R.id.id_total);
        time = findViewById(R.id.id_time);

        final Geocoder gc = new Geocoder(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            //performAction(...);
        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOC);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOC);

        }




        locationListenerGPS = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(Location location) {
                //lat & long
                Log.d("TAG", "BEFORE");
                latitude = location.getLatitude();
                System.out.println("LAT: " + latitude);
                longitude = location.getLongitude();
                System.out.println("LON: " + longitude);

                DecimalFormat df = new DecimalFormat("##.####");

                String msg = df.format(latitude) + "° N\n " + df.format(longitude) + "° W";
                Log.d("TAG", msg);
                coord.setText(msg);

                Log.d("ROUND", String.valueOf(latitude/1000));



                //geocoder
                if(gc.isPresent()){
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(latitude, longitude, 1);
                        Log.d("ADD", list.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("ADD", "CATCH");
                    }
                    //Log.d("TAG2", String.valueOf(latitude));
                    //Log.d("TAG2", String.valueOf(longitude));
                    Address address = list.get(0);
                    StringBuffer str = new StringBuffer();
                    str.append(address.getAddressLine(0));
                    String strAddress = str.toString();
                    Log.d("ADD", strAddress);
                    add.setText(strAddress);

                }

                //distance
                if(last == null)
                    last = location;

                dis += location.distanceTo(last);

                current = location.distanceTo(last);

                round = dis*0.000621; //convert

                round2 = current*0.000621;

                round = (double)Math.round(round*1000)/1000;

                round2 = (double)Math.round(round2*1000)/1000;

                total.setText(String.valueOf(round + " miles"));
                Log.d("DISTANCE", String.valueOf(round));

                each.setText(String.valueOf(round2 + " miles"));
                Log.d("DISTANCE", String.valueOf(round2));

                last = location;

                //time;
                //sTime = SystemClock.elapsedRealtime();
                long elapsed = SystemClock.elapsedRealtime() - sTime;
                double sec = (double) elapsed/1000.0;
                //Duration d = Duration.ofMillis(elapsed);
                time.setText(sec + " seconds");





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

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          Log.d("TAG", "INSIDE");
          //return;

        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);



    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "BEFORE SWITCH");
        switch (requestCode) {
            case REQUEST_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListenerGPS);
                    Log.d("TAG", "INSIDE PERMISSION RESULT");
                }
        }
    }







}