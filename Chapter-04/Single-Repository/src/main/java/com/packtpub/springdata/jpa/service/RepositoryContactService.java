package com.packtpub.springdata.jpa.service;

import com.packtpub.springdata.jpa.dto.ContactDTO;
import com.packtpub.springdata.jpa.dto.SearchDTO;
import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * This implementation communicates with the data storage by using Spring Data JPA.
 * @author Petri Kainulainen
 */
@Service
public class RepositoryContactService implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryContactService.class);

    @Resource
    private ContactRepository repository;

    @Transactional
    @Override
    public Contact add(ContactDTO added) {
        LOGGER.debug("Adding new contact with information: {}", added);

        Contact contact = Contact.getBuilder(added.getFirstName(), added.getLastName())
                .address(added.getStreetAddress(), added.getPostCode(), added.getPostOffice(), added.getState(), added.getCountry())
                .emailAddress(added.getEmailAddress())
                .phoneNumber(added.getPhoneNumber())
                .build();

        return repository.save(contact);
    }

    @Transactional(readOnly = true)
    @Override
    public long count() {
        LOGGER.debug("Getting contact count");
        return repository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public long count(SearchDTO dto) {
        LOGGER.debug("Getting contact count with search criteria: {}", dto);
        return repository.findContactCount(dto.getSearchTerm());
    }

    @Transactional(rollbackFor = NotFoundException.class)
    @Override
    public Contact deleteById(Long id) throws NotFoundException {
        LOGGER.debug("Deleting contact by id: {}", id);

        Contact deleted = findById(id);
        repository.delete(deleted);

        LOGGER.debug("Deleted contact: {}", deleted);

        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> findAllForPage(int pageIndex, int pageSize) {
        LOGGER.debug("Finding all contacts for page: {} when page size is {}", pageIndex, pageSize);

        return repository.findAllForPage(pageIndex, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Contact findById(Long id) throws NotFoundException {
        LOGGER.debug("Finding contact by id: {}", id);

        Contact found = repository.findOne(id);

        if (found == null) {
            LOGGER.debug("No contact found with id: {}", id);
            throw new NotFoundException("No contact found with id: " + id);
        }

        LOGGER.debug("Found contact: {}", found);

        return found;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> search(SearchDTO dto) {
        LOGGER.debug("Searching contacts with search criteria: {}", dto);

        return repository.findContactsForPage(dto.getSearchTerm(), dto.getPageIndex(), dto.getPageSize());
    }

    @Transactional(rollbackFor = NotFoundException.class)
    @Override
    public Contact update(ContactDTO updated) throws NotFoundException {
        LOGGER.debug("Updating contact with information: {}", updated);

        Contact found = repository.findOne(updated.getId());

        if (found == null) {
            LOGGER.debug("No contact found with id: {}", updated.getId());
            throw new NotFoundException("No contact found with id: " + updated.getId());
        }

        found.update(updated.getFirstName(), updated.getLastName(), updated.getEmailAddress(), updated.getPhoneNumber());
        found.updateAddress(updated.getStreetAddress(), updated.getPostCode(), updated.getPostOffice(), updated.getState(), updated.getCountry());

        return found;
    }
}
