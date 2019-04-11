package com.example.drone;

import android.graphics.Color;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;


//Class drone
public class Drone {
    //les attributs
    private float vitesse;  //la vitesse du drone
    private float vitesseMax; //la vitesse maximal du drone

    private LatLng prev; //position du drone (x,y)

    private float positionX, positionY, orientation; // 0 < orientation = dregre < 360
    private float positionOrigineX = (float) 46.150, positionOrigineY = (float) -1.165;//la postion d'origine du drone
    private BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.boat);     //la bitmap du drone
    private GroundOverlay marker;
    private GoogleMap mMap;//la map

    private boolean arretPos = false;
    private float arretPosX = 0;
    private float arretPosY = 0;


    //constructeur de la class Drone
    public Drone() {
        this.vitesseMax = 60;
        this.vitesse = 0;
        this.positionX = positionOrigineX;
        this.positionY = positionOrigineY;
        this.orientation = 0;
    }



    //les geters
    public float getVitesse() {
        return vitesse;
    }
    public float getPositionOrigineX() {
        return this.positionOrigineX;
    }

    public float getPositionOrigineY() {
        return this.positionOrigineY;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }




    //les setters

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public void setPosition(float positionX, float positionY) {
        this.setPositionX(positionX);
        this.setPositionY(positionY);
    }

    //fonnction setVitesse permet de verifier la vitesse
    public void setVitesse(float vit) {
        if (vit > vitesseMax) {
            this.vitesse = vitesseMax;
        } else if (vit < 0) {
            this.vitesse = 0;
        } else {
            this.vitesse = vit;
        }
    }
    //fonction d'orientation du drone
    public void setOrientation(float orientation) {
        if (orientation < 360) {
            this.orientation = orientation % 360;
        } else if (orientation > 0) {
            this.orientation = 360 - (Math.abs(orientation) % 360);
        } else {
            this.orientation = orientation;
        }
    }





    //methode de calcul de vitesse (acceleration | ralentissement)

    public void calculVitesse(float n) {
        float i = 0;
        if (n < 7 && n >= -10 ) { // on accélère   n < 0
            i =  this.vitesse + ((Math.abs(n))/10);
            this.setVitesse(i);
        }
        if ( n > 7 ) { // on ralenti  n > 0
            i = this.vitesse - ((Math.abs(n))/10);
            this.setVitesse(i);
        }
    }

    //set orientation du drone
    public void calculOrientation(float n) {
        if(n != 0)
            this.setOrientation(this.orientation + n);
    }



    //method initMarker placer le drone dans le placement initial
    public void initMarker(GoogleMap mMap) {
        this.mMap = mMap;
        LatLng position = new LatLng(positionX, positionY);

        LatLngBounds bounds = new LatLngBounds(new LatLng(positionX - 0.001, positionY - 0.001), new LatLng(positionX + 0.001, positionY + 0.001));
        marker = this.mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(image)
                .positionFromBounds(bounds));

        marker.setPosition(position);
        marker.setBearing(orientation);
        this.prev=new LatLng(this.positionOrigineX,this.positionOrigineY);
    }


    //cette fonnction permet de gérer le deplacement du drone
    public void moveMarker(long time) {
        float times = (float) (time * Math.pow(10, -3)); // s
        float vit = (float) (this.vitesse / (60)); // m/s
        float distance = vit / times; // m
        float radian = (float) (this.orientation * 2 * Math.PI) / 360;

        float addX = (float) (Math.cos(radian) * distance) / 300000;
        float addY = (float) (Math.sin(radian) * distance) / 300000;

        float newX = this.positionX + addX;
        float newY = this.positionY + addY;

        LatLng position = new LatLng(newX, newY);
        this.setPosition(newX, newY);

        marker.setPosition(position);//mettre a jour la position  avec la nouvelle postion
        marker.setBearing(orientation);//mettre a jour l'orientation  avec le nouveau calcul

        LatLng suiv=new LatLng(this.positionX, this.positionY);
        mMap.addPolyline(new PolylineOptions()
                .add(this.prev,suiv)
                .width(5)
                .color(Color.RED));
        if (this.arretPos) {
            if (this.getPositionX() == arretPosX && this.getPositionY() == arretPosY) {
                this.vitesse = 0;
            }
        }
        this.prev=suiv;
    }

    //fonction permet du nettoyer la map
    public void clearMap() {
        this.mMap.clear();
    }
}
