package com.ar.echoafcavlapplication.Data;

import android.location.Location;

import java.util.Date;

public class RealmDataWrapper {
    private Location location;
    private Date date;

    public RealmDataWrapper() {
    }

    public RealmDataWrapper(Location location, Date date) {
        this.location = location;
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

