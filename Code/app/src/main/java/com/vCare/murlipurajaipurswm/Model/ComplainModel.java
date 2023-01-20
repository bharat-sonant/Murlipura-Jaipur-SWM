package com.vCare.murlipurajaipurswm.Model;

public class ComplainModel {

    public String message;
    public String date;
    public String type;
    public String status;

    public ComplainModel(){}

    public ComplainModel(String message, String date, String type, String status) {
        this.message = message;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
