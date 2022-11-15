package com.vCare.murlipurajaipurswm.Model;

public class NewComplainModel {

    String complaintype;
    String message;
    String action;
    String date;
    String number;
    String zone;
    String comlaintnumber;

    public NewComplainModel() {
    }

    public NewComplainModel(String complaintype, String message, String action, String date, String number, String zone, String comlaintnumber) {
        this.complaintype = complaintype;
        this.message = message;
        this.action = action;
        this.date = date;
        this.number = number;
        this.zone = zone;
        this.comlaintnumber = comlaintnumber;
    }

    public String getComplaintype() {
        return complaintype;
    }

    public void setComplaintype(String complaintype) {
        this.complaintype = complaintype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getComlaintnumber() {
        return comlaintnumber;
    }

    public void setComlaintnumber(String comlaintnumber) {
        this.comlaintnumber = comlaintnumber;
    }
}
