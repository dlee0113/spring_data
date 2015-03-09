package com.packtpub.springdata.jpa.dto;

/**
 * @author Petri Kainulainen
 */
public class ContactListDTO {

    private Long id;

    private String firstName;

    private String lastName;

    public ContactListDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
