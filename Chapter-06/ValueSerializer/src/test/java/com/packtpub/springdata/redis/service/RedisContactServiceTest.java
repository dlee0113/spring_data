package com.packtpub.springdata.redis.service;

import com.packtpub.springdata.redis.model.Contact;
import com.packtpub.springdata.redis.model.ContactTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RedisContactServiceTest {

    private Long CONTACT_ID = Long.valueOf(1);
    private String CONTACT_KEY = "contact1";

    private RedisContactService service;

    private RedisAtomicLong contactIdCounterMock;

    private RedisTemplate<String, Contact> redisTemplateMock;

    private SetOperations setOperationsMock;

    private ValueOperations valueOperationsMock;

    @Before
    public void setUp() {
        service = new RedisContactService();

        contactIdCounterMock = mock(RedisAtomicLong.class);
        ReflectionTestUtils.setField(service, "contactIdCounter", contactIdCounterMock);

        redisTemplateMock = mock(RedisTemplate.class);
        ReflectionTestUtils.setField(service, "redisTemplate", redisTemplateMock);

        setOperationsMock = mock(SetOperations.class);
        valueOperationsMock = mock(ValueOperations.class);
    }

    @Test
    public void add() {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(contactIdCounterMock.incrementAndGet()).thenReturn(CONTACT_ID);

        Contact added = ContactTestUtil.createModel();
        Contact actual = service.add(added);

        verify(redisTemplateMock, times(1)).opsForValue();
        verify(redisTemplateMock, times(1)).opsForSet();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(contactIdCounterMock, times(1)).incrementAndGet();
        verifyNoMoreInteractions(contactIdCounterMock);

        verify(valueOperationsMock, times(1)).set(CONTACT_KEY, added);
        verifyNoMoreInteractions(valueOperationsMock);

        verify(setOperationsMock, times(1)).add(RedisContactService.KEY_CONTACT_SET, added);
        verifyNoMoreInteractions(setOperationsMock);

        assertEquals(CONTACT_ID, actual.getId());
        assertEquals(added, actual);
    }

    @Test
    public void deleteById() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);

        Contact deleted = ContactTestUtil.createModel(CONTACT_ID);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(deleted);

        Contact actual = service.deleteById(CONTACT_ID);

        verify(redisTemplateMock, times(2)).opsForValue();
        verify(redisTemplateMock, times(1)).opsForSet();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verify(valueOperationsMock, times(1)).set(CONTACT_KEY, null);
        verifyNoMoreInteractions(valueOperationsMock);

        verify(setOperationsMock, times(1)).remove(RedisContactService.KEY_CONTACT_SET, deleted);
        verifyNoMoreInteractions(setOperationsMock);

        verifyZeroInteractions(contactIdCounterMock);

        assertEquals(deleted, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(null);

        service.deleteById(CONTACT_ID);

        verify(redisTemplateMock, times(1)).opsForValue();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verifyNoMoreInteractions(valueOperationsMock);

        verifyZeroInteractions(contactIdCounterMock, setOperationsMock);
    }

    @Test
    public void findAll() {
        Set<Contact> found = new HashSet<Contact>();
        found.add(ContactTestUtil.createModel(CONTACT_ID));

        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.members(RedisContactService.KEY_CONTACT_SET)).thenReturn(found);

        List<Contact> actual = service.findAll();

        verify(redisTemplateMock, times(1)).opsForSet();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(setOperationsMock, times(1)).members(RedisContactService.KEY_CONTACT_SET);
        verifyNoMoreInteractions(setOperationsMock);

        verifyZeroInteractions(contactIdCounterMock, valueOperationsMock);

        assertEquals(actual.size(), found.size());

        for (Contact contact: found) {
            assertTrue(actual.remove(contact));
        }

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findById() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);

        Contact found = ContactTestUtil.createModel(CONTACT_ID);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(found);

        Contact actual = service.findById(CONTACT_ID);

        verify(redisTemplateMock, times(1)).opsForValue();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verifyNoMoreInteractions(valueOperationsMock);

        verifyZeroInteractions(contactIdCounterMock, setOperationsMock);

        assertEquals(found, actual);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(null);

        service.findById(CONTACT_ID);

        verify(redisTemplateMock, times(1)).opsForValue();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verifyNoMoreInteractions(valueOperationsMock);

        verifyZeroInteractions(contactIdCounterMock, setOperationsMock);
    }

    @Test
    public void update() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);

        Contact found = ContactTestUtil.createModel(CONTACT_ID);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(found);

        Contact updated = ContactTestUtil.createModel(CONTACT_ID);
        Contact actual = service.update(updated);

        verify(redisTemplateMock, times(2)).opsForValue();
        verify(redisTemplateMock, times(2)).opsForSet();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verify(valueOperationsMock, times(1)).set(CONTACT_KEY, actual);
        verifyNoMoreInteractions(valueOperationsMock);

        verify(setOperationsMock, times(1)).remove(RedisContactService.KEY_CONTACT_SET, found);
        verify(setOperationsMock, times(1)).add(RedisContactService.KEY_CONTACT_SET, updated);
        verifyNoMoreInteractions(setOperationsMock);

        verifyZeroInteractions(contactIdCounterMock);

        assertEquals(updated, actual);
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(CONTACT_KEY)).thenReturn(null);

        Contact updated = ContactTestUtil.createModel(CONTACT_ID);
        service.update(updated);

        verify(redisTemplateMock, times(1)).opsForValue();
        verifyNoMoreInteractions(redisTemplateMock);

        verify(valueOperationsMock, times(1)).get(CONTACT_KEY);
        verifyNoMoreInteractions(valueOperationsMock);

        verifyZeroInteractions(contactIdCounterMock, setOperationsMock);
    }
}
