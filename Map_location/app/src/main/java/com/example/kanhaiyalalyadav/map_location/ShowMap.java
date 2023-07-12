package com.example.kanhaiyalalyadav.map_location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMap extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap m_map;
    boolean map_ready = false;
    public static final String EXTRA_MESSAGE2 = "message";
    String message = "";
    Double latti = 0d;
    Double longi = 0d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        Intent intent = getIntent();
        message = intent.getStringExtra(EXTRA_MESSAGE2);
        String []arr = message.split("\n");
        try {
            if (arr.length == 2) {
                String[] exact = arr[1].split(",");
                latti = Double.valueOf(exact[0]);
                longi = Double.valueOf(exact[1]);
                Toast.makeText(getApplicationContext(), "From ShowMap:\n" + exact[0] + "\n" + exact[1], Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Choose the correct message", Toast.LENGTH_SHORT).show();
        }catch(Exception e)
        {
            String msg = e.toString();
            Toast.makeText(getApplicationContext(), "Choose the correct message+ "+msg, Toast.LENGTH_SHORT).show();
        }
        Button btnMap = (Button) findViewById(R.id.btnMap2);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map_ready)
                    m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });


        Button btnSat = (Button) findViewById(R.id.btnSatellite2);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map_ready)
                    m_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        Button btnHyb = (Button) findViewById(R.id.btnHybrid2);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map_ready)
                    m_map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;
        map_ready = true;
        LatLng pos = new LatLng(latti,longi);
        MarkerOptions me =new MarkerOptions().position(pos).title("I am Here");
        m_map.addMarker(me);
        CameraPosition target = CameraPosition.builder().target(pos).zoom(18).build();
        m_map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}
