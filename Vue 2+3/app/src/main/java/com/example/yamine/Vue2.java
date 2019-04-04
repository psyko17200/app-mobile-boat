package com.example.yamine;



import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Vue2 extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private Data dataAccelerometre;
    private Data dataMagnetometre;
    private Data data;
    private float[] values;
    private float[] R;
    private Drone drone;
    private SensorManager sensorManager;
    private Sensor accelerometre;
    private Sensor magnetometre;
    private long oldTimestamp;

    /*------------------------------------------Handler-------------------------------------------------------*/
    private Handler handler2;
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            drone.moveMarker(100);
            handler2.postDelayed(this, 100);
        }
    };


    /*------------------------------------------Create---------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.yamine.R.layout.activity_vue2);
        oldTimestamp = 0;
        values = new float[3];
        values[0] = 0;
        values[1] = 0;
        values[2] = 0;
        data = new Data(values);
        dataAccelerometre = new Data(values);
        dataMagnetometre = new Data(values);

        handler2 = new Handler();
        handler2.postDelayed(runnable2, 100);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometre = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // à enlever ?
        sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometre, SensorManager.SENSOR_DELAY_UI);

        drone = new Drone();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.example.yamine.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometre, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, accelerometre);
        sensorManager.unregisterListener(this, magnetometre);
        /*if(handler != null){
            handler.removeCallbacks(runnable);
        }*/
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lr = new LatLng(46.14796727435368, -1.1672115325927734);
        LatLngBounds portLR = new LatLngBounds(new LatLng(46.13696545362447, -1.1806440353393555), new LatLng(46.159561408123146, -1.148371696472168));
        //mMap.addMarker(new MarkerOptions().position(lr).title("Marker in LR"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portLR.getCenter(), 13));
        drone.initMarker(mMap);
    }

    /*------------------------------------------Sensor functions---------------------------------------------------------*/
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        // envoi des données
        if (sensorEvent.timestamp - oldTimestamp > 20000000) {
            R = new float[9];
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                dataAccelerometre.setVal(arrondir(sensorEvent.values, 2));
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                dataMagnetometre.setVal(arrondir(sensorEvent.values, 2));
            }
            SensorManager.getRotationMatrix(R, null, dataAccelerometre.getVal(), dataMagnetometre.getVal());
            R = arrondir(R, 2);
            sensorManager.getOrientation(R, values);
            values = arrondir(values, 2);
            //drone.calculVitesse(dataAccelerometre.getY());
            drone.calculOrientation(dataAccelerometre.getX());
            data.setVal(arrondir(dataAccelerometre.getVal(), 2));
            oldTimestamp = sensorEvent.timestamp;
        }

    }

    /*------------------------------------------methodes movement---------------------------------------------------------*/
    public void stop(View v) {
        sensorManager.unregisterListener(this, accelerometre);
        sensorManager.unregisterListener(this, magnetometre);
        this.drone.setVitesse(0);
    }

    public void start(View v) {
        sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometre, SensorManager.SENSOR_DELAY_UI);
        drone.setVitesse(35);
    }

    public void home(View v) {
        sensorManager.unregisterListener(this, accelerometre);
        sensorManager.unregisterListener(this, magnetometre);

        goTo(drone, "HOME");
    }

    public void goTo(Drone d, String s) {
        if (s.equals("HOME")) {
           // goTo(d, d.getPositionOrigineX(), d.getPositionOrigineY());
            Log.e("stat", "x="+String.valueOf(d.getPositionOrigineX()+"y="+d.getPositionOrigineY()));
            drone.setPosition(d.getPositionOrigineX(),d.getPositionOrigineY());
            this.drone.setVitesse(0);
            this.drone.clearMap();
            this.mMap.clear();
            this.onMapReady(mMap);



        }
    }

    public void goTo(Drone d, float x, float y) {
        Log.e("stat", "x2="+String.valueOf(d.getPositionX()+"y2="+d.getPositionX()));


        float X = d.getPositionX() - d.getPositionOrigineX();
        float Y = d.getPositionY() - d.getPositionOrigineY();
        float tan = X/ Y;
        float degree = (float) (Math.atan(tan));
        int newOrientation = (int) degree; // calcul
        d.setOrientation(newOrientation);
        if (drone.getVitesse() <= 0) {

        }
        drone.setVitesse(300);
        drone.setArretPos(true);
        drone.setArretPosX(x);
        drone.setArretPosY(y);
    }

    /*------------------------------------------methode arondir---------------------------------------------------------*/
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

    //OH SHIT
    public void goVue1(View view){
        //Intent intent = new Intent(this,Vue1.class);
        //startActivity(intent);
    }

//    public void goVue2(View view){
//        Intent intent = new Intent(this,Vue2.class);
//        startActivity(intent);
//    }

    public void goVue3(View view){
        Intent intent = new Intent(this,Vue3.class);
        startActivity(intent);
    }
}
