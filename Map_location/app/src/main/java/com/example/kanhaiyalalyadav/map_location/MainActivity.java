package com.example.kanhaiyalalyadav.map_location;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    GoogleMap mMap;
    boolean mapReady = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    public static double lattitude = 0;
    public static double longitude = 0;

    protected LocationRequest mLocationRequest;

    static final CameraPosition NEWYORK = CameraPosition.builder().target(new LatLng(40.784,-73.9857))
                                            .zoom(21).bearing(0).tilt(45).build();

    static final CameraPosition WALCHAND =CameraPosition.builder().target(new LatLng(16.8459,74.6013))
            .zoom(17).bearing(90).tilt(45).build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buildGoogleApiClient();
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });


        Button btnSattelite = (Button) findViewById(R.id.btnSatellite);
        btnSattelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        Button btnNewYotk = (Button) findViewById(R.id.btnSendLocation);
        btnNewYotk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String loc = String.valueOf(lattitude)+","+String.valueOf(longitude);
                Intent intent = new Intent(MainActivity.this,SendSms.class);
                intent.putExtra(SendSms.EXTRA_MESSAGE,loc);
                startActivity(intent);
            }
        });


        Button btnWal = (Button) findViewById(R.id.btnWalchand);
        btnWal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(WALCHAND);
            }
        });

        Button findMe = (Button) findViewById(R.id.btnFindMe);
        findMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady) {
                    MarkerOptions me =new MarkerOptions().position(new LatLng(lattitude,longitude)).title("I am Here");
                    mMap.addMarker(me);
                    CameraPosition MY_DEST =CameraPosition.builder().target(new LatLng(lattitude,longitude))
                            .zoom(17).bearing(90).tilt(45).build();
                    Toast.makeText(MainActivity.this,Double.toString(lattitude)+"\n"+Double.toString(lattitude),Toast.LENGTH_SHORT);
                    flyTo(MY_DEST);
                }
            }
        });


        Button readAndView = (Button) findViewById(R.id.btnReadSms);
        readAndView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity.this,ReceiveSms.class);
                startActivity(intent);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void flyTo(CameraPosition target)
    {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target),10000,null);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {

        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null)
                {
                    lattitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }

            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }



      /*mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lattitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }*/
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);




    }



    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {


                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Thic activity", location.toString());
        //txtOutput.setText(location.toString());

        lattitude = location.getLatitude();
        longitude = location.getLongitude();
        //      mLatitudeText.setText(String.valueOf(location.getLatitude()));
        //    mLongitudeText.setText(String.valueOf(location.getLongitude()));
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Tag-------->","Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Tag-------->", ""+connectionResult.getErrorMessage());
    }


    public void onDisconnected()
    {
        Log.i("Tag-------->","Disconnected");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        LatLng nala = new LatLng(19.4328,72.8456);
        CameraPosition target = CameraPosition.builder().target(nala).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

    }
}
