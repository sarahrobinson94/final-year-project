package com.sarahrobinson.finalyearproject.classes;

import java.util.List;
import java.util.Map;

/**
 * Created by sarahrobinson on 19/03/2017.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String image;
    private Map<String, Object> favouritePlaces;
    private Map<String, Object> events;

    public User() {

    }

    public User(String id, String name, String email, String image, Map<String, Object> favouritePlaces, Map<String, Object> events) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.image = image;
        this.favouritePlaces = favouritePlaces;
        this.events = events;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getFavouritePlaces() {
        return favouritePlaces;
    }

    public void setFavouritePlaces(Map<String, Object> favouritePlaces) {
        this.favouritePlaces = favouritePlaces;
    }

    public Map<String, Object> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Object> events) {
        this.events = events;
    }

}