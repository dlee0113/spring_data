package com.packtpub.springdata.jpa.repository;

import com.mysema.query.types.Predicate;
import com.packtpub.springdata.jpa.model.QContact;

/**
 * A builder class used to build Querydsl predicates.
 * @author Petri Kainulainen
 */
public class ContactPredicates {

    /**
     * Builds a Querydsl predicate which ensures that the first or last name
     * of a contact starts with the given search term when case is ignored.
     * @param searchTerm    The used search term.
     * @return
     */
    public static Predicate firstOrLastNameStartsWith(final String searchTerm) {
        QContact contact = QContact.contact;

        return contact.firstName.startsWithIgnoreCase(searchTerm)
                .or(contact.lastName.startsWithIgnoreCase(searchTerm));
    }
}
