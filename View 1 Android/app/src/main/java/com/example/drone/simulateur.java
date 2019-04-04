package com.example.drone;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.*;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class simulateur extends Thread {

        private int port = 55555;
        private String ip = "10.13.26.1";
        private BufferedReader in;
        private GoogleMap mMap;
        private vue1Activity act;
        private float coordLat;
        private float coordLon;
        private Marker marker = null;
        private Socket socket;
        private boolean test=true;

        public simulateur(String ip, int port, vue1Activity act){
            this.ip=ip;
            this.port=port;
            this.act=act;
        }

        public void run() {
                try {
                    this.socket = new Socket(ip, port);
                    System.out.println("Connected");
                    if(this.socket.isConnected()) {
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                //a enlever
                                act.findViewById(R.id.textView).setVisibility(View.GONE);
                                act.findViewById(R.id.tracer).setVisibility(View.VISIBLE);
                                act.ip.setVisibility(View.GONE);
                                act.port.setVisibility(View.GONE);
                                act.connect.setVisibility(View.GONE);
                                }
                        });
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        int degLat, degLon;
                        float minuteLat, minuteLon;
                        final PolylineOptions polyOpt = new PolylineOptions();
                        while ((line = in.readLine()) != null && test==true) {
                            System.out.println();
                            final float prevLat, prevLon;
                            if (line.contains("$GPRMC")) {
                                final String[] trame = line.split(",");
                                prevLat = this.coordLat;
                                prevLon = this.coordLon;

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
                                    act.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (marker != null) {
                                                marker.remove();
                                            }
                                            LatLng latlng = new LatLng(coordLat, coordLon);
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(latlng);
                                            marker = mMap.addMarker(markerOptions);
                                            polyOpt.add(new LatLng(coordLat, coordLon));
                                            polyOpt.geodesic(true);
                                            Polyline poly = mMap.addPolyline(polyOpt);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordLat, coordLon), 17));
                                            act.info.setText("Lat: " + coordLat + " Lon: " + coordLon + " V:" + trame[7]);
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
                                    act.findViewById(R.id.tracer).setVisibility(View.GONE);
                                    act.ip.setVisibility(View.VISIBLE);
                                    act.port.setVisibility(View.VISIBLE);
                                    act.connect.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ttes");
                }finally{
                    System.out.println("Terminated");
                }
        }

        public void addMap(GoogleMap map){
            this.mMap=map;
        }

        public void returnMenu() {
            test=false;
        }
    }
