package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.config.PersistenceTestContext;
import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.model.Contact_;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.persistence.criteria.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceTestContext.class})
public class ContactSpecificationsTest {

    private static final String LIKE_PATTERN = "foo%";
    private static final String SEARCH_TERM = "Foo";

    private CriteriaBuilder criteriaBuilderMock;
    private CriteriaQuery criteriaQueryMock;
    private Root<Contact> rootMock;

    @Before
    public void setUp() {
        criteriaBuilderMock = mock(CriteriaBuilder.class);
        criteriaQueryMock = mock(CriteriaQuery.class);
        rootMock = mock(Root.class);
    }

    @Test
    public void firstOrLastNameStartsWith() {
        Path firstNamePathMock = mock(Path.class);
        Path lastNamePathMock = mock(Path.class);

        when(rootMock.get(Contact_.firstName)).thenReturn(firstNamePathMock);
        when(rootMock.get(Contact_.lastName)).thenReturn(lastNamePathMock);

        Expression firstNameLowerExpressionMock = mock(Expression.class);
        Expression lastNameLowerExpressionMock = mock(Expression.class);

        when(criteriaBuilderMock.lower(firstNamePathMock)).thenReturn(firstNameLowerExpressionMock);
        when(criteriaBuilderMock.lower(lastNamePathMock)).thenReturn(lastNameLowerExpressionMock);

        Predicate firstNameLikePredicateMock = mock(Predicate.class);
        Predicate lastNameLikePredicateMock = mock(Predicate.class);

        when(criteriaBuilderMock.like(firstNameLowerExpressionMock, LIKE_PATTERN)).thenReturn(firstNameLikePredicateMock);
        when(criteriaBuilderMock.like(lastNameLowerExpressionMock, LIKE_PATTERN)).thenReturn(lastNameLikePredicateMock);

        Predicate searchPredicateMock = mock(Predicate.class);

        when(criteriaBuilderMock.or(firstNameLikePredicateMock, lastNameLikePredicateMock)).thenReturn(searchPredicateMock);

        Specification<Contact> specification = ContactSpecifications.firstOrLastNameStartsWith(SEARCH_TERM);
        Predicate actualSearchPredicate = specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(rootMock, times(1)).get(Contact_.firstName);
        verify(rootMock, times(1)).get(Contact_.lastName);

        verifyNoMoreInteractions(rootMock);

        verify(criteriaBuilderMock, times(1)).lower(firstNamePathMock);
        verify(criteriaBuilderMock, times(1)).lower(lastNamePathMock);

        verifyNoMoreInteractions(firstNamePathMock, lastNamePathMock);

        verify(criteriaBuilderMock, times(1)).like(firstNameLowerExpressionMock, LIKE_PATTERN);
        verify(criteriaBuilderMock, times(1)).like(lastNameLowerExpressionMock, LIKE_PATTERN);

        verifyNoMoreInteractions(firstNameLowerExpressionMock, lastNameLowerExpressionMock);

        verify(criteriaBuilderMock, times(1)).or(firstNameLikePredicateMock, lastNameLikePredicateMock);
        verifyNoMoreInteractions(firstNameLikePredicateMock, lastNameLikePredicateMock);

        verifyNoMoreInteractions(criteriaBuilderMock);

        verifyZeroInteractions(criteriaQueryMock);

        assertEquals(searchPredicateMock, actualSearchPredicate);
    }

}
