package com.sarahrobinson.finalyearproject.classes;

import java.util.List;

/**
 * Created by sarahrobinson on 26/04/2017.
 */

public class Event {

    private String name;
    private String description;
    private String date;
    private String time;
    private String location;
    private String locationId;
    private String image;
    private String creator;
    private List<String> invited;

    public Event() {

    }

    public Event(String name, String description, String date, String time, String location, String locationId, String image, String creator, List<String> invited) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.locationId = locationId;
        this.image = image;
        this.creator = creator;
        this.invited = invited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getInvited() {
        return invited;
    }

    public void setInvited(List<String> invited) {
        this.invited = invited;
    }
}