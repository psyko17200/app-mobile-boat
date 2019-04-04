package com.example.yamine;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by HANI on 30/03/2019.
 */

public class Data{
    private float x,y,z;
    public Data(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Data(float[] val) {
        this.x = val[0];
        this.y = val[1];
        this.z = val[2];
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setVal(float[] val) {
        this.x = val[0];
        this.y = val[1];
        this.z = val[2];
    }

    public void setVal(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float[] getVal(){
        float[] tab = new float[3];
        tab[0]=x;
        tab[1]=y;
        tab[2]=z;
        return tab;
    }
}
