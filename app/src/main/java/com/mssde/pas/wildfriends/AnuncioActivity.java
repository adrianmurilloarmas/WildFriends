package com.mssde.pas.wildfriends;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnuncioActivity extends AppCompatActivity {
    // creating object of ViewPager
    private ViewPager imageViewPager;

    //FirebaseDatabase database;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef_Id;
    private DatabaseReference databaseRef_Value;
    //FirebaseDatabase database_ads;
    DatabaseReference myRef;

    //Firebase objects
    private StorageReference storageReference;
    private Anuncio anuncio;

    private TextView title;
    private TextView date;
    private TextView type;
    private TextView breed;
    private TextView location;
    private TextView info;

    private TextView contact;

    // images array
    private List<String> storage_uri;

    List<Anuncio> ad_List;
    List<String> id_List;

    // Creating Object of ViewPagerAdapter
    ImageAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anuncio);

        if(getIntent().getExtras() != null){
            anuncio = (Anuncio) getIntent().getSerializableExtra("Anuncio");
        }

        ad_List = new ArrayList<>();
        id_List = new ArrayList<>();

        storage_uri = new ArrayList<>();

        title = (TextView) findViewById(R.id.title_anuncio);
        date = (TextView) findViewById(R.id.ad_date);
        type = (TextView) findViewById(R.id.type_animal_ad);
        breed = (TextView) findViewById(R.id.breed_ad);
        location = (TextView) findViewById(R.id.location_ad);
        info = (TextView) findViewById(R.id.info_ad);

        contact = (TextView) findViewById(R.id.contact_button);

        imageViewPager = (ViewPager) findViewById(R.id.imageViewer);

        imgAdapter = new ImageAdapter(AnuncioActivity.this,anuncio.getImages());

        imageViewPager.setAdapter(imgAdapter);


        Log.e("TAG", "Img ID "+anuncio.getId());

        // Initializing the ViewPagerAdapter
        //imgAdapter = new ImageAdapter(AnuncioActivity.this, storage_uri,anuncio.getId());
        // Adding the Adapter to the ViewPager
        //images.setAdapter(imgAdapter);

        setUpData();
/*
        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.e("TAG", "Intento1 ");
                for (StorageReference item : listResult.getItems()) {
                    Log.e("TAG", "Intento2 ");
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("TAG", "Intento3 "+uri.toString());
                            storage_uri.add(uri.toString());
                            imgAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Error getting images", e);
            }
        });*/



        if(anuncio.isStatus()){
            title.setText("Perdido");
        }
        else{
            title.setText("Encontrado");
        }

        date.setText(getString(R.string.date_text)+anuncio.getDate());
        type.setText(getString(R.string.animal_type_ad)+ anuncio.getType());
        breed.setText(getString(R.string.breed_text)+anuncio.getBreed());
        location.setText(getString(R.string.location_ad)+anuncio.getLocation().getLatitude()+", "+anuncio.getLocation().getLongitude());
        info.setText(getString(R.string.info_ad)+anuncio.getInfo());

        contact.setText(anuncio.getEmail());
        //anuncio.setImages(storage_uri);
    }


    public void listenerValues(DataSnapshot dataSnapshot_ads){

        Map<String, Object> temp_data = new HashMap<>();
        id_List.add((String) dataSnapshot_ads.getKey());
        databaseRef_Value = databaseRef_Id.child((String) dataSnapshot_ads.getKey());


        ValueEventListener eventListenerValue = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot_values : snapshot.getChildren()) {
                    String key2 = (String) dataSnapshot_values.getKey();
                    //Log.e("btb", "Key2: " + key2);

                    for (DataSnapshot value : dataSnapshot_values.getChildren()) {
                        String key3 = (String) value.getKey();
                        //Log.e("btb", "Key3: " + key3);
                        temp_data.put(key3, value.getValue());
                    }

                    Anuncio ad_temp = new Anuncio((HashMap<String, Object>) temp_data);
                    ad_List.add(ad_temp);
                    //Log.e("TAG", "Num IDs: " + id_List.size() + " Num Ads: " + ad_List.size());

                    //runOnUiThread(()->adapter.notifyDataSetChanged());
                }
                //adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("firebase", "Database Error", error.toException());
            }
        };
        databaseRef_Value.addListenerForSingleValueEvent(eventListenerValue);
    }
    public void setUpData(){


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef_Id = firebaseDatabase.getReference("/advertisements");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot_ads : dataSnapshot.getChildren()) {
                    //Log.e("btb", "Key: " + (String) dataSnapshot_ads.getKey());
                    listenerValues(dataSnapshot_ads);
                    //listenerImages(dataSnapshot_ads,adapter);
                }
                Log.e("TAG","From AnuncioAct Size de Uris"+storage_uri.size());
                imgAdapter = new ImageAdapter(AnuncioActivity.this, storage_uri);
                // Adding the Adapter to the ViewPager
                imageViewPager.setAdapter(imgAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        };
        databaseRef_Id.addListenerForSingleValueEvent(eventListener);
    }
}