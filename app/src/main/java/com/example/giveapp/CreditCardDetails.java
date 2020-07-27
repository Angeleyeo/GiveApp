package com.example.giveapp;

import java.io.Serializable;

public class CreditCardDetails implements Serializable {

    private String name;
    private String cardNum;
    private String last4;
    private int expMth;
    private int expYear;
    private String CCV;
    private String logoUrl;
    private String id;


    public CreditCardDetails() {

    }

    public CreditCardDetails(String name, String cardNum, String last4, int expMth, int expYear, String CCV, String logoUrl) {
        this.name = name;
        this.cardNum = cardNum;
        this.last4 = last4;
        this.expMth = expMth;
        this.expYear = expYear;
        this.CCV = CCV;
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public String getCardNum() {
        return cardNum;
    }

    public String getLast4() {
        return last4;
    }

    public int getExpMth() {
        return expMth;
    }

    public int getExpYear() {
        return expYear;
    }

    public String getCCV() {
        return CCV;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
