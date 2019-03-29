package com.example.yamine;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity {

    //private GoogleMap mMap;
    //private ArrayList<LatLng> liste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        liste=new ArrayList<LatLng>();*/


    }
    public void goVue3(View view){
        Intent intent = new Intent(this,Vue3.class);
        startActivity(intent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*
    @Override
   public void onMapReady(GoogleMap googleMap) {


    }-*/
       /* mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Depart"));
        liste.add(sydney) ;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        // Set a listener for marker click.
  //      mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions Markeroption=new MarkerOptions();
                Markeroption.position(latLng);



                LatLng last= liste.get(liste.size()-1);
                liste.add(Markeroption.getPosition()) ;


                LatLng now= liste.get(liste.size()-1);


                mMap.addMarker(Markeroption.title(String.valueOf(Markeroption.getPosition())));



                mMap.addPolyline(new PolylineOptions().add(last,now).width(13).color(R.color.colorPrimary));

            }

   }     }
);*/





}


