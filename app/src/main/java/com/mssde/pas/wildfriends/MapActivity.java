package com.mssde.pas.wildfriends;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private MapView map;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef_Id;
    private DatabaseReference databaseRef_Value;
    LocationManager mLocationManager;
    List<Anuncio> ad_List;
    List<String> id_List;

    Location current_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        ad_List = new ArrayList<>();
        id_List = new ArrayList<>();

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);

        getLocation();

        GeoPoint startPoint = new GeoPoint(current_location.getLatitude(), current_location.getLongitude());

        map.getController().setZoom(15); // nivel de zoom
        map.getController().setCenter(startPoint);

        setUpData();

        setUpLocations();

    }

    private void setUpLocations() {
        // Agregar los marcadores al mapa
        for (Anuncio loc : ad_List) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(loc.getLocation().getLatitude(), loc.getLocation().getLongitude()));
            if(loc.isStatus()){
                m.setTitle("Perdido " + loc.getDate());
            }
            else{
                m.setTitle("Encontrado " + loc.getDate());
            }

            m.setSnippet(loc.getInfo());

            map.getOverlays().add(m);
        }
    }

    public void setUpData(){

        Map<String, Object> temp_data = new HashMap<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef_Id = firebaseDatabase.getReference("/advertisements");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot_ads : dataSnapshot.getChildren()) {
                    //Log.e("btb", "Key: " + (String) dataSnapshot_ads.getKey());
                    id_List.add((String) dataSnapshot_ads.getKey());
                    databaseRef_Value = databaseRef_Id.child((String) dataSnapshot_ads.getKey());
                    ValueEventListener eventListenerValue = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot_values : snapshot.getChildren()) {
                                //Log.e("btb", "Key2: " + (String) dataSnapshot_values.getKey());

                                for (DataSnapshot value : dataSnapshot_values.getChildren()) {
                                    //Log.e("btb", "Key3: " + (String) value.getKey());
                                    temp_data.put((String) value.getKey(), value.getValue());
                                }
                                Anuncio ad_temp = new Anuncio((HashMap<String, Object>) temp_data);
                                ad_List.add(ad_temp);
                                //Log.e("TAG", "Num IDs: " + id_List.size() + " Num Ads: " + ad_List.size());
                                setUpLocations();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("firebase", "Database Error", error.toException());
                        }
                    };
                    databaseRef_Value.addListenerForSingleValueEvent(eventListenerValue);
                }
                //Log.e("TAG", "Num IDs: " + id_List.size() + " Num Ads: " + ad_List.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        };
        databaseRef_Id.addListenerForSingleValueEvent(eventListener);
    }

    private Location getLastKnownLocation(){
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        android.location.Location bestLocation = null;
        Location myLocation = null;
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            for (String provider: providers){
                android.location.Location loc = mLocationManager.getLastKnownLocation(provider);
                if(loc == null){
                    continue;
                }
                if(bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()){
                    bestLocation = loc;
                }
                else {
                    myLocation = new Location(bestLocation.getLatitude(), bestLocation.getLongitude());
                }
            }
        }
        return myLocation;
    }

    public void getLocation(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        }
        else{
            current_location = getLastKnownLocation();
        }
    }
}