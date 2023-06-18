package com.mssde.pas.wildfriends;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdvertisementsAdapter
        extends RecyclerView.Adapter<AdvertisementsAdapter.ViewHolder> {
        //implements View.OnClickListener {

    private List<Anuncio> ads;
    private View.OnClickListener listener;
    private Context context;
    public AdvertisementsAdapter(List<Anuncio> adList, Context context) {
        this.ads = adList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_listed, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Anuncio ad = ads.get(position);
        holder.ad_holder = ad;
        if (ad.isStatus()) {
            holder.title.setText("Perdido");
        } else {
            holder.title.setText("Encontrado");
        }

        holder.info.setText("Info:\n" + ad.getInfo());

        /*holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });*/

        //Picasso.get().load(noticia.getUrl()).into(holder.imagen);


    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.show_ad){

                }
                else if (item.getItemId() == R.id.in_map){

                }
                return false;
            }
        });

        popup.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView info;
        private ImageView img;
        private ImageButton button;

        Anuncio ad_holder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_ad_list);
            info = itemView.findViewById(R.id.text_ad_list);
            img = itemView.findViewById(R.id.img_ad_list);
            button = itemView.findViewById(R.id.options_ad_button);

            /*button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG","Button info: "+ad_holder.getInfo());
                    Intent intent = new Intent(itemView.getContext(), AnuncioActivity.class);
                    intent.putExtra("Anuncio",ad_holder);
                    startActivity(itemView.getContext(),intent,intent.getExtras());
                }
            });*/

        }
    }

}
