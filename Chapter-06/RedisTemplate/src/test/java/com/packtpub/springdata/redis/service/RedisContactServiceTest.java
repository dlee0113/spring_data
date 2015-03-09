package com.packtpub.springdata.redis.service;

import com.packtpub.springdata.redis.model.Address;
import com.packtpub.springdata.redis.model.Contact;
import com.packtpub.springdata.redis.model.ContactTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RedisContactServiceTest {

    private Long CONTACT_ID = Long.valueOf(1);
    private String CONTACT_KEY = "contact1";

    private RedisContactService service;

    private RedisAtomicLong contactIdCounterMock;

    private RedisTemplate<String, String> redisTemplateMock;

    private BoundHashOperations boundHashOperationsMock;

    private SetOperations setOperationsMock;

    @Before
    public void setUp() {
        service = new RedisContactService();

        contactIdCounterMock = mock(RedisAtomicLong.class);
        ReflectionTestUtils.setField(service, "contactIdCounter", contactIdCounterMock);

        redisTemplateMock = mock(RedisTemplate.class);
        ReflectionTestUtils.setField(service, "redisTemplate", redisTemplateMock);

        boundHashOperationsMock = mock(BoundHashOperations.class);
        setOperationsMock = mock(SetOperations.class);
    }

    @Test
    public void add() {
        when(contactIdCounterMock.incrementAndGet()).thenReturn(CONTACT_ID);
        when(redisTemplateMock.boundHashOps(CONTACT_KEY)).thenReturn(boundHashOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);

        Contact added = ContactTestUtil.createModel();
        Contact actual = service.add(added);

        verify(contactIdCounterMock, times(1)).incrementAndGet();
        verifyNoMoreInteractions(contactIdCounterMock);

        verify(redisTemplateMock, times(1)).boundHashOps(CONTACT_KEY);
        verifyThatContactWasPersisted(CONTACT_ID, added);

        verify(redisTemplateMock, times(1)).opsForSet();
        verify(setOperationsMock, times(1)).add(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY);
        verifyNoMoreInteractions(boundHashOperationsMock, setOperationsMock, redisTemplateMock);

        assertEquals(added, actual);
    }

    @Test
    public void deleteById() throws NotFoundException {
        when(redisTemplateMock.boundHashOps(CONTACT_KEY)).thenReturn(boundHashOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(true);
        initGetHashOperationsForContact(CONTACT_ID);

        Contact deleted = service.deleteById(CONTACT_ID);

        verify(redisTemplateMock, times(2)).boundHashOps(CONTACT_KEY);

        verifyThatContactWasGet();
        verifyThatContactWasDeleted();

        verify(redisTemplateMock, times(2)).opsForSet();
        verify(setOperationsMock, times(1)).isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY);
        verify(setOperationsMock,times(1)).remove(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY);
        verifyNoMoreInteractions(setOperationsMock);

        verifyNoMoreInteractions(boundHashOperationsMock, redisTemplateMock);
        verifyZeroInteractions(contactIdCounterMock);

        assertContact(CONTACT_ID, deleted);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(false);

        service.deleteById(CONTACT_ID);

        verifyThatExistCheckForContactIsDone(CONTACT_KEY);
        verifyNoMoreInteractions(redisTemplateMock, setOperationsMock);

        verifyZeroInteractions(boundHashOperationsMock, contactIdCounterMock);
    }

    @Test
    public void findAll() {
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.members(RedisContactService.KEY_CONTACT_SET)).thenReturn(createKeySet(CONTACT_KEY));
        when(redisTemplateMock.boundHashOps(CONTACT_KEY)).thenReturn(boundHashOperationsMock);
        initGetHashOperationsForContact(CONTACT_ID);

        List<Contact> contacts = service.findAll();

        verify(redisTemplateMock, times(1)).opsForSet();
        verify(redisTemplateMock, times(1)).boundHashOps(CONTACT_KEY);
        verify(setOperationsMock, times(1)).members(RedisContactService.KEY_CONTACT_SET);
        verifyNoMoreInteractions(setOperationsMock);

        verifyThatContactWasGet();
        verifyNoMoreInteractions(boundHashOperationsMock, redisTemplateMock);

        verifyZeroInteractions(contactIdCounterMock);

        assertEquals(1, contacts.size());
        Contact actual = contacts.get(0);
        assertContact(CONTACT_ID, actual);
    }

    private Set<String> createKeySet(String... keys) {
        Set<String> keySet = new HashSet<String>();

        for (String key: keys) {
            keySet.add(key);
        }

        return keySet;
    }

    @Test
    public void findById() throws NotFoundException {
        when(redisTemplateMock.boundHashOps(CONTACT_KEY)).thenReturn(boundHashOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(true);
        initGetHashOperationsForContact(CONTACT_ID);

        Contact found = service.findById(CONTACT_ID);

        verify(redisTemplateMock, times(1)).boundHashOps(CONTACT_KEY);

        verifyThatExistCheckForContactIsDone(CONTACT_KEY);
        verifyThatContactWasGet();

        verifyNoMoreInteractions(boundHashOperationsMock, redisTemplateMock,setOperationsMock);
        verifyZeroInteractions(contactIdCounterMock);

        assertContact(CONTACT_ID, found);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(false);

        service.findById(CONTACT_ID);

        verifyThatExistCheckForContactIsDone(CONTACT_KEY);

        verifyNoMoreInteractions(redisTemplateMock, setOperationsMock);
        verifyZeroInteractions(boundHashOperationsMock, contactIdCounterMock);
    }

    @Test
    public void update() throws NotFoundException {
        when(redisTemplateMock.boundHashOps(CONTACT_KEY)).thenReturn(boundHashOperationsMock);
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(true);

        Contact updated = ContactTestUtil.createModel(CONTACT_ID);
        service.update(updated);

        verify(redisTemplateMock, times(1)).boundHashOps(CONTACT_KEY);
        verifyThatContactWasPersisted(CONTACT_ID, updated);

        verifyThatExistCheckForContactIsDone(CONTACT_KEY);

        verifyNoMoreInteractions(boundHashOperationsMock, redisTemplateMock);
        verifyZeroInteractions(contactIdCounterMock, setOperationsMock);
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenContactIsNotFound() throws NotFoundException {
        when(redisTemplateMock.opsForSet()).thenReturn(setOperationsMock);
        when(setOperationsMock.isMember(RedisContactService.KEY_CONTACT_SET, CONTACT_KEY)).thenReturn(false);

        Contact updated = ContactTestUtil.createModel(CONTACT_ID);
        service.update(updated);

        verifyThatExistCheckForContactIsDone(CONTACT_KEY);

        verifyNoMoreInteractions(redisTemplateMock, setOperationsMock);
        verifyZeroInteractions(boundHashOperationsMock, contactIdCounterMock);
    }

    private void assertContact(Long expectedId, Contact actual) {
        assertEquals(expectedId, actual.getId());
        assertEquals(ContactTestUtil.EMAIL_ADDRESS, actual.getEmailAddress());
        assertEquals(ContactTestUtil.FIRST_NAME, actual.getFirstName());
        assertEquals(ContactTestUtil.LAST_NAME, actual.getLastName());
        assertEquals(ContactTestUtil.PHONE_NUMBER, actual.getPhoneNumber());

        Address actualAddress = actual.getAddress();

        assertEquals(ContactTestUtil.STREET_ADDRESS, actualAddress.getStreetAddress());
        assertEquals(ContactTestUtil.POST_CODE, actualAddress.getPostCode());
        assertEquals(ContactTestUtil.POST_OFFICE, actualAddress.getPostOffice());
        assertEquals(ContactTestUtil.STATE, actualAddress.getState());
        assertEquals(ContactTestUtil.COUNTRY, actualAddress.getCountry());
    }

    private void initGetHashOperationsForContact(Long id) {
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_CONTACT_ID)).thenReturn(id);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_CONTACT_EMAIL_ADDRESS)).thenReturn(ContactTestUtil.EMAIL_ADDRESS);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_CONTACT_FIRST_NAME)).thenReturn(ContactTestUtil.FIRST_NAME);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_CONTACT_LAST_NAME)).thenReturn(ContactTestUtil.LAST_NAME);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_CONTACT_PHONE_NUMBER)).thenReturn(ContactTestUtil.PHONE_NUMBER);

        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_ADDRESS_STREET_ADDRESS)).thenReturn(ContactTestUtil.STREET_ADDRESS);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_ADDRESS_POST_CODE)).thenReturn(ContactTestUtil.POST_CODE);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_ADDRESS_POST_OFFICE)).thenReturn(ContactTestUtil.POST_OFFICE);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_ADDRESS_STATE)).thenReturn(ContactTestUtil.STATE);
        when(boundHashOperationsMock.get(RedisContactService.HASH_KEY_ADDRESS_COUNTRY)).thenReturn(ContactTestUtil.COUNTRY);

    }

    private void verifyThatContactWasDeleted() {
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_CONTACT_ID);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_CONTACT_EMAIL_ADDRESS);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_CONTACT_FIRST_NAME);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_CONTACT_LAST_NAME);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_CONTACT_PHONE_NUMBER);

        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_ADDRESS_STREET_ADDRESS);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_ADDRESS_POST_CODE);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_ADDRESS_POST_OFFICE);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_ADDRESS_STATE);
        verify(boundHashOperationsMock, times(1)).delete(RedisContactService.HASH_KEY_ADDRESS_COUNTRY);
    }

    private void verifyThatContactWasGet() {
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_CONTACT_ID);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_CONTACT_EMAIL_ADDRESS);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_CONTACT_FIRST_NAME);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_CONTACT_LAST_NAME);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_CONTACT_PHONE_NUMBER);

        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_ADDRESS_STREET_ADDRESS);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_ADDRESS_POST_CODE);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_ADDRESS_POST_OFFICE);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_ADDRESS_STATE);
        verify(boundHashOperationsMock, times(1)).get(RedisContactService.HASH_KEY_ADDRESS_COUNTRY);
    }

    private void verifyThatExistCheckForContactIsDone(String key) {
        verify(redisTemplateMock, times(1)).opsForSet();
        verify(setOperationsMock, times(1)).isMember(RedisContactService.KEY_CONTACT_SET, key);
    }

    private void verifyThatContactWasPersisted(Long id, Contact added) {
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_CONTACT_ID, id);
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_CONTACT_EMAIL_ADDRESS, added.getEmailAddress());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_CONTACT_FIRST_NAME, added.getFirstName());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_CONTACT_LAST_NAME, added.getLastName());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_CONTACT_PHONE_NUMBER, added.getPhoneNumber());

        Address address = added.getAddress();

        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_ADDRESS_STREET_ADDRESS, address.getStreetAddress());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_ADDRESS_POST_CODE, address.getPostCode());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_ADDRESS_POST_OFFICE, address.getPostOffice());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_ADDRESS_STATE, address.getState());
        verify(boundHashOperationsMock, times(1)).put(RedisContactService.HASH_KEY_ADDRESS_COUNTRY, address.getCountry());
    }
}
