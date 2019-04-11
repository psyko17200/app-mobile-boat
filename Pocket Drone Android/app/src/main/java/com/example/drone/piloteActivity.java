package com.example.drone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class piloteActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap mMap;
    private Data dataAccelerometre;  // accelorometre
    private Data dataMagnetometre;   // Magnetometre
    private Data data;    // herite de la class data
    private float[] values;
    private float[] Rot; // tableau qui vas contenir nos valeurs de rotation
    private Drone drone;   // notre drone qui herite de la class drone
    private SensorManager sensorManager;
    private Sensor accelerometre;
    private Sensor magnetometre;
    private long oldTimestamp;
    protected BottomNavigationView bottomNavigationView;//button de navigation

    private boolean rep=true;

    /*------------------------------------------Handler-------------------------------------------------------*/
    private Handler handler2;//Le Handler à charge de la communication entre la Thread de background et celle de l'IHM
    private Runnable runnable2 = new Runnable() {   // notre interface runnable
        @Override
        public void run() {                        // qui appel la methode run ( sans argument )
            drone.moveMarker(100);
            handler2.postDelayed(this, 100);
            ((TextView)piloteActivity.this.findViewById(R.id.vitAff)).setText("Vitesse:     " + String.format("%.1f",drone.getVitesse()) + "      noeud(s)");
        }
    };

    /*------------------------------------------Create---------------------------------------------------------*/

    /*



    values[0] est la valeur sur l'axe x,
    values[1] la valeur sur l'axe y et
    values[2] la valeur sur l'axe z

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
        on a cree nos boutons ( home ,arret d'urgence  et demarrer , et retour ... )
        on gere aussi les ORIENTATION (ORIENTATION_LANDSCAPE,ORIENTATION_PORTRAIT) de notre vue

        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pilote_activity);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        final RelativeLayout pilotage=findViewById(R.id.pilotage);
        pilotage.setVisibility(View.GONE);

        final RelativeLayout window=findViewById(R.id.fenetre);
        final Button ib=findViewById(R.id.begin);
        final Button retour=findViewById(R.id.retour);
        final Button starStop=findViewById(R.id.Marche);
        final Button home=findViewById(R.id.home);

        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            pilotage.setVisibility(View.VISIBLE);
            window.setVisibility(View.GONE);
            findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        }

        ib.setOnClickListener(new View.OnClickListener() {  // ca permet de mettre notre vue en mode paysage lors du click
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
        });

        retour.setOnClickListener(new View.OnClickListener() { // ca permet de mettre notre vue en mode portrait lors du click
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        starStop.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) { // cette fonction nous permet  de faire le vas et viens entre le button demarrer
                if (rep){               // et le button Arret D'URGENCE
                    start();
                    rep=false;
                    starStop.setText("Arret");
                }else{
                    stop();
                    starStop.setText("Démarrer");
                    rep=true;
                }


            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home(starStop);
            }
        });

        /*

        on initialise les valeurs de nos axes ' X , Y , Z'
        et on insere ces valeurs dans notre accelorometre et notre Magnetometre


        */

        oldTimestamp = 0;
        values = new float[3];
        values[0] = 0;
        values[1] = 0;
        values[2] = 0;
        data = new Data(values);
        this.dataAccelerometre = new Data(values);
        this.dataMagnetometre = new Data(values);

        handler2 = new Handler();
        handler2.postDelayed(runnable2, 100);

        /*
        c'est notre toolbar qui nous permet de nous deplacer d'une vue a une autres
        aller a la vue1 et la vue3

        */

        this.bottomNavigationView.setSelectedItemId(R.id.act2);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.act1:
                        Intent goToAct1 = new Intent(piloteActivity.this, visualisationActivity.class);
                        startActivity(goToAct1);
                        finish();
                        break;
                    case R.id.act3:
                        Intent goToAct3= new Intent(piloteActivity.this, itineraireActivity.class);
                        startActivity(goToAct3);
                        finish();
                        break;
                }
                return true;
            }
        });

        drone = new Drone();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker  and move the camera
        LatLngBounds portLR = new LatLngBounds(new LatLng(46.13696545362447, -1.1806440353393555), new LatLng(46.159561408123146, -1.148371696472168));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portLR.getCenter(), 16));
        this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        drone.initMarker(mMap);
    }

    /*------------------------------------------Sensor functions---------------------------------------------------------*/
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        /*

        faire si changement sur le capteur


        */
        // envoi des données
        if (sensorEvent.timestamp - oldTimestamp > 20000000) {
            Rot = new float[9];
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                this.dataAccelerometre.setVal(arrondir(sensorEvent.values, 2));
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                this.dataMagnetometre.setVal(arrondir(sensorEvent.values, 2));
            }
            SensorManager.getRotationMatrix(Rot, null, this.dataAccelerometre.getVal(), this.dataMagnetometre.getVal());
            this.Rot = arrondir(Rot, 2);
            this.sensorManager.getOrientation(Rot, values);

            this.values = arrondir(this.values, 2);
            this.drone.calculVitesse(this.dataAccelerometre.getX());
            this.drone.calculOrientation(this.dataAccelerometre.getY());
            this.data.setVal(arrondir(this.dataAccelerometre.getVal(), 2));
            this.oldTimestamp = sensorEvent.timestamp;
        }

    }

    /*------------------------------------------methodes movement---------------------------------------------------------*/

    /*
     notre methode stop qui permet d'arreter le fonctionement de l'accelerometre et du magnetometre
     et mettre la vitesse de notre drone a zero
    */
    public void stop() {
        sensorManager.unregisterListener(this, accelerometre);
        sensorManager.unregisterListener(this, magnetometre);
        this.drone.setVitesse(0);
    }

     /*
     notre methode start qui permet de lancer le fonctionement de l'accelerometre et du magnetometre
     et mettre la vitesse de notre drone a cinq
     SensorManager.SENSOR_DELAY_NORMAL (0,2 seconde entre chaque prise) ;
       SensorManager.SENSOR_DELAY_UI (0,06 seconde entre chaque mise à jour)
    */

    public void start() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometre = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);////TYPE_GYROSCOPE a place  GYROSCOPE
        sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometre, SensorManager.SENSOR_DELAY_UI);
        drone.setVitesse(5);
    }

     /*
     notre methode home dans laquel on fait appel a la methode stop ( arreter le fonctionement de l'accelerometre et du magnetometre)
     et appeler la methode goto qui permet a notre drone de recevoir la nouvelle position
    */

    public void home(Button starStop) {
        stop();
        starStop.setText("Démarrer");
        goTo(drone);
    }

     /*
     notre methode home qui permet de mettre la vitesse du drone a zero
     faire un clear sur la map ( donc tout les markers et les polylines vont disparaitrent )

    */

    public void goTo(Drone d) {
        drone.setPosition(d.getPositionOrigineX(),d.getPositionOrigineY());
        this.drone.setVitesse(0);
        this.drone.clearMap();
        this.onMapReady(mMap);
    }

    /*------------------------------------------methode arondir---------------------------------------------------------*/

    /*

    ces deux methodes nous permettent d'arondir un chiffre dans un premiers temps
    et les mettres dans une liste dans un deuxieme temps

    */
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
}
