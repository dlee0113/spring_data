package com.packtpub.springdata.jpa.service;

import com.mysema.query.types.Predicate;
import com.packtpub.springdata.jpa.dto.ContactDTO;
import com.packtpub.springdata.jpa.dto.SearchDTO;
import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.model.ContactTestUtil;
import com.packtpub.springdata.jpa.repository.ContactRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

        verify(repositoryMock, times(1)).count(any(Predicate.class));
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void deleteById() throws NotFoundException {
        service.deleteById(ID);

        verify(repositoryMock, times(1)).deleteById(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenContactIsNotFound() throws NotFoundException {
        when(repositoryMock.deleteById(ID)).thenThrow(new NotFoundException(""));

        service.deleteById(ID);

        verify(repositoryMock, times(1)).deleteById(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAllForPage() {
        Page foundPage = buildWantedPage();
        when(repositoryMock.findAll(any(Pageable.class))).thenReturn(foundPage);


        List<Contact> actual = service.findAllForPage(PAGE_INDEX, PAGE_SIZE);

        ArgumentCaptor<Pageable> pageableArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(repositoryMock, times(1)).findAll(pageableArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(foundPage.getContent(), actual);

        Pageable pageSpecification = pageableArgument.getValue();
        assertPage(PAGE_INDEX, PAGE_SIZE, pageSpecification);
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

        Page foundPage = buildWantedPage();
        when(repositoryMock.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);


        List<Contact> actual = service.search(dto);

        ArgumentCaptor<Pageable> pageableArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(repositoryMock, times(1)).findAll(any(Predicate.class), pageableArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(foundPage.getContent(), actual);

        Pageable pageSpecification = pageableArgument.getValue();
        assertPage(dto.getPageIndex(), dto.getPageSize(), pageSpecification);
    }

    private SearchDTO constructSearchDTO() {
        SearchDTO dto = new SearchDTO();
        dto.setPageIndex(PAGE_INDEX);
        dto.setPageSize(PAGE_SIZE);
        dto.setSearchTerm(SEARCH_TERM);

        return dto;
    }

    private Page buildWantedPage() {
        List<Contact> found = new ArrayList<Contact>();
        Page foundPage = new PageImpl<Contact>(found);
        return foundPage;
    }

    private void assertPage(int expectedPageIndex, int expectedPageSize, Pageable actualPage) {
        assertEquals(expectedPageIndex, actualPage.getPageNumber());
        assertEquals(expectedPageSize, actualPage.getPageSize());

        Sort actualSort = actualPage.getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor("lastName").getDirection());
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor("firstName").getDirection());
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
