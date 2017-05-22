package com.sarahrobinson.finalyearproject.classes;

import java.util.List;

/**
 * Created by sarahrobinson on 19/03/2017.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String image;
    private List<String> favouritePlaces;
    private List<String> events;

    public User() {

    }

    public User(String id, String name, String email, String image, List<String> favouritePlaces, List<String> events) {
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

    public List<String> getFavouritePlaces() {
        return favouritePlaces;
    }

    public void setFavouritePlaces(List<String> favouritePlaces) {
        this.favouritePlaces = favouritePlaces;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

}