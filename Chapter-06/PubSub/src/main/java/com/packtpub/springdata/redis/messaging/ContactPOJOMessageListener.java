package com.packtpub.springdata.redis.messaging;

import com.packtpub.springdata.redis.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petri Kainulainen
 */
public class ContactPOJOMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactPOJOMessageListener.class);

    public void handleMessage(Contact contact, String channel) {
        LOGGER.debug("Received contact: {} on channel: {}", contact, channel);
    }
}
