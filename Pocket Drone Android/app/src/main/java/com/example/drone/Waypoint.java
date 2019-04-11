package com.example.drone;

import com.google.android.gms.maps.model.MarkerOptions;

public class Waypoint {
    private MarkerOptions point;
    private float vitesse;

    //Constructeur de la classe Waypoint qui va nous servir a creer des markers et leur assigné a chacun une vitesse que l'on définira dans une popUp
        public Waypoint(MarkerOptions point, float vitesse) {
        this.point = point;
        this.vitesse = vitesse;
    }

    //getter de la classe
    public MarkerOptions getPoint() {
        return point;
    }

    public float getVitesse() {
        return vitesse;
    }

    public String getVitesseString() {
        return String.valueOf(vitesse);
    }

    //setter de la classe
    public void setVitesse(float vitesse) {
        this.vitesse = vitesse;
    }

    //toString
    @Override
    public String toString() {
        return "Waypoint{" +
                "point=" + point.getTitle() +
                ", vitesse=" + vitesse +
                '}';
    }
}