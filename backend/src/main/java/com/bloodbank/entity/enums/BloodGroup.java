package com.bloodbank.entity.enums;

public enum BloodGroup {
    A_POS("A+"), A_NEG("A-"), 
    B_POS("B+"), B_NEG("B-"), 
    O_POS("O+"), O_NEG("O-"), 
    AB_POS("AB+"), AB_NEG("AB-");

    private String value;
    BloodGroup(String value) { this.value = value; }
    public String getValue() { return value; }
}
