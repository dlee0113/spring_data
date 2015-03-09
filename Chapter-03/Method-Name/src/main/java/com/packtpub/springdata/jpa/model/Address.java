package com.packtpub.springdata.jpa.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A component for address information.
 * @author Petri Kainulainen
 */
@Embeddable
public class Address {

    public static final int MAX_LENGTH_COUNTRY = 20;
    public static final int MAX_LENGTH_STREET_ADDRESS = 150;
    public static final int MAX_LENGTH_POST_CODE = 10;
    public static final int MAX_LENGTH_POST_OFFICE = 40;
    public static final int MAX_LENGTH_STATE = 20;

    @Column(name = "country", length = MAX_LENGTH_COUNTRY)
    private String country;

    @Column(name = "street_address", length = MAX_LENGTH_STREET_ADDRESS)
    private String streetAddress;

    @Column(name = "post_code", length = MAX_LENGTH_POST_CODE)
    private String postCode;

    @Column(name = "post_office", length = MAX_LENGTH_POST_OFFICE)
    private String postOffice;

    @Column(name = "state", length = MAX_LENGTH_STATE)
    private String state;

    public Address() {

    }

    public static Builder getBuilder(String streetAddress, String postCode, String postOffice) {
        return new Builder(streetAddress, postCode, postOffice);
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

    public void update(String streetAddress, String postCode, String postOffice, String state, String country) {
        this.streetAddress = streetAddress;
        this.postCode = postCode;
        this.postOffice = postOffice;
        this.state = state;
        this.country = country;
    }

    public static class Builder {

        private Address built;

        public Builder(String streetAddress, String postCode, String postOffice) {
            built = new Address();
            built.streetAddress = streetAddress;
            built.postCode = postCode;
            built.postOffice = postOffice;
        }

        public Builder country(String country) {
            built.country = country;
            return this;
        }

        public Builder state(String state) {
            built.state = state;
            return this;
        }

        public Address build() {
            return built;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
