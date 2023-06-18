package com.mssde.pas.wildfriends;

import java.io.Serializable;

public class Location implements Serializable {

    private String Name;
    private String Description;
    private double Latitude;
    private double Longitude;

    public Location(String name, String description, double latitude, double longitude) {
        this.Name = name;
        this.Description = description;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }
    public Location(String[] location) {
        this.Latitude = Double.parseDouble(location[0]);
        this.Longitude = Double.parseDouble(location[1]);
    }

    public Location(double latitude, double longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
