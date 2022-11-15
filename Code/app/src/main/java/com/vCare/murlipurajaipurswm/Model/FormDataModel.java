package com.vCare.murlipurajaipurswm.Model;

public class FormDataModel {
    String userName;
    String mobile;
    String latitude;
    String longitude;
    String address;
    String ward;

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public FormDataModel(String userName, String mobile, String latitude, String longitude, String address, String ward) {
        this.userName = userName;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.ward = ward;
    }

    public FormDataModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
