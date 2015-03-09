package com.packtpub.springdata.jpa.service;

import com.packtpub.springdata.jpa.dto.ContactDTO;
import com.packtpub.springdata.jpa.dto.SearchDTO;
import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.model.ContactTestUtil;
import com.packtpub.springdata.jpa.repository.ContactRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RepositoryContactServiceTest {

    private static final Long ID = Long.valueOf(3);

    private static final int PAGE_INDEX = 0;
    private static final int PAGE_SIZE = 10;
    private static final String SEARCH_TERM = "foo";

    private RepositoryContactService service;

    private ContactRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryContactService();

        repositoryMock = mock(ContactRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void add() {
        ContactDTO added = ContactTestUtil.createDTO();

        service.add(added);

        ArgumentCaptor<Contact> contactArgument = ArgumentCaptor.forClass(Contact.class);
        verify(repositoryMock, times(1)).save(contactArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        Contact actual = contactArgument.getValue();
        ContactTestUtil.assertContact(added, actual);
    }

    @Test
    public void count() {
        service.count();

        verify(repositoryMock, times(1)).count();
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void countForSearch() {
        SearchDTO dto = new SearchDTO();
        dto.setSearchTerm(SEARCH_TERM);

        service.count(dto);

        verify(repositoryMock, times(1)).findContactCount(dto.getSearchTerm());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void deleteById() throws NotFoundException {
        Contact deleted = new Contact();
        when(repositoryMock.findOne(ID)).thenReturn(deleted);

        Contact actual = service.deleteById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verify(repositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(deleted, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenContactIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(ID)).thenReturn(null);

        service.deleteById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAllForPage() {
        ArrayList<Contact> found = new ArrayList<Contact>();

        when(repositoryMock.findAllForPage(PAGE_INDEX, PAGE_SIZE)).thenReturn(found);

        List<Contact> actual = service.findAllForPage(PAGE_INDEX, PAGE_SIZE);

        verify(repositoryMock, times(1)).findAllForPage(PAGE_INDEX, PAGE_SIZE);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(found, actual);
    }

    @Test
    public void findById() throws NotFoundException {
        Contact found = new Contact();
        when(repositoryMock.findOne(ID)).thenReturn(found);

        Contact actual = service.findById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(actual, found);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenContactIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(ID)).thenReturn(null);

        service.findById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws NotFoundException {
        Contact found = ContactTestUtil.createModel(ID);
        when(repositoryMock.findOne(ID)).thenReturn(found);

        ContactDTO updated = ContactTestUtil.createDTO(ID);
        Contact actual = service.update(updated);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);

        ContactTestUtil.assertContact(updated, found);
        assertEquals(found, actual);
    }

    @Test
    public void search() {
        SearchDTO dto = constructSearchDTO();

        ArrayList<Contact> found = new ArrayList<Contact>();
        when(repositoryMock.findContactsForPage(dto.getSearchTerm(), dto.getPageIndex(), dto.getPageSize())).thenReturn(found);


        List<Contact> actual = service.search(dto);

        verify(repositoryMock, times(1)).findContactsForPage(dto.getSearchTerm(), dto.getPageIndex(), dto.getPageSize());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(found, actual);
    }

    private SearchDTO constructSearchDTO() {
        SearchDTO dto = new SearchDTO();
        dto.setPageIndex(PAGE_INDEX);
        dto.setPageSize(PAGE_SIZE);
        dto.setSearchTerm(SEARCH_TERM);

        return dto;
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenContactIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(ID)).thenReturn(null);

        ContactDTO updated = ContactTestUtil.createDTO(ID);
        service.update(updated);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);
    }
}
