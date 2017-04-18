package com.sarahrobinson.finalyearproject;

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

    public User() {

    }

    public User(String id, String name, String email, String image, List<String> favouritePlaces) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.image = image;
        this.favouritePlaces = favouritePlaces;
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
}