package com.vCare.murlipurajaipurswm.Model;

public class NewComplainModel {

    String complainType;
    String message;
    String action;
    String date;
    String number;
    String zone;
    String comlaintNumber;
    String cardNumber;
    String name;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewComplainModel(String complainType, String message, String action, String date, String number, String zone, String comlaintNumber, String cardNumber, String name) {
        this.complainType = complainType;
        this.message = message;
        this.action = action;
        this.date = date;
        this.number = number;
        this.zone = zone;
        this.comlaintNumber = comlaintNumber;
        this.cardNumber = cardNumber;
        this.name = name;
    }

    public NewComplainModel() {}

    public String getComplainType() {
        return complainType;
    }

    public void setComplainType(String complainType) {
        this.complainType = complainType;
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

    public String getComlaintNumber() {
        return comlaintNumber;
    }

    public void setComlaintNumber(String comlaintNumber) {
        this.comlaintNumber = comlaintNumber;
    }
}
