package com.vCare.murlipurajaipurswm.Model;

public class ComplainModel {

    public String sno;
    public String date;
    public String type;
    public String status;

    public ComplainModel(String sno, String date, String type, String status) {
        this.sno = sno;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
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
