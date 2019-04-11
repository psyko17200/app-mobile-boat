package com.example.drone;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class visualisationActivity extends FragmentActivity implements OnMapReadyCallback {

    protected GoogleMap mMap;
    protected connect_et_recup s;
    protected TextView info;
    protected EditText ip;
    protected EditText port;
    protected Button connect;
    protected Button disconnect;
    protected SupportMapFragment mapFragment;
    protected BottomNavigationView bottomNavigationView;
    protected AlertDialog popUpCo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualisation_activity); //mise en relation entre l'activité et la vue
        this.ip=findViewById(R.id.ip);
        this.port=findViewById(R.id.port);
        this.connect=findViewById(R.id.connect);
        this.disconnect =findViewById(R.id.disconnect);
        this.info=findViewById(R.id.info);
        this.bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.findViewById(R.id.tracer).setVisibility(View.GONE);

        //Popup qui va nous servir a afficher un message d'erreur si jamais la connection ne fonctionne pas ou les données entrées n'ont pas le bon format
        this.popUpCo=new AlertDialog.Builder(visualisationActivity.this).create();
        TextView title=new TextView(visualisationActivity.this);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText("Erreur de connection");
        title.setTextSize(25);
        this.popUpCo.setCustomTitle(title);
        LinearLayout popUp=new LinearLayout(visualisationActivity.this);
        popUp.setOrientation(LinearLayout.VERTICAL);
        TextView txtPopUp=new TextView(visualisationActivity.this);
        txtPopUp.setGravity(Gravity.CENTER);
        txtPopUp.setText("Entrées invalide ou serveur déconnecté");
        Button ok = new Button(visualisationActivity.this);
        ok.setText("ok");
        popUp.addView(txtPopUp);
        popUp.addView(ok);
        popUpCo.setView(popUp);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpCo.dismiss();
            }
        });

        this.disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.returnMenu();
            }
        });
        this.connect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!ip.getText().toString().isEmpty() && !port.getText().toString().isEmpty() && port.getText().toString().matches("^[0-9]+$") && ip.getText().toString().matches("([0-9]{1,3}.){3}[0-9]{1,3}")) {
                    //Creation du thread qui va recuperer les trames (correspond a la classe connect_et_recup)
                    s = new connect_et_recup(ip.getText().toString(),Integer.valueOf(port.getText().toString()), visualisationActivity.this);
                    s.addMap(mMap);
                    s.start();
                }else{
                popUpCo.show();
                }
            }
        });

        //Correspond a la navigation bar qui permet de naviguer entre les activités
        this.bottomNavigationView.setSelectedItemId(R.id.act1);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.act2:
                        Intent goToAct2 = new Intent(visualisationActivity.this, piloteActivity.class);
                        startActivity(goToAct2);
                        finish();
                        break;
                    case R.id.act3:
                        Intent goToAct3= new Intent(visualisationActivity.this, itineraireActivity.class);
                        startActivity(goToAct3);
                        finish();
                        break;
                }
                return true;
            }
        });
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
    //Demarage de la map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
}
