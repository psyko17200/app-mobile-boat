package com.example.yamine;

import com.google.android.gms.maps.model.Marker;

public class Waypoint {
    private Marker point;
    private int vitesse;

    public Waypoint(Marker point, int vitesse) {
        this.point = point;
        this.vitesse = vitesse;
    }

    public Marker getPoint() {
        return point;
    }

    public void setPoint(Marker point) {
        this.point = point;
    }

    public int getVitesse() {
        return vitesse;
    }

    public String getVitesseString() {
        return "vitesse" + vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "point=" + point.getTitle() +
                ", vitesse=" + vitesse +
                '}';
    }
}