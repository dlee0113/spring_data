package com.packtpub.springdata.jpa.repository;

import com.mysema.query.types.Predicate;
import com.packtpub.springdata.jpa.model.Contact;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class ContactRepositoryImplTest {

    private static final int PAGE_INDEX = 1;
    private static final int PAGE_SIZE = 10;

    private static final String SEARCH_TERM = "Bar";

    private ContactRepositoryImpl repository;

    private QueryDslJpaRepository<Contact, Long> contactRepositoryMock;

    @Before
    public void setUp() {
        repository = new ContactRepositoryImpl();

        contactRepositoryMock = mock(QueryDslJpaRepository.class);
        ReflectionTestUtils.setField(repository, "repository", contactRepositoryMock);
    }

    @Test
    public void countContacts() {
        repository.findContactCount(SEARCH_TERM);

        verify(contactRepositoryMock, times(1)).count(any(Predicate.class));
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    public void findAllForPage() {
        Page foundPage = buildWantedPage();
        when(contactRepositoryMock.findAll(any(Pageable.class))).thenReturn(foundPage);

        repository.findAllForPage(PAGE_INDEX, PAGE_SIZE);

        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(contactRepositoryMock, times(1)).findAll(pageArgument.capture());
        verifyNoMoreInteractions(contactRepositoryMock);

        Pageable pageSpecification = pageArgument.getValue();
        assertPage(PAGE_INDEX, PAGE_SIZE, pageSpecification);
    }

    @Test
    public void findContactsForPage() {
        Page foundPage = buildWantedPage();
        when(contactRepositoryMock.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(foundPage);

        repository.findContactsForPage(SEARCH_TERM, PAGE_INDEX, PAGE_SIZE);

        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(contactRepositoryMock, times(1)).findAll(any(Predicate.class), pageArgument.capture());
        verifyNoMoreInteractions(contactRepositoryMock);

        Pageable pageSpecification = pageArgument.getValue();
        assertPage(PAGE_INDEX, PAGE_SIZE, pageSpecification);
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
}
