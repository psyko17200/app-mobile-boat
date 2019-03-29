package com.example.drone;
import android.app.Activity;
import android.util.Log;

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
        private Activity act;
        private float coordLat;
        private float coordLon;
        private Marker marker = null;

        public simulateur(){}

        public simulateur(String ip, int port, Activity act){
            this.ip=ip;
            this.port=port;
            this.act=act;
        }

        public void run() {
                Socket socket;
                try {
                    socket = new Socket(ip, port);
                    System.out.println("Connected");
                    in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
                    String line;
                    int degLat, degLon;
                    float minuteLat, minuteLon;
                    final PolylineOptions polyOpt = new PolylineOptions();
                    while((line = in.readLine())!=null){
                        final float prevLat,prevLon;
                        if(line.contains("$GPRMC")) {
                            String [] trame=line.split(",");
                            prevLat=this.coordLat;
                            prevLon=this.coordLon;

                            degLat=(int)(Float.valueOf(trame[3])/100);
                            minuteLat=Float.valueOf(trame[3])%100;
                            this.coordLat=degLat+minuteLat/60;
                            if(trame[4].equals("S")){
                                coordLat*=-1;
                            }

                            degLon=(int)(Float.valueOf(trame[5])/100);
                            minuteLon=Float.valueOf(trame[5])%100;
                            this.coordLon=degLon+minuteLon/60;
                            if(trame[6].equals("W")){
                                coordLon*=-1;
                            }
                            if(this.coordLat!=prevLat || this.coordLon!=prevLon) {
                                act.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(marker !=null) {
                                            marker.remove();
                                        }
                                        LatLng latlng = new LatLng(coordLat, coordLon);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(latlng);
                                        marker =mMap.addMarker(markerOptions);
                                        polyOpt.add(new LatLng(coordLat,coordLon));
                                        polyOpt.geodesic(true);
                                        Polyline poly=mMap.addPolyline(polyOpt);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordLat,coordLon),17));

                                    }
                                });
                            }
                            sleep(150);
                        }
                    }
                    in.close();
                    socket.close();
                    System.out.println("Terminated");

                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                }
        }

        public void addMap(GoogleMap map){
            this.mMap=map;
        }
    }
