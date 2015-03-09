package com.packtpub.springdata.redis.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author Petri Kainulainen
 */
public class ContactMessageListener implements MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(ContactMessageListener.class);

    private RedisSerializer<String> stringSerializer = new StringRedisSerializer();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        LOGGER.debug("Received message: {} on channel: {}",
                stringSerializer.deserialize(message.getBody()),
                stringSerializer.deserialize(message.getChannel()));
    }
}
