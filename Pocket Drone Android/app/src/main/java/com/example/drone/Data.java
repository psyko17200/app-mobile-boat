package com.example.drone;

//la classe data represente les données recuperées a partir de l'accelerometer
public class Data{

    private float x,y,z; // le format de donnée d accelerometer


    //constructeur prend en parametre un tableau de float
    public Data(float[] val) {
        this.x = val[0];
        this.y = val[1];
        this.z = val[2];
    }
    //les getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public float[] getVal(){
        float[] tab = new float[3];
        tab[0]=x;
        tab[1]=y;
        tab[2]=z;
        return tab;
    }
    //les setters
    public void setVal(float[] val) {
        this.x = val[0];
        this.y = val[1];
        this.z = val[2];
    }


}
