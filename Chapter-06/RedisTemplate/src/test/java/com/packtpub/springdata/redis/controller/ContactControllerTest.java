package com.packtpub.springdata.redis.controller;

import com.packtpub.springdata.redis.config.TestContext;
import com.packtpub.springdata.redis.model.Address;
import com.packtpub.springdata.redis.model.Contact;
import com.packtpub.springdata.redis.model.ContactTestUtil;
import com.packtpub.springdata.redis.service.ContactService;
import com.packtpub.springdata.redis.service.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
public class ContactControllerTest {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String FEEDBACK_MESSAGE = "feedbackMessage";

    private static final String FIELD_NAME_COUNTRY = "address.country";
    private static final String FIELD_NAME_EMAIL_ADDRESS = "emailAddress";
    private static final String FIELD_NAME_FIRST_NAME = "firstName";
    private static final String FIELD_NAME_LAST_NAME = "lastName";
    private static final String FIELD_NAME_PHONE_NUMBER = "phoneNumber";
    private static final String FIELD_NAME_POST_CODE = "address.postCode";
    private static final String FIELD_NAME_POST_OFFICE = "address.postOffice";
    private static final String FIELD_NAME_STATE = "address.state";
    private static final String FIELD_NAME_STREET_ADDRESS = "address.streetAddress";

    private static final long CONTACT_COUNT = 4;

    private static final Long ID = Long.valueOf(3);

    private static final String FIRST_NAME_PREFIX = "firstName";
    private static final String LAST_NAME_PREFIX = "lastName";

    private static final String INVALID_EMAIL_ADDRESS = "invalid";

    private static final int PAGE_INDEX = 0;
    private static final int PAGE_SIZE = 10;

    private static final String SEARCH_TERM = "foo";

    private ContactController controller;

    private MessageSource messageSourceMock;

    private ContactService serviceMock;

    @Resource
    private Validator validator;

    @Before
    public void setUp() {
        controller = new ContactController();

        messageSourceMock = mock(MessageSource.class);
        ReflectionTestUtils.setField(controller, "messageSource", messageSourceMock);

        serviceMock = mock(ContactService.class);
        ReflectionTestUtils.setField(controller, "service", serviceMock);
    }

    @Test
    public void addContact() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = ContactTestUtil.createModel();

        Contact model = ContactTestUtil.createModel(ID);
        when(serviceMock.add(formObject)).thenReturn(model);

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_ADDED);

        String view = controller.addContact(formObject, result, attributes);

        verify(serviceMock, times(1)).add(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = createExpectedRedirectViewPath(ContactController.REQUEST_MAPPING_VIEW_CONTACT);

        assertEquals(expectedView, view);

        Long actualId = Long.valueOf((String) attributes.asMap().get(ContactController.PARAMETER_CONTACT_ID));
        assertEquals(model.getId(), actualId);

        assertFeedbackMessage(attributes, ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_ADDED);
    }

    @Test
    public void addEmptyContact() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = new Contact();
        ReflectionTestUtils.setField(formObject, "firstName", "");
        ReflectionTestUtils.setField(formObject, "lastName", "");

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.addContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.ADD_CONTACT_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_FIRST_NAME, FIELD_NAME_LAST_NAME);
    }

    @Test
    public void addContactWithInvalidEmailAddress() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = ContactTestUtil.createModel();
        ReflectionTestUtils.setField(formObject, "emailAddress", INVALID_EMAIL_ADDRESS);

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.addContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.ADD_CONTACT_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_EMAIL_ADDRESS);

    }

    @Test
    public void addContactWithTooLongFieldValues() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = ContactTestUtil.createModelWithLooLongFields();

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.addContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.ADD_CONTACT_VIEW, view);
        assertFieldErrors(result,
                FIELD_NAME_COUNTRY,
                FIELD_NAME_EMAIL_ADDRESS,
                FIELD_NAME_FIRST_NAME,
                FIELD_NAME_LAST_NAME,
                FIELD_NAME_PHONE_NUMBER,
                FIELD_NAME_POST_CODE,
                FIELD_NAME_POST_OFFICE,
                FIELD_NAME_STATE,
                FIELD_NAME_STREET_ADDRESS);
    }

    @Test
    public void deleteContact() throws NotFoundException {
        Contact deleted = ContactTestUtil.createModel(ID);
        when(serviceMock.deleteById(ID)).thenReturn(deleted);

        initMessageSourceForFeedbackMessage(ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_DELETED);

        String feedbackMessage = controller.deleteContact(ID);

        verify(serviceMock, times(1)).deleteById(ID);
        verifyNoMoreInteractions(serviceMock);

        verify(messageSourceMock, times(1)).getMessage(eq(ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_DELETED), any(Object[].class), any(Locale.class));
        verifyNoMoreInteractions(messageSourceMock);

        assertEquals(FEEDBACK_MESSAGE, feedbackMessage);
    }

    @Test(expected = NotFoundException.class)
    public void deleteContactWhenContactIsNotFound() throws NotFoundException {
        when(serviceMock.deleteById(ID)).thenThrow(new NotFoundException(""));

        controller.deleteContact(ID);

        verify(serviceMock, times(1)).deleteById(ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void showAddContactPage() {
        Model model = new BindingAwareModelMap();

        String view = controller.showAddContactPage(model);

        assertEquals(ContactController.ADD_CONTACT_VIEW, view);
        Contact formObject = (Contact) model.asMap().get(ContactController.MODEL_ATTRIBUTE_CONTACT);
        assertNotNull(formObject);

        assertNull(formObject.getId());
        assertNull(formObject.getFirstName());
        assertNull(formObject.getLastName());
        assertNull(formObject.getEmailAddress());
        assertNull(formObject.getPhoneNumber());

        Address address = new Address();

        assertNull(address.getStreetAddress());
        assertNull(address.getPostCode());
        assertNull(address.getPostOffice());
        assertNull(address.getState());
        assertNull(address.getCountry());
    }

    @Test
    public void showContactPage() throws NotFoundException {
        Contact contact = ContactTestUtil.createModel(ID);
        when(serviceMock.findById(ID)).thenReturn(contact);

        Model model = new BindingAwareModelMap();
        String view = controller.showContactPage(ID, model);

        verify(serviceMock, times(1)).findById(ID);
        verifyNoMoreInteractions(serviceMock);

        assertEquals(ContactController.CONTACT_VIEW, view);
        assertEquals(contact, model.asMap().get(ContactController.MODEL_ATTRIBUTE_CONTACT));
    }

    @Test(expected = NotFoundException.class)
    public void showContactPageWhenContactIsNotFound() throws NotFoundException {
        when(serviceMock.findById(ID)).thenThrow(new NotFoundException(""));

        Model model = new BindingAwareModelMap();
        controller.showContactPage(ID, model);

        verify(serviceMock, times(1)).findById(ID);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void showHomePage() {
        List<Contact> contacts = new ArrayList<Contact>();
        when(serviceMock.findAll()).thenReturn(contacts);

        Model model = new BindingAwareModelMap();
        String view = controller.showHomePage(model);

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);

        assertEquals(ContactController.HOME_VIEW, view);
        assertEquals(contacts, model.asMap().get(ContactController.MODEL_ATTRIBUTE_CONTACTS));
    }

    @Test
    public void showUpdateContactPage() throws NotFoundException {
        Contact edited = ContactTestUtil.createModel(ID);
        when(serviceMock.findById(ID)).thenReturn(edited);

        Model model = new BindingAwareModelMap();

        String view = controller.showUpdateContactPage(ID, model);

        verify(serviceMock, times(1)).findById(ID);
        verifyNoMoreInteractions(serviceMock);

        assertEquals(ContactController.UPDATE_CONTACT_VIEW, view);

        Contact formObject = (Contact) model.asMap().get(ContactController.MODEL_ATTRIBUTE_CONTACT);
        assertEquals(edited, formObject);
    }

    @Test
    public void showUpdateContactPageWhenAddressIsNull() throws NotFoundException {
        Contact updated = ContactTestUtil.createModel(ID);
        ReflectionTestUtils.setField(updated, "address", null);

        when(serviceMock.findById(ID)).thenReturn(updated);

        Model model = new BindingAwareModelMap();

        String view = controller.showUpdateContactPage(ID, model);

        verify(serviceMock, times(1)).findById(ID);
        verifyNoMoreInteractions(serviceMock);

        assertEquals(ContactController.UPDATE_CONTACT_VIEW, view);

        Contact formObject = (Contact) model.asMap().get(ContactController.MODEL_ATTRIBUTE_CONTACT);
        assertEquals(updated, formObject);
    }

    @Test(expected = NotFoundException.class)
    public void showUpdateContactPageWhenContactIsNotFound() throws NotFoundException {
        when(serviceMock.findById(ID)).thenThrow(new NotFoundException(""));

        Model model = new BindingAwareModelMap();
        controller.showUpdateContactPage(ID, model);

        verify(serviceMock, times(1)).findById(ID);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateContact() throws NotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/update");

        Contact formObject = ContactTestUtil.createModel(ID);
        when(serviceMock.update(formObject)).thenReturn(formObject);

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_UPDATED);

        String view = controller.updateContact(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = createExpectedRedirectViewPath(ContactController.REQUEST_MAPPING_VIEW_CONTACT);

        assertEquals(expectedView, view);

        Long actualId = Long.valueOf((String) attributes.asMap().get(ContactController.PARAMETER_CONTACT_ID));
        assertEquals(formObject.getId(), actualId);

        assertFeedbackMessage(attributes, ContactController.FEEDBACK_MESSAGE_KEY_CONTACT_UPDATED);
    }

    @Test(expected = NotFoundException.class)
    public void updateContactWhenContactIsNotFound() throws NotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/update");

        Contact formObject = ContactTestUtil.createModel(ID);
        when(serviceMock.update(formObject)).thenThrow(new NotFoundException(""));

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        controller.updateContact(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateEmptyContact() throws NotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = new Contact();
        ReflectionTestUtils.setField(formObject, "firstName", "");
        ReflectionTestUtils.setField(formObject, "lastName", "");

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.updateContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.UPDATE_CONTACT_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_FIRST_NAME, FIELD_NAME_LAST_NAME);
    }

    @Test
    public void updateContactWithInvalidEmailAddress() throws NotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = ContactTestUtil.createModel();
        ReflectionTestUtils.setField(formObject, "emailAddress", INVALID_EMAIL_ADDRESS);

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.updateContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.UPDATE_CONTACT_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_EMAIL_ADDRESS);

    }

    @Test
    public void updateContactWithTooLongFieldValues() throws NotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/contact/add");
        Contact formObject = ContactTestUtil.createModelWithLooLongFields();

        BindingResult result = bindAndValidate(mockRequest, formObject);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.updateContact(formObject, result, attributes);

        verifyZeroInteractions(serviceMock);
        assertEquals(ContactController.UPDATE_CONTACT_VIEW, view);
        assertFieldErrors(result,
                FIELD_NAME_COUNTRY,
                FIELD_NAME_EMAIL_ADDRESS,
                FIELD_NAME_FIRST_NAME,
                FIELD_NAME_LAST_NAME,
                FIELD_NAME_PHONE_NUMBER,
                FIELD_NAME_POST_CODE,
                FIELD_NAME_POST_OFFICE,
                FIELD_NAME_STATE,
                FIELD_NAME_STREET_ADDRESS);
    }

    private void assertFeedbackMessage(RedirectAttributes model, String messageCode) {
        assertFlashMessages(model, messageCode, ContactController.FLASH_MESSAGE_KEY_FEEDBACK);
    }

    private void assertFlashMessages(RedirectAttributes model, String messageCode, String flashMessageParameterName) {
        Map<String, ?> flashMessages = model.getFlashAttributes();
        Object message = flashMessages.get(flashMessageParameterName);
        assertNotNull(message);
        flashMessages.remove(message);
        assertTrue(flashMessages.isEmpty());

        verify(messageSourceMock, times(1)).getMessage(eq(messageCode), any(Object[].class), any(Locale.class));
        verifyNoMoreInteractions(messageSourceMock);
    }

    private void assertFieldErrors(BindingResult result, String... fieldNames) {
        assertEquals(fieldNames.length, result.getFieldErrorCount());
        for (String fieldName : fieldNames) {
            assertNotNull(result.getFieldError(fieldName));
        }
    }

    private BindingResult bindAndValidate(HttpServletRequest request, Object formObject) {
        WebDataBinder binder = new WebDataBinder(formObject);
        binder.setValidator(validator);
        binder.bind(new MutablePropertyValues(request.getParameterMap()));
        binder.getValidator().validate(binder.getTarget(), binder.getBindingResult());
        return binder.getBindingResult();
    }

    private String createExpectedRedirectViewPath(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append("redirect:");
        builder.append(path);
        return builder.toString();
    }

    private void initMessageSourceForFeedbackMessage(String feedbackMessageCode) {
        when(messageSourceMock.getMessage(eq(feedbackMessageCode), any(Object[].class), any(Locale.class))).thenReturn(FEEDBACK_MESSAGE);
    }
}
