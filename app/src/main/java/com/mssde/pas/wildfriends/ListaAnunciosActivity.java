package com.mssde.pas.wildfriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaAnunciosActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef_Id;
    private DatabaseReference databaseRef_Value;

    List<Anuncio> ad_List;
    List<String> id_List;

    RecyclerView adsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_anuncios);

        ad_List = new ArrayList<>();
        id_List = new ArrayList<>();

        adsList = (RecyclerView) findViewById(R.id.listAds);

        AdvertisementsAdapter adapter = setUpAdapter();

        setUpData(adapter);

        setUpListener(adapter);

    }

    private void setUpListener(AdvertisementsAdapter adapter) {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","Button from lista: "+ad_List.get(adsList.getChildAdapterPosition(v)));
                Intent intent = new Intent(getApplicationContext(), AnuncioActivity.class);
                intent.putExtra("Anuncio",ad_List.get(adsList.getChildAdapterPosition(v)));
                startActivity(intent);
            }
        });
    }

    /*public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(adsList.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
    }*/

    public AdvertisementsAdapter setUpAdapter(){
        AdvertisementsAdapter adapter = new AdvertisementsAdapter(ad_List,getApplicationContext());
        adsList.setAdapter(adapter);
        adsList.setLayoutManager(new LinearLayoutManager(this));

        return adapter;
    }

    public void setUpData(AdvertisementsAdapter adapter){

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

                                runOnUiThread(()->adapter.notifyDataSetChanged());
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

}