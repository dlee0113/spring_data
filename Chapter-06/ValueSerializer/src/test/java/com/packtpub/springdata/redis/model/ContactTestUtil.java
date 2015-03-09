package com.packtpub.springdata.redis.model;

/**
 * @author Petri Kainulainen
 */
public class ContactTestUtil {

    private static final String CHARACTER = "a";

    public static final String EMAIL_ADDRESS = "foo.bar@bar.com";
    public static final String EMAIL_SUFFIX = "@bar.com";
    public static final String FIRST_NAME = "Foo";
    public static final String LAST_NAME = "Bar";
    public static final String PHONE_NUMBER = "1234567";

    public static final String STREET_ADDRESS = "streetAddress";
    public static final String POST_CODE = "postCode";
    public static final String POST_OFFICE = "postOffice";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";

    private static String createEmailAddressWithLength(int length) {
        StringBuilder builder = new StringBuilder();

        length = length - EMAIL_SUFFIX.length();

        for (int index = 0; index < length; index++) {
            builder.append(CHARACTER);
        }

        builder.append(EMAIL_SUFFIX);

        return builder.toString();
    }

    private static final String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < length; index++) {
            builder.append(CHARACTER);
        }

        return builder.toString();
    }

    public static Contact createModel() {
        return createModel(null);
    }

    public static Contact createModel(Long id) {
        return createModel(id, FIRST_NAME, LAST_NAME);
    }

    public static Contact createModel(Long id, String firstName, String lastName) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setEmailAddress(EMAIL_ADDRESS);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setPhoneNumber(PHONE_NUMBER);

        Address address = new Address();
        address.setStreetAddress(STREET_ADDRESS);
        address.setPostCode(POST_CODE);
        address.setPostOffice(POST_OFFICE);
        address.setState(STATE);
        address.setCountry(COUNTRY);

        contact.setAddress(address);

        return contact;
    }

    public static Contact createModelWithLooLongFields() {
        Contact contact = new Contact();

        contact.setEmailAddress(createEmailAddressWithLength(Contact.MAX_LENGTH_EMAIL_ADDRESS + 1));
        contact.setFirstName(createStringWithLength(Contact.MAX_LENGTH_FIRST_NAME + 1));
        contact.setLastName(createStringWithLength(Contact.MAX_LENGTH_LAST_NAME + 1));
        contact.setPhoneNumber(createStringWithLength(Contact.MAX_LENGTH_PHONE_NUMBER + 1));

        Address address = new Address();
        address.setStreetAddress(createStringWithLength(Address.MAX_LENGTH_STREET_ADDRESS + 1));
        address.setPostCode(createStringWithLength(Address.MAX_LENGTH_POST_CODE + 1));
        address.setPostOffice(createStringWithLength(Address.MAX_LENGTH_POST_OFFICE + 1));
        address.setState(createStringWithLength(Address.MAX_LENGTH_STATE + 1));
        address.setCountry(createStringWithLength(Address.MAX_LENGTH_COUNTRY + 1));

        contact.setAddress(address);

        return contact;
    }
}
