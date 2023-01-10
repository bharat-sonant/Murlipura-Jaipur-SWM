package com.vCare.murlipurajaipurswm.Model;

public class NewComplainMappingModel {

    String card;
    String complaintDataPath;
    String date;
    String complaintSource;

    public NewComplainMappingModel(String card, String complaintDataPath, String date, String complaintSource) {
        this.card = card;
        this.complaintDataPath = complaintDataPath;
        this.date = date;
        this.complaintSource = complaintSource;
    }

    public String getComplaintSource() {
        return complaintSource;
    }

    public void setComplaintSource(String complaintSource) {
        this.complaintSource = complaintSource;
    }

    public NewComplainMappingModel(){}
    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getComplaintDataPath() {
        return complaintDataPath;
    }

    public void setComplaintDataPath(String complaintDataPath) {
        this.complaintDataPath = complaintDataPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
