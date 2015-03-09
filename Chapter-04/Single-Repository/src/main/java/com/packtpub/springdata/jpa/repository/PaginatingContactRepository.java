package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.model.Contact;

import java.util.List;

/**
 * Declares the custom methods which are added to the contact repository.
 * @author Petri Kainulainen
 */
public interface PaginatingContactRepository {

    /**
     * Finds the number of contacts matching with the given search term.
     * @param searchTerm    The search term.
     * @return  The number of contacts matching with the given search search.
     */
    public long findContactCount(String searchTerm);

    /**
     * Finds all contacts for a page
     * @param pageIndex The index of the wanted page.
     * @param pageSize  The size of the wanted page.
     * @return  A list of contacts.
     */
    public List<Contact> findAllForPage(int pageIndex, int pageSize);

    /**
     * Finds contacts which matches with the given search term and
     * belongs to the given page.
     * @param searchTerm    The search term.
     * @param pageIndex     The index of wanted page.
     * @param pageSize      The size of the wanted page.
     * @return  A list of contacts.
     */
    public List<Contact> findContactsForPage(String searchTerm, int pageIndex, int pageSize);
}
