package com.example.asus.maptest4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements  OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /*GET THE FILE*/

    protected String doInBackground() {

        try {
            URL url = new URL("http://soundways.eu/interface/channel.php?_action=getChannel&channeldoi=7af3f6c49fafcc37a1d1d5249d3aff19");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                //System.out.print(stringBuilder.toString());
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }


    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }

        @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);

        LatLng sydney;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

            /*HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH*/

            try {
                JSONObject object = (JSONObject) new JSONTokener(doInBackground()).nextValue();

                JSONArray photos = object.getJSONArray("channels");
                //System.out.println("99999999999999999999999999999999999999999999999999999999999999999999");
                //System.out.print(object.toString());
                //System.out.println("99999999999999999999999999999999999999999999999999999999999999999999");
                JSONObject ob=photos.getJSONObject(0);
                //JSONObject lng=photos.getJSONObject(0);
                Double lat=ob.getDouble("channel_latitude");
                Double lng=ob.getDouble("channel_longitude");

                sydney = new LatLng(lat,lng);
                //sydney = new LatLng(0,0);

                //Log.println(Log.INFO,"stack", photos.toString());

            } catch (JSONException e) {
                // Appropriate error handling code
                Log.e("ERROR", e.getMessage(), e);
                sydney= new LatLng(0,0);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.addCircle(new CircleOptions()
                    .center(sydney)
                    .radius(5)
                    .strokeColor(Color.RED)
            );


            /*HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH*/
        }else{
            sydney= new LatLng(0,0);
        }


    }

}
