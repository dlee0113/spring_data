package com.packtpub.springdata.redis.service;

import com.packtpub.springdata.redis.model.Address;
import com.packtpub.springdata.redis.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This implementation communicates with Redis by using the RedisTemplate class.
 * @author Petri Kainulainen
 */
@Service
public class RedisContactService implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisContactService.class);

    private static final String KEY_CONTACT_PREFIX = "contact";
    protected static final String KEY_CONTACT_SET = "contacts";

    protected static final String HASH_KEY_ADDRESS_COUNTRY = "country";
    protected static final String HASH_KEY_ADDRESS_STREET_ADDRESS = "streetAddress";
    protected static final String HASH_KEY_ADDRESS_POST_CODE = "postCode";
    protected static final String HASH_KEY_ADDRESS_POST_OFFICE = "postOffice";
    protected static final String HASH_KEY_ADDRESS_STATE = "state";

    protected static final String HASH_KEY_CONTACT_ID = "id";
    protected static final String HASH_KEY_CONTACT_EMAIL_ADDRESS = "emailAddress";
    protected static final String HASH_KEY_CONTACT_FIRST_NAME = "firstName";
    protected static final String HASH_KEY_CONTACT_LAST_NAME = "lastName";
    protected static final String HASH_KEY_CONTACT_PHONE_NUMBER = "phoneNumber";


    @Resource
    private RedisAtomicLong contactIdCounter;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Contact add(Contact added) {
        LOGGER.debug("Adding contact with information: {}", added);

        String key = persist(added);

        redisTemplate.opsForSet().add(KEY_CONTACT_SET, key);

        return added;
    }

    @Override
    public Contact deleteById(Long id) throws NotFoundException {
        LOGGER.debug("Finding contact by id: {}", id);

        Contact deleted = findById(id);
        LOGGER.debug("Deleting the information of contact: {}", deleted);

        String key = buildKey(id);
        LOGGER.debug("Using key: {}", key);

        redisTemplate.opsForSet().remove(KEY_CONTACT_SET, key);

        BoundHashOperations operations = redisTemplate.boundHashOps(key);

        operations.delete(HASH_KEY_CONTACT_ID);
        operations.delete(HASH_KEY_CONTACT_EMAIL_ADDRESS);
        operations.delete(HASH_KEY_CONTACT_FIRST_NAME);
        operations.delete(HASH_KEY_CONTACT_LAST_NAME);
        operations.delete(HASH_KEY_CONTACT_PHONE_NUMBER);

        operations.delete(HASH_KEY_ADDRESS_STREET_ADDRESS);
        operations.delete(HASH_KEY_ADDRESS_POST_CODE);
        operations.delete(HASH_KEY_ADDRESS_POST_OFFICE);
        operations.delete(HASH_KEY_ADDRESS_STATE);
        operations.delete(HASH_KEY_ADDRESS_COUNTRY);

        return deleted;
    }

    @Override
    public List<Contact> findAll() {
        LOGGER.debug("Finding all contacts");
        List<Contact> contacts = new ArrayList<Contact>();

        Collection<String> keys = redisTemplate.opsForSet().members(KEY_CONTACT_SET);

        for (String key: keys) {
            LOGGER.debug("Finding contact with key: {}", key);

            Contact contact = buildContact(key);
            contacts.add(contact);
        }

        LOGGER.debug("Returning {} contacts", contacts.size());

        return contacts;
    }

    @Override
    public Contact findById(Long id) throws NotFoundException {
        LOGGER.debug("Finding contact by id: {}", id);

        if (contactDoesNotExist(id)) {
            LOGGER.debug("Contact was not found with id: {}", id);
            throw new NotFoundException("No contact found with id: " + id);
        }

        return buildContact(id);
    }

    @Override
    public Contact update(Contact updated) throws NotFoundException {
        LOGGER.debug("Updating contact with information: {}", updated);

        if (contactDoesNotExist(updated.getId())) {
            LOGGER.debug("Contact was not found with id: {)", updated.getId());
            throw new NotFoundException("No contact found with id: " + updated.getId());
        }

        persist(updated);

        return updated;
    }

    private boolean contactDoesNotExist(Long id) {
        LOGGER.debug("Checking if contact is not found with id: {}", id);

        String key = buildKey(id);

        return !redisTemplate.opsForSet().isMember(KEY_CONTACT_SET, key);
    }

    private String buildKey(Long contactId) {
        return KEY_CONTACT_PREFIX + contactId;
    }

    private Contact buildContact(Long id) {
        LOGGER.debug("Building contact with id: {}", id);

        String key = buildKey(id);

        return buildContact(key);
    }

    private Contact buildContact(String key) {
        LOGGER.debug("Building contact with key: {}", key);

        Contact contact = new Contact();

        BoundHashOperations operations = redisTemplate.boundHashOps(key);

        contact.setId((Long) operations.get(HASH_KEY_CONTACT_ID));
        contact.setEmailAddress((String) operations.get(HASH_KEY_CONTACT_EMAIL_ADDRESS));
        contact.setFirstName((String) operations.get(HASH_KEY_CONTACT_FIRST_NAME));
        contact.setLastName((String) operations.get(HASH_KEY_CONTACT_LAST_NAME));
        contact.setPhoneNumber((String) operations.get(HASH_KEY_CONTACT_PHONE_NUMBER));

        Address address = new Address();
        address.setStreetAddress((String) operations.get(HASH_KEY_ADDRESS_STREET_ADDRESS));
        address.setPostCode((String) operations.get(HASH_KEY_ADDRESS_POST_CODE));
        address.setPostOffice((String) operations.get(HASH_KEY_ADDRESS_POST_OFFICE));
        address.setState((String) operations.get(HASH_KEY_ADDRESS_STATE));
        address.setCountry((String) operations.get(HASH_KEY_ADDRESS_COUNTRY));
        contact.setAddress(address);

        LOGGER.debug("Build contact: {}", contact);

        return contact;
    }

    private String persist(Contact persisted) {
        LOGGER.debug("Persisting contact: {}", persisted);

        Long id = persisted.getId();
        if (id == null) {
            LOGGER.debug("No id found for the persisted contact.");
            id = contactIdCounter.incrementAndGet();
            persisted.setId(id);
            LOGGER.debug("The id of the persisted contact is {}", id);
        }

        String contactKey = buildKey(id);

        BoundHashOperations operations = redisTemplate.boundHashOps(contactKey);

        operations.put(HASH_KEY_CONTACT_ID, persisted.getId());
        operations.put(HASH_KEY_CONTACT_EMAIL_ADDRESS, persisted.getEmailAddress());
        operations.put(HASH_KEY_CONTACT_FIRST_NAME, persisted.getFirstName());
        operations.put(HASH_KEY_CONTACT_LAST_NAME, persisted.getLastName());
        operations.put(HASH_KEY_CONTACT_PHONE_NUMBER, persisted.getPhoneNumber());

        Address address = persisted.getAddress();

        operations.put(HASH_KEY_ADDRESS_STREET_ADDRESS, address.getStreetAddress());
        operations.put(HASH_KEY_ADDRESS_POST_CODE, address.getPostCode());
        operations.put(HASH_KEY_ADDRESS_POST_OFFICE, address.getPostOffice());
        operations.put(HASH_KEY_ADDRESS_STATE, address.getState());
        operations.put(HASH_KEY_ADDRESS_COUNTRY, address.getCountry());

        LOGGER.debug("persisted contact: {}", persisted);

        return contactKey;
    }

}
