package com.example.yamine;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class Drone {
    private float vitesse;
    private float vitesseMax;

    private float positionX, positionY, orientation; // 0 < orientation = dregre < 360
    private float positionOrigineX = (float) 46.142436904145534, positionOrigineY = (float) -1.1726617813110352;

    private BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.fleche1);
    private GroundOverlay marker;
    private GoogleMap mMap;
    private Polyline line;

    private boolean arretPos = false;
    private float arretPosX = 0;
    private float arretPosY = 0;

    public Drone() {
        this.vitesseMax = 10;
        this.vitesse = 0;
        this.positionX = positionOrigineX;
        this.positionY = positionOrigineY;
        this.orientation = 0;
    }

    /*------------------------------------------getter/setter---------------------------------------------------------*/



    public float getVitesse() {
        return vitesse;
    }

    public void setVitesse(float vit) {
        if (vit > vitesseMax) {
            this.vitesse = vitesseMax;
        } else if (vit < 0) {
            this.vitesse = 0;
        } else {
            this.vitesse = vit;
        }
    }

    public float getPositionOrigineX() {
        return this.positionOrigineX;
    }

    public float getPositionOrigineY() {
        return this.positionOrigineY;
    }

    public boolean getArretPos() {
        return this.arretPos;
    }

    public float getArretPosX() {
        return this.arretPosX;
    }

    public float getArretPosY() {
        return this.arretPosY;
    }

    public void setArretPos(boolean b) {
        this.arretPos = b;
    }

    public void setArretPosX(float f) {
        this.arretPosX = f;
    }

    public void setArretPosY(float f) {
        this.arretPosY = f;
    }

    public void setPosition(float positionX, float positionY) {
        this.setPositionX(positionX);
        this.setPositionY(positionY);
    }

    public void setOrientation(float orientation) {
        if (orientation > 360) {
            this.orientation = orientation % 360;
        } else if (orientation < 0) {
            this.orientation = 360 - (Math.abs(orientation) % 360);
        } else {
            this.orientation = orientation;
        }
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getOrientation() {
        return orientation;
    }

    /*------------------------------------------methode calcul---------------------------------------------------------*/

    public void calculVitesse(float n) {
        float i = 0;
        if (n < 0) { // on accélère
            i = this.vitesse + ((this.vitesseMax * Math.abs(n)) / 1500);
            this.setVitesse(i);
        }
        if (n > 0) { // on ralenti
            i = this.vitesse - ((this.vitesseMax * n) / 1000);
            this.setVitesse(i);
        }
    }

    public void calculOrientation(float n) {
        if(n != 0)
            this.setOrientation(this.orientation - n);
    }

    /*------------------------------------------methode marker---------------------------------------------------------*/

    public GroundOverlay getMarker() {
        return marker;
    }

    public void setMarker(GroundOverlay marker) {
        this.marker = marker;
    }

    public void initMarker(GoogleMap mMap) {
        this.mMap = mMap;
        LatLng position = new LatLng(positionX, positionY);
        LatLngBounds bounds = new LatLngBounds(new LatLng(positionX - 0.001, positionY - 0.001), new LatLng(positionX + 0.001, positionY + 0.001));
        marker = this.mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(image)
                .positionFromBounds(bounds)
                .transparency((float) 0.5));
        marker.setPosition(position);
        marker.setBearing(orientation);
        List<LatLng> list = new ArrayList<LatLng>();
        list.add(new LatLng(this.positionOrigineX, this.positionOrigineY));
        line = this.mMap.addPolyline(new PolylineOptions()
                .add()
                .width(13)
                .color(Color.BLUE));
        line.getPoints();


    }

    public void moveMarker(long time) {
        float times = (float) (time * Math.pow(10, -3)); // s
        float vit = (float) (this.vitesse / (3.6)); // m/s
        float distance = vit / times; // m
        float radian = (float) (this.orientation * 2 * Math.PI) / 360;
        float addX = (float) (Math.cos(radian) * distance) / 100000;
        float addY = (float) (Math.sin(radian) * distance) / 100000;
        float newX = this.positionX + addX;
        float newY = this.positionY + addY;
        //newX = (float) (this.positionX+(this.vitesse*0.00001));
        //newY = (float) (this.positionY+(this.vitesse*0.00001));
        LatLng position = new LatLng(newX, newY);
        this.setPosition(newX, newY);
        marker.setPosition(position);
        marker.setBearing(orientation);
        List<LatLng> list = line.getPoints();
        list.add(new LatLng(this.positionX, this.positionY));
        line.setPoints(list);
        if (this.arretPos) {
            if (this.getPositionX() == arretPosX && this.getPositionY() == arretPosY) {
                this.vitesse = 0;
            }
        }
    }

    /*------------------------------------------methode arrondir---------------------------------------------------------*/


    public float[] arrondir(float[] f, int nbr) {
        for (int i = 0; i < f.length; i++) {
            f[i] = arrondir(f[i], nbr);
        }
        return f;
    }

    public float arrondir(float f, int nbr) {
        f = f * (10 ^ nbr);
        int a = (int) f;
        f = a / (10 ^ nbr);
        return f;
    }

    public void clearMap() {
        this.line.remove();




    }
}
