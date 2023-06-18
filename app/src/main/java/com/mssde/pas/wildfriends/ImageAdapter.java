package com.mssde.pas.wildfriends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

class ImageAdapter extends PagerAdapter {

    // Context object
    Context context;

    // Array of images
    List<String> images;

    // Layout Inflater
    LayoutInflater mLayoutInflater;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;


    // Viewpager Constructor
    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    /*@Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }*/

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.image_item, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_item);


        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl(images.get(position));

        // setting the image in the imageView
        //imageView.setImageResource(images[position]);
        Log.e("TAG","URL of Firebase Storage"+images.get(position));
        Glide.with(context)
                .load(mStorageRef)
                .into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
