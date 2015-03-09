package com.packtpub.springdata.redis.service;

import com.packtpub.springdata.redis.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Resource
    private RedisAtomicLong contactIdCounter;

    @Resource
    private RedisTemplate<String, Contact> redisTemplate;

    @Override
    public Contact add(Contact added) {
        LOGGER.debug("Adding contact with information: {}", added);

        persist(added);
        redisTemplate.opsForSet().add(KEY_CONTACT_SET, added);

        return added;
    }

    @Override
    public Contact deleteById(Long id) throws NotFoundException {
        LOGGER.debug("Finding contact by id: {}", id);

        Contact deleted = findById(id);
        LOGGER.debug("Deleting the information of contact: {}", deleted);

        String key = buildKey(id);
        LOGGER.debug("Using key: {}", key);

        redisTemplate.opsForSet().remove(KEY_CONTACT_SET, deleted);
        redisTemplate.opsForValue().set(key, null);

        return deleted;
    }

    @Override
    public List<Contact> findAll() {
        LOGGER.debug("Finding all contacts");

        Collection<Contact> contacts = redisTemplate.opsForSet().members(KEY_CONTACT_SET);
        LOGGER.debug("Returning {} contacts", contacts.size());

        return new ArrayList<Contact>(contacts);
    }

    @Override
    public Contact findById(Long id) throws NotFoundException {
        LOGGER.debug("Finding contact by id: {}", id);

        String key = buildKey(id);
        LOGGER.debug("Finding contact by key: {}", key);

        Contact found = redisTemplate.opsForValue().get(key);

        if (found == null) {
            LOGGER.debug("No contact found with id: {}", id);
            throw new NotFoundException("No contact found with id: {}" + id);
        }

        return found;
    }

    @Override
    public Contact update(Contact updated) throws NotFoundException {
        LOGGER.debug("Updating contact with information: {}", updated);

        Contact old = findById(updated.getId());

        persist(updated);

        redisTemplate.opsForSet().remove(KEY_CONTACT_SET, old);
        redisTemplate.opsForSet().add(KEY_CONTACT_SET, updated);

        return updated;
    }

    private void persist(Contact persisted) {
        LOGGER.debug("Persisting contact: {}", persisted);

        Long id = persisted.getId();
        if (id == null) {
            LOGGER.debug("No id found for the persisted contact.");
            id = contactIdCounter.incrementAndGet();
            persisted.setId(id);
            LOGGER.debug("The id of the persisted contact is {}", id);
        }

        String key = buildKey(persisted.getId());

        redisTemplate.opsForValue().set(key, persisted);
    }

    private String buildKey(Long contactId) {
        return KEY_CONTACT_PREFIX + contactId;
    }
}
