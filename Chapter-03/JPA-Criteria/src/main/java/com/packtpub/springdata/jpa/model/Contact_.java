package com.packtpub.springdata.jpa.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author Petri Kainulainen
 */
@StaticMetamodel(Contact.class)
public class Contact_ {
    public static volatile SingularAttribute<Contact, String> firstName;
    public static volatile SingularAttribute<Contact, String> lastName;
}
