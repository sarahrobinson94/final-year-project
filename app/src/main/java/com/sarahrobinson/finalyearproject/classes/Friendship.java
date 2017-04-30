package com.sarahrobinson.finalyearproject.classes;

import java.util.List;

/**
 * Created by sarahrobinson on 01/05/2017.
 */

public class Friendship {

    private String requestBy;
    private String requestDate;
    private String requestStatus;
    private String acceptDate;

    public Friendship() {

    }

    public Friendship(String requestBy, String requestDate, String requestStatus, String acceptDate) {
        this.requestBy = requestBy;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
        this.acceptDate = acceptDate;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(String acceptDate) {
        this.acceptDate = acceptDate;
    }

}