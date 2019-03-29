package com.example.drone;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;

public class vue1Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private simulateur s;
    private EditText ip;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue1);
        this.ip=findViewById(R.id.ip);
        this.port=findViewById(R.id.port);

        Button connect=findViewById(R.id.connect);
        Button disconnect=findViewById(R.id.disconnect);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        connect.setOnClickListener(new View.OnClickListener(){
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


}
