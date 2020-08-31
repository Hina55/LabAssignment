package com.hina.complainapp.models;


public class ComplaintHelperClass {
    private String username;
    private String image;
    private double latitude;
    private double longitude;
    private String category;
    private String severity;
    private String description;
    private String date;
    private String time;




    public ComplaintHelperClass(String username, String image, double latitude, double longitude, String category, String severity, String description, String date, String time) {
        this.username=username;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.severity = severity;
        this.description = description;
        this.date=date;
        this.time=time;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double logitude) {
        this.longitude = logitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
