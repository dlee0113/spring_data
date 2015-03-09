package com.packtpub.springdata.jpa.repository;

import com.mysema.query.types.Predicate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class ContactPredicatesTest {

    private static final String EXPECTED_PREDICATE_STRING = "startsWithIgnoreCase(contact.firstName,Foo) || startsWithIgnoreCase(contact.lastName,Foo)";
    private static final String SEARCH_TERM = "Foo";

    @Test
    public void firstOrLastNameStartsWith() {
        Predicate predicate = ContactPredicates.firstOrLastNameStartsWith(SEARCH_TERM);

        assertEquals(EXPECTED_PREDICATE_STRING, predicate.toString());
    }
}
