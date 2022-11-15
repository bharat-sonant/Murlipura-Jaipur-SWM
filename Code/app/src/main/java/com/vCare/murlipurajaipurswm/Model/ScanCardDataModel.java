package com.vCare.murlipurajaipurswm.Model;

public class ScanCardDataModel {
    String date;
    String time;

    public ScanCardDataModel(){}

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

    public ScanCardDataModel(String date, String time) {
        this.date = date;
        this.time = time;
    }
}
