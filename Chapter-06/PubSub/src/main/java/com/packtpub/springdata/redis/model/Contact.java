package com.packtpub.springdata.redis.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

/**
 * A model object for contacts.
 * @author Petri Kainulainen
 */
public class Contact {

    public static final int MAX_LENGTH_EMAIL_ADDRESS = 100;
    public static final int MAX_LENGTH_FIRST_NAME = 50;
    public static final int MAX_LENGTH_LAST_NAME = 100;
    public static final int MAX_LENGTH_PHONE_NUMBER = 30;

    private Long id;

    @Valid
    private Address address;

    @Email
    @Length(max = Contact.MAX_LENGTH_EMAIL_ADDRESS)
    private String emailAddress;

    @NotEmpty
    @Length(max = Contact.MAX_LENGTH_FIRST_NAME)
    private String firstName;

    @NotEmpty
    @Length(max = Contact.MAX_LENGTH_LAST_NAME)
    private String lastName;

    @Length(max = Contact.MAX_LENGTH_PHONE_NUMBER)
    private String phoneNumber;

    public Contact() {

    }

    public Long getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
