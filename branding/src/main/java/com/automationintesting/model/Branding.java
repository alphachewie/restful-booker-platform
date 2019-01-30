package com.automationintesting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Branding {

    @JsonProperty
    private int brandingid;
    @JsonProperty
    private Map map;
    @JsonProperty
    private Logo logo;
    @JsonProperty
    private String description;
    @JsonProperty
    private Contact contact;

    public Branding(Map map, Logo logo, String description, Contact contact) {
        this.map = map;
        this.logo = logo;
        this.description = description;
        this.contact = contact;
    }

    public Branding(ResultSet result) throws SQLException {
        this.map = new Map();
        this.logo = new Logo();
        this.description = result.getString("description");
        this.contact = new Contact();
    }

    public int getBrandingid() {
        return brandingid;
    }

    public Map getMap() {
        return map;
    }

    public Logo getLogo() {
        return logo;
    }

    public String getDescription() {
        return description;
    }

    public Contact getContact() {
        return contact;
    }

    public void setBrandingid(int brandingid) {
        this.brandingid = brandingid;
    }

}
