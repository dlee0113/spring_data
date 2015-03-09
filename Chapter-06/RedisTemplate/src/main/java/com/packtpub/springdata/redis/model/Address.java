package com.packtpub.springdata.redis.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

/**
 * A component for address information.
 * @author Petri Kainulainen
 */
public class Address {

    public static final int MAX_LENGTH_COUNTRY = 20;
    public static final int MAX_LENGTH_STREET_ADDRESS = 150;
    public static final int MAX_LENGTH_POST_CODE = 10;
    public static final int MAX_LENGTH_POST_OFFICE = 40;
    public static final int MAX_LENGTH_STATE = 20;

    @Length(max = Address.MAX_LENGTH_COUNTRY)
    private String country;

    @Length(max = Address.MAX_LENGTH_STREET_ADDRESS)
    private String streetAddress;

    @Length(max = Address.MAX_LENGTH_POST_CODE)
    private String postCode;

    @Length(max = Address.MAX_LENGTH_POST_OFFICE)
    private String postOffice;

    @Length(max = Address.MAX_LENGTH_STATE)
    private String state;

    public Address() {

    }

    public String getCountry() {
        return country;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }

    public void setState(String state) {
        this.state = state;
    }
}
