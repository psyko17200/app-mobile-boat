package com.example.yamine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.WeakHashMap;

public class Vue3 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int nbMarkeurs = 0;
    private int cpt = 0;
    private WeakHashMap whm = new WeakHashMap<>();
    String nameM = "";
    private ArrayList<Waypoint> waypointsArray = new ArrayList<>();

    private ArrayList<MarkerOptions> waypoints = new ArrayList<MarkerOptions>();
    private Marker tmp;
    private Date aujourdhui = new Date();
    private SimpleDateFormat formater = null;
    private SimpleDateFormat formater2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final AlertDialog alertDialog;
        final AlertDialog alertVitesse;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Envoi de données");
        alertDialog.setMessage("Envoi...");





        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.show();



                try {
                    //tansformations des donnée en fomat json
                    toJson(waypointsArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                for (int i = 0; i < waypointsArray.size()-1; i++) {
                     //waypointsArray.remove(i);
                    waypoints.remove(i);

                }
                mMap.clear();
            }
        });

    }


    public Waypoint estDansArray(String nom) {
        for (int i = 0; i < waypointsArray.size(); i++) {
            if (waypointsArray.get(i).getPoint().getTitle().equals(nom)) {
                return waypointsArray.get(i);
            }
        }
        return null;
    }

    public void onMapReady(GoogleMap googleMap) {


        String test = ddToDms(46.149691684762026, -1.1702870205044746);
        String test2 = ddToRMC(46.149691684762026, -1.1702870205044746);

        Log.e("test", "" + test);
        Log.e("test2", "" + test2);
        Log.e("test", "" + convert(46.149691684762026));

        try {
            sendData("test");
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        //Création de la boite de dialogue
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Choisir la vitesse");
        //Ajout du contenu dans la boite de dialogue
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        TextView text = new TextView(this);
        text.setPadding(10, 10, 10, 10);
        final SeekBar seek = new SeekBar(this);
        linear.addView(seek);
        linear.addView(text);
        alertDialog.setView(linear);


        //Ajout de markeurs lors d'un tap sur la map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                android.util.Log.i("onMapClick", "Horray! position:" + arg0);
                MarkerOptions tmpM = new MarkerOptions()
                        .position(arg0)
                        .title("WayPoint " + ++nbMarkeurs);
                mMap.addMarker(tmpM);
                //waypoints.add(nbMarkeurs,tmp);
                waypoints.add(tmpM);
                if (waypoints.size() > 1) {
                    Log.e("add poly", "ok");
                    Polyline line2 = mMap.addPolyline(new PolylineOptions()
                            .add(waypoints.get(cpt).getPosition(), waypoints.get(++cpt).getPosition())
                            .width(5)
                            .color(Color.RED));
                }

            }
        });

        //Listener sur le click d'un markeur
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.e("onMarkerClick", "ok");
                //whm.put(this, ""+(Math.random() * 100));
                alertDialog.show();
                //Log.e("whm: ",""+whm);
                nameM = marker.getTitle();
                tmp = marker;
                Waypoint tmp2 = new Waypoint(tmp, 25);
                waypointsArray.add(tmp2);
                Log.e("satat", "---------------------------------");
                Log.e("satat", "" + waypointsArray.get(waypointsArray.size()-1).getPoint().getPosition());
                Log.e("Stat", "" + waypointsArray.get(waypointsArray.size()-1).getVitesseString());

                Log.e("Stat nombre ", "" + waypointsArray.size()+" nbr de la class"+nbMarkeurs);
                return true;
            }
        });
        //Listener sur la seekbar
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.e("whm on seekbar: ",""+whm.get(nameM));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("onStopTrackingTouch", "seekbar: " + seekBar.getProgress() + ", name" + nameM);
                Log.e("nbMarkeur", "" + nbMarkeurs);
                Log.e("watpointtr", "" + waypointsArray.get(0).toString());
                estDansArray(nameM).setVitesse(seekBar.getProgress());


            }
        });
      //  estDansArray(nameM).setVitesse(
        //Dessiner les lignes entre les waypoints
        for (int i = 0; i < waypoints.size() - 1; i++) {


            Polyline line2 = mMap.addPolyline(new PolylineOptions()
                    .add(waypoints.get(i).getPosition(), waypoints.get(i + 1).getPosition())
                    .width(5)
                    .color(Color.RED));
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                android.util.Log.e("onMapLongClick", "latLng:" + latLng);

            }
        });


    }

    //Transforme un arraylist en un objet json + création des trames
    @SuppressLint("LongLogTag")
    public void toJson(ArrayList<Waypoint> waypointsArray) throws JSONException {

        //Création d'un string contenant le Json et de la trame
        String tmp = "{ \"waypoints\":{ ";

        for (Waypoint m : waypointsArray) {
            //json
            tmp += "\"" + m.getPoint().getTitle() + "\":{";
            tmp += "\"vitesse\":" + m.getVitesse() + ",";
            String pos = "\"" + m.getPoint().getPosition().latitude + "," + m.getPoint().getPosition().longitude + "\"";
            tmp += "\"LatLng\":" + pos;
            tmp += "},";



        }
        tmp = tmp.substring(0, tmp.length() - 1);
        tmp += "}}";

        Log.e("toJson string", "" + tmp);
        JSONObject obj = new JSONObject(tmp);






        File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
        File file = new File(directory,"waypoints.txt");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(tmp.getBytes());
            Log.e("Creation du fichier json ", "" + obj);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Converti des coordonnées en format DMS
    public String ddToDms(double lat, double lng) {
        String latResult, lngResult, dmsResult;

        latResult = (lat >= 0) ? "N" : "S";

        // Call to getDms(lat) function for the coordinates of Latitude in DMS.
        // The result is stored in latResult variable.
        latResult += convert(lat);

        lngResult = (lng >= 0) ? "E" : "W";

        // Call to getDms(lng) function for the coordinates of Longitude in DMS.
        // The result is stored in lngResult variable.
        lngResult += convert(lng);

        // Joining both variables and separate them with a space.
        dmsResult = latResult + ' ' + lngResult;

        // Return the resultant string
        return dmsResult;
    }

    //Converti des coordonnées dd en format RMC (trame nmea)
    public String ddToRMC(double lat, double lng) {
        String latResult, lngResult, dmsResult;

        // Call to getDms(lat) function for the coordinates of Latitude in DMS.
        // The result is stored in latResult variable.
        latResult = convert2(lat);
        latResult += (lat >= 0) ? "N" : "S";

        // Call to getDms(lng) function for the coordinates of Longitude in DMS.
        // The result is stored in lngResult variable.
        lngResult = convert2(lng);
        lngResult += (lng >= 0) ? "E" : "W";

        // Joining both variables and separate them with a space.
        dmsResult = latResult + ',' + lngResult;

        // Return the resultant string
        return dmsResult;
    }

    public String getDms(double val) {
        int deg = (int) Math.floor(val);
        int min = (int) (val - (double) deg) * 60;
        double sec = (val - deg - (min / 60)) * 3600;
        return deg + " °" + min + " '" + sec + "\"";
    }

    public String convert(double lat) {
        //double d = Math.floor(this.lat);
        lat = Math.abs(lat);
        int d = (int) lat;
        double m = (int) ((lat - d) * 60);
        double s = ((lat - d - m / 60) * 3600);

        return ("" + d + "°" + m + "'" + String.format("%.2f", s) + "\"");
    }

    public String convert2(double lat) {
        lat = Math.abs(lat);
        int d = (int) lat;
        double m = (int) ((lat - d) * 60);
        double s = ((lat - d - m / 60) * 3600);

        return ("" + d + "" + (int) m + "." + String.format("%.2f", s) + ",");
    }

    public void sendData(String msg) throws IOException {

    }
    public static void writeToFile(byte[] data, File file) throws IOException {

        BufferedOutputStream bos = null;

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        }
        finally {
            if (bos != null) {
                try {
                    bos.flush ();
                    bos.close ();
                }
                catch (Exception e) {
                }
            }
        }
    }
}
