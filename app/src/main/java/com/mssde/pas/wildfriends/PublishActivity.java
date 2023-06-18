package com.mssde.pas.wildfriends;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public static final String STORAGE_PATH_UPLOADS = "images/";

    //Firebase Storage
    LocationManager mLocationManager;

    //FirebaseDatabase database;
    FirebaseDatabase database_ads;

    //Firebase objects
    private StorageReference storageReference;
    DatabaseReference myRef;

    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    private boolean status;

    private EditText type,breed,info,location,date;

    private TextView img_text;
    private ImageView last_img;
    //uri to store file
    private List<Uri> filePath = new ArrayList<>();

    private List<String> images = new ArrayList<>();

    Location current_location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish);

        storageReference = FirebaseStorage.getInstance().getReference();
        database_ads = FirebaseDatabase.getInstance();
        myRef = database_ads.getReference();

        Button publish_ad,find_location,browse;

        find_location = (Button) findViewById(R.id.ubi_button);
        browse = (Button) findViewById(R.id.browse_button);

        type = (EditText) findViewById(R.id.animal_type);
        breed = (EditText) findViewById(R.id.breed);
        info = (EditText) findViewById(R.id.info);
        date = (EditText) findViewById(R.id.date_publish);
        location = (EditText) findViewById(R.id.location);


        img_text = (TextView) findViewById(R.id.img_text);

        last_img = (ImageView) findViewById(R.id.image_uploaded);


        publish_ad = (Button) findViewById(R.id.publish_button);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browse_img();
            }
        });

        find_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        publish_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                Anuncio anuncio = new Anuncio(user.getDisplayName(),
                        date.getText().toString(), type.getText().toString(),
                        breed.getText().toString(), info.getText().toString(),
                        current_location,status,email);

                //anuncio.setImages(images);
                Log.e("TAG","Num imgs: "+images.size());

                uploadFile(anuncio);
                //uploadAd(anuncio);
                //openAnuncioActivity(anuncio);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath.add(data.getData());
            img_text.setText("Imágenes: " + filePath.size() + " Seleccionadas");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                //images.add(data.getData());
                last_img.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLastKnownLocation();
            }
        }
    }

    public void openAnuncioActivity(Anuncio anuncio){
        Intent intent = new Intent(this, AnuncioActivity.class);
        intent.putExtra("Anuncio",anuncio);
        startActivity(intent);
    }

    private Location getLastKnownLocation(){
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        android.location.Location bestLocation = null;
        Location myLocation = null;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        }
        else{
            current_location = getLastKnownLocation();
            String loc = current_location.getLatitude() + ", " +current_location.getLongitude();
            location.setText(loc);
        }
    }

    private void uploadAd(Anuncio anuncio){
        myRef.child("advertisements/"+anuncio.getId()).push().setValue(anuncio.toMap());
        myRef.child("ad_ids/").push().setValue(anuncio.getId());
    }

    private void uploadFile(Anuncio ad) {
        //checking if file is available
        final int[] i = {0};
        if (!filePath.isEmpty()) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //getting the storage reference
            for(Uri path: filePath) {
                final StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + ad.getId() + "/" + System.currentTimeMillis() + "." + getFileExtension(path));
                //i++;

                //adding the file to reference
                //int finalI = i;
                sRef.putFile(path)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            i[0]++;
                            //dismissing the progress dialog
                            //String sFilePath = path.toString();
                            String sSessionUri = taskSnapshot.getUploadSessionUri().toString().trim();
                            //String sMetadataUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            //images.add(sSessionUri);
                            //Map<String, Object> childUpdates = new HashMap<>();
                            //childUpdates.put("/advertisements/"+ad.getId()+"/images", sSessionUri);

                            //myRef.child("advertisements").child(ad.getId()).child("images").push().setValue(sSessionUri);
                            ad.addImage(sSessionUri);
                            progressDialog.dismiss();
                            //myRef.updateChildren(childUpdates);
                            Log.e("TAG", "SesUri "+sSessionUri);
                            Toast.makeText(getApplicationContext(), "File Uploaded!", Toast.LENGTH_LONG).show(); // ["+sFilePath+"]["+sSessionUri + "][" + sMetadataUrl+"]
                            if(i[0] == filePath.size()){
                                Log.e("TAG", "Update final: "+i[0]+" ");
                                uploadAd(ad);
                                openAnuncioActivity(ad);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
            }
        } else {
            //display an error if no file is selected
            Toast.makeText(getApplicationContext(), "Please select a file ... ", Toast.LENGTH_LONG).show();
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        if (view.getId() == R.id.encont_opt)
            if (checked)
                status = false;
        if (view.getId() == R.id.perdido_opt)
            if (checked)
                status = true;
    }
    private void showDatePickerDialog(){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String selectedDate = dayOfMonth + "/" + (month+1) + "/" + year;
                date.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(),"datePicker");

    }

    private void browse_img() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

}

