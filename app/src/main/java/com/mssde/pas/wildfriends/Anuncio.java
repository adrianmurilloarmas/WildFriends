package com.mssde.pas.wildfriends;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Anuncio implements Serializable {
    private String id;
    private String user;
    private String type;
    private String breed;
    private String info;
    private Location location;
    private List<String> images;
    private String date;

    private String email;


    private boolean status; //True-Lost, False-Found

    public Anuncio(){

    }

    public Anuncio(String id,String user,String date, String type, String breed, String info, Location location, boolean status,String email) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.type = type;
        this.breed = breed;
        this.info = info;
        this.location = location;
        this.images = new ArrayList<>();
        this.status = status;
        this.email = email;
    }

    public Anuncio(String user,String date, String type, String breed, String info, Location location, boolean status,String email) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.date = date;
        this.type = type;
        this.breed = breed;
        this.info = info;
        this.location = location;
        this.images = new ArrayList<>();
        this.status = status;
        this.email = email;
    }

    public Anuncio(HashMap<String,Object> ad){
        this.id = (String) ad.get("id");
        this.user = (String) ad.get("user");
        this.date = (String) ad.get("date");
        this.type = (String) ad.get("type");
        this.breed = (String) ad.get("breed");
        this.info = (String) ad.get("info");
        this.location = new Location((Double) ad.get("latitude"),(Double) ad.get("longitude"));
        this.status = (boolean) ad.get("status");
        this.email = (String) ad.get("email");
        for(int i=0; i<5;i++){
            try{
                this.images.add((String)ad.get("image_"+i));
            } catch (Exception e) {
                break;
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void addImage(String img){
        this.images.add(img);
    }

    public Map<String,Object> toMap(){
        Map<String,Object> ad = new HashMap<>();
        ad.put("user",this.user);
        ad.put("type",this.type);
        ad.put("breed",this.breed);
        ad.put("info",this.info);
        ad.put("latitude",this.location.getLatitude());
        ad.put("longitude",this.location.getLongitude());
        ad.put("date",this.date);
        ad.put("status",this.status);
        ad.put("email",this.email);
        for(int i=0; i<this.images.size(); i++){
            ad.put("image_"+i,this.images.get(i));
        }

        return ad;
    }
}
