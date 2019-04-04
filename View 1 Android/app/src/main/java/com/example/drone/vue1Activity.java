package com.example.drone;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class vue1Activity extends FragmentActivity implements OnMapReadyCallback {

    protected GoogleMap mMap;
    protected simulateur s;
    protected TextView info;
    protected EditText ip;
    protected EditText port;
    protected Button connect;
    protected Button disconnect;
    protected SupportMapFragment mapFragment;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue1);
        this.ip=findViewById(R.id.ip);
        ip.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        this.port=findViewById(R.id.port);
        port.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        this.connect=findViewById(R.id.connect);
        this.disconnect =findViewById(R.id.disconnect);
        this.info=findViewById(R.id.info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.findViewById(R.id.tracer).setVisibility(View.GONE);

        this.disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.returnMenu();
            }
        });

        this.connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ip!=null && port!=null) {
                    //Creation du thread qui va recuperer les trames
                    s = new simulateur(ip.getText().toString(),Integer.valueOf(port.getText().toString()),vue1Activity.this);
                    s.addMap(mMap);
                    s.start();
                }
            }
        });
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
        // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));

    }


    public EditText getIp() {
        return ip;
    }

    public EditText getPort() {
        return port;
    }

    public Button getConnect() {
        return connect;
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }
}
