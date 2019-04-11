package com.example.drone;
import android.graphics.Color;
import android.location.Location;
import android.text.Html;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.*;
import java.io.*;

public class connect_et_recup extends Thread {

        private int port;
        private String ip;
        private BufferedReader in;
        private GoogleMap mMap;
        private visualisationActivity act;
        private float coordLat;
        private float coordLon;
        private Marker marker = null;
        private Socket socket;
        private boolean test=true;

        //Constructeur qui prend en parametre l'ip, le port et l'activity ce qui nous permettra d'effectué des taches sur le thread de l'activité
        public connect_et_recup(String ip, int port, visualisationActivity act){
            this.ip=ip;
            this.port=port;
            this.act=act;
        }

        //Methode run du thread qui va permettre la connection au socket et la récuperation des trames
            //Va également gérer l'affichage des differents layout et l'affichage des markers sur la map
        public void run() {
                try {
                    this.socket = new Socket(ip, port);
                    if(this.socket.isConnected()) {
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                act.findViewById(R.id.logo).setVisibility(View.GONE);
                                act.findViewById(R.id.tracer).setVisibility(View.VISIBLE);
                                act.findViewById(R.id.menu_connect).setVisibility(View.GONE);
                                act.findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
                                }
                        });
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        int degLat, degLon;
                        float minuteLat, minuteLon;
                        final PolylineOptions polyOpt = new PolylineOptions();
                        while ((line = in.readLine()) != null && test) {
                            System.out.println();
                            final Location locPrev;
                            if (line.contains("$GPRMC")) {
                                final String[] trame = line.split(",");
                                locPrev=new Location("");
                                locPrev.setLatitude(coordLat);
                                locPrev.setLongitude(coordLon);

                                degLat = (int) (Float.valueOf(trame[3]) / 100);
                                minuteLat = Float.valueOf(trame[3]) % 100;
                                this.coordLat = degLat + minuteLat / 60;
                                if (trame[4].equals("S")) {
                                    coordLat *= -1;
                                }

                                degLon = (int) (Float.valueOf(trame[5]) / 100);
                                minuteLon = Float.valueOf(trame[5]) % 100;
                                this.coordLon = degLon + minuteLon / 60;
                                if (trame[6].equals("W")) {
                                    coordLon *= -1;
                                }
                                    //Ajout des markers et des tracer entre chaque trames reçus
                                    act.runOnUiThread(new Runnable() {
                                        public void run() {
                                            LatLng latlng = new LatLng(coordLat, coordLon);
                                            Location locNew=new Location("");
                                            locNew.setLongitude(coordLon);
                                            locNew.setLatitude(coordLat);
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions
                                                    .position(latlng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.boat));
                                            if (marker != null) {
                                                marker.remove();
                                                markerOptions.rotation(locPrev.bearingTo(locNew));
                                            }

                                            marker = mMap.addMarker(markerOptions);
                                            polyOpt.add(new LatLng(coordLat, coordLon));
                                            polyOpt.geodesic(true);
                                            polyOpt.color(Color.RED);
                                            mMap.addPolyline(polyOpt);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordLat, coordLon), 17));
                                            String infoHtml="Lat: <b>" + String.format("%03.2f",coordLat) + trame[4] + "</b> Lon: <b>" + String.format("%03.2f",coordLon) + trame[6] +"</b> V: <b>" + trame[7] + " Knots </b>";
                                            act.info.setText(Html.fromHtml(infoHtml));
                                        }
                                    });
                                //sleep(150);
                            }
                        }
                        in.close();
                        socket.close();
                        if(socket.isClosed()) {
                            act.runOnUiThread(new Runnable() {
                                public void run() {
                                    mMap.clear();
                                    act.findViewById(R.id.logo).setVisibility(View.VISIBLE);
                                    act.findViewById(R.id.tracer).setVisibility(View.GONE);
                                    act.findViewById(R.id.menu_connect).setVisibility(View.VISIBLE);
                                    act.findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    //affichage de la popup si la connection au socket echoue
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            act.popUpCo.show();
                        }});

                }
        }

        //Methode qui permet d'assigner la map de notre activité afin de pouvoir effectué le traitement de l'affichage sur la map
        public void addMap(GoogleMap map){
            this.mMap=map;
        }

        //Methode qui permet de verifier l'appuie du bouton de retour et d'allez a la fin du thread
        public void returnMenu() {
            test=false;
        }
    }
