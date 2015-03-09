package com.packtpub.springdata.jpa.service;

import com.packtpub.springdata.jpa.dto.ContactDTO;
import com.packtpub.springdata.jpa.dto.SearchDTO;
import com.packtpub.springdata.jpa.model.Contact;

import java.util.List;

/**
 * Specifies the service methods for contracts.
 * @author Petri Kainulainen
 */
public interface ContactService {

    /**
     * Adds a new contact.
     * @param added The information of the added contact.
     * @return  The added contact.
     */
    public Contact add(ContactDTO added);

    /**
     * Gets the count of contacts.
     * @return  The count of all contacts.
     */
    public long count();

    /**
     * Gets the count of contacts matching the given search conditions.
     * @param dto   The search conditions
     * @return  The count of contacts matching with the given search conditions.
     */
    public long count(SearchDTO dto);

    /**
     * Deletes a contact.
     * @param id    The id of the deleted contact.
     * @return  The deleted contact.
     * @throws NotFoundException    if a contact is not found with the given id.
     */
    public Contact deleteById(Long id) throws NotFoundException;

    /**
     * Finds contacts for page.
     * @param pageIndex The index of the page.
     * @param pageSize  The number of contacts per page.
     * @return  A list of contacts for the given page.
     */
    public List<Contact> findAllForPage(int pageIndex, int pageSize);

    /**
     * Finds a contact.
     * @param id    The id of the wanted contact.
     * @return  The found contact.
     * @throws NotFoundException    if no contact is found with the given id.
     */
    public Contact findById(Long id) throws NotFoundException;


    /**
     * Searches contacts.
     * @param dto   The used search criteria.
     * @return  A list of contacts. If no contacts is found, this method returns an empty list.
     */
    public List<Contact> search(SearchDTO dto);

    /**
     * Updates the information of a contact.
     * @param updated   The new information of a contact.
     * @return  The updated contact.
     * @throws NotFoundException    if no contact is found with the provided id.
     */
    public Contact update(ContactDTO updated) throws NotFoundException;
}
