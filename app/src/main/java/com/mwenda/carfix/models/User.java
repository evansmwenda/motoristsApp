package com.mwenda.carfix.models;

public class User {

    private String id;
    private String email_address;
    private String phone_number;
    private String company_name;
    private String distance;


    public User(String id, String email_address, String phone_number, String company_name,String distance) {
        this.id = id;
        this.email_address = email_address;
        this.phone_number = phone_number;
        this.company_name = company_name;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
