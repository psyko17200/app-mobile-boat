package com.example.drone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class itineraireActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int nbMarkeurs = 0;
    private int cpt = 0;

    private ArrayList<Waypoint> waypointsArray = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    private AlertDialog defineVit;
    private MarkerOptions mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itineraire_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Correspond a la navigation bar qui permet de naviguer entre les activités
        this.bottomNavigationView=findViewById(R.id.bottom_navigation);
        this.bottomNavigationView.setSelectedItemId(R.id.act3);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.act1:
                        Intent goToAct1 = new Intent(itineraireActivity.this, visualisationActivity.class);
                        startActivity(goToAct1);
                        finish();
                        break;
                    case R.id.act2:
                        Intent goToAct2 = new Intent(itineraireActivity.this, piloteActivity.class);
                        startActivity(goToAct2);
                        finish();
                        break;
                }
                return true;
            }
        });

        Button terminer = findViewById(R.id.terminer);
        terminer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createFile();
            }

        });

        Button effacer = findViewById(R.id.effacer);
        effacer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    waypointsArray.clear();
                    cpt=0;
                    nbMarkeurs=0;
                ((TextView) itineraireActivity.this.findViewById(R.id.nb_marker)).setText(nbMarkeurs + " marker(s) placé(s)");
                    mMap.clear();
            }
        });

    }

    //Permet de chercher et retourner un Waypoint grâce a son nom
    public Waypoint estDansArray(String nom) {
        for (int i = 0; i < waypointsArray.size(); i++) {
            if (waypointsArray.get(i).getPoint().getTitle().equals(nom)) {
                return waypointsArray.get(i);
            }
        }
        return null;
    }

    //Lancement de la Map
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Position
        LatLng lr = new LatLng(46.14796727435368, -1.1672115325927734);

        // Déplacement sur la position LR
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lr));
        //Changer l'affichage de la map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        
        //Zoomer sur LR
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lr)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Creation de la popup qui nous servira a definir ou modifier la vitesse a l'ajout ou au clique sur un point
        this.defineVit=new AlertDialog.Builder(this).create();
        TextView title=new TextView(this);
        title.setGravity(Gravity.CENTER);
        title.setText("Marker");
        title.setTextSize(25);
        title.setTypeface(null, Typeface.BOLD);
        this.defineVit.setCustomTitle(title);
        final LinearLayout vitEdit=new LinearLayout(this);
        vitEdit.setOrientation(LinearLayout.VERTICAL);
        final EditText entree=new EditText(this);
        final TextView msg=new TextView(this);
        msg.setGravity(Gravity.CENTER);
        msg.setText("Veuillez entrer une vitesse");
        final Button ok = new Button(this);
        ok.setText("ok");
        vitEdit.addView(msg);
        vitEdit.addView(entree);
        vitEdit.addView(ok);
        vitEdit.setGravity(View.TEXT_ALIGNMENT_CENTER);
        this.defineVit.setView(vitEdit);

        //Ajout de markeurs lors d'un tap sur la map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            private float valVit=10;
            @Override
            public void onMapClick(LatLng arg0) {
                valVit=10;
                entree.setText("");
                entree.setHint("10");
                mark=new MarkerOptions()
                        .position(arg0)
                        .title(""+ nbMarkeurs++);
                mMap.addMarker(mark);
                final Waypoint wp=new Waypoint(mark, valVit);
                //Validation de la definition de la vitesse si ce n'est pas dans le format demander, la vitesse est par default de 10, si c'est null egalement
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(entree.getText().toString().isEmpty() || !entree.getText().toString().matches("^([0-9]+.[0-9]+|[0-9]+)$")){
                            valVit=10;
                        }else{
                            valVit=Float.valueOf(entree.getText().toString());
                            if(valVit<0){
                                valVit=0;
                            }else if(valVit>60){
                                valVit=60;
                            }
                        }
                        wp.setVitesse(valVit);
                        defineVit.dismiss();
                    }
                });
                waypointsArray.add(wp);
                //Ajout du tracé entre deux markers
                if (waypointsArray.size() > 1) {
                    Log.e("add poly", "ok");
                    mMap.addPolyline(new PolylineOptions()
                            .add(waypointsArray.get(cpt).getPoint().getPosition(), waypointsArray.get(++cpt).getPoint().getPosition())
                            .width(5)
                            .color(Color.RED));
                }
                ((TextView) itineraireActivity.this.findViewById(R.id.nb_marker)).setText(nbMarkeurs + " marker(s) placé(s)");
                defineVit.show();
            }
        });

        //Gestion de l'appuie sur un marker
            //Cela va ouvrir la popup de modification de la vitesse en gerant bien sur tout les cas possibles encore une fois
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Waypoint tmp=estDansArray(marker.getTitle());
                entree.setText("");
                defineVit.show();
                entree.setHint(tmp.getVitesseString());
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float valVit=10;
                        if(!(entree.getText().toString().isEmpty() || !entree.getText().toString().matches("^([0-9]+.[0-9]+|[0-9]+)$"))){
                            valVit=Float.valueOf(entree.getText().toString());
                            if(valVit<0){
                                valVit=0;
                            }else if(valVit>60){
                                valVit=60;
                            }
                        }
                        tmp.setVitesse(valVit);

                        defineVit.dismiss();
                    }
                });
                return true;
            }
        });
    }

    //Creation de la trame GPRMC et ajout dans un fichier qui ce trouve a la racine de la memoire interne du telephone
    public void createFile() {
        Location locPrev;
        Location locNew=null;
        System.out.println(Environment.getExternalStorageDirectory());
        File file = new File(Environment.getExternalStorageDirectory()+"/","waypoints.txt");
        PrintWriter writer;
        try {
            writer = new PrintWriter(file, "UTF-8");
        SimpleDateFormat f;
        for (Waypoint w:waypointsArray) {
            locPrev=locNew;
            locNew=new Location("");
            locNew.setLongitude(w.getPoint().getPosition().longitude);
            locNew.setLatitude(w.getPoint().getPosition().latitude);
            String trame="$GPRMC,";
            Date d = new Date();
            f = new SimpleDateFormat("hhmmss.SSS");
            trame+=f.format(d)+",A,";
            double tmpLat=w.getPoint().getPosition().latitude;
            System.out.println(tmpLat);
            char nORs='N';
            if(tmpLat<0){
                tmpLat*=-1;
                nORs='S';
            }
            int deg=(int)tmpLat;
            double min=(tmpLat%1)*60;
            double sec=(min%1)*60;
            trame+=String.format("%02d",deg) +""+ String.format("%02d",(int)min) +"."+ String.format("%02d",(int)sec)+","+nORs+",";

            double tmpLon=w.getPoint().getPosition().longitude;
            System.out.println(tmpLon);
            char eORw='E';
            if(tmpLon<0){
                tmpLon*=-1;
                eORw='W';
            }
            deg=(int)tmpLon;
            min=(tmpLon%1)*60;
            sec=(min%1)*60;
            trame+=String.format("%03d",deg) +""+ String.format("%02d",(int)min) +"."+ String.format("%02d",(int)sec)+","+eORw+","+w.getVitesse()+",";
            if(locPrev!=null){
                Float heading = locPrev.bearingTo(locNew);
                trame+=String.format("%.1f",heading);
            }
            trame+=",";
            d = new Date();
            f = new SimpleDateFormat("ddMMyy");
            trame+=f.format(d)+",,,";
            trame+="*"+checksum(trame);
            System.out.println(trame);
            writer.println(trame);
        }
        writer.flush();
        writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Checksum qui permet de validé ou non une trame GPRMC
    public String checksum(String trame){
        int crc=0;
        for(int i=1; i<trame.length();i++){
            crc^=trame.charAt(i);
        }
        return Integer.toHexString(crc);
    }
}
