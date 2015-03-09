package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.config.PersistenceTestContext;
import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.model.ContactTestUtil;
import com.packtpub.springdata.jpa.service.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceTestContext.class})
public class GenericBaseRepositoryTest {

    private Long ID = Long.valueOf(2);

    @PersistenceContext
    private EntityManager entityManager;

    private GenericBaseRepository<Contact, Long> repositorySpy;

    @Before
    public void setUp() {
        JpaEntityInformation<Contact, Long> contactEntityInfo = new JpaMetamodelEntityInformation<Contact, Long>(Contact.class, entityManager.getMetamodel());
        repositorySpy = spy(new GenericBaseRepository<Contact, Long>(contactEntityInfo, entityManager));
    }

    @Test
    public void deleteById() throws NotFoundException {
        Contact deleted = ContactTestUtil.createModel(ID);
        doReturn(deleted).when(repositorySpy).findOne(ID);
        doNothing().when(repositorySpy).delete(deleted);

        Contact actual = repositorySpy.deleteById(ID);

        verify(repositorySpy, times(1)).deleteById(ID);
        verify(repositorySpy, times(1)).findOne(ID);
        verify(repositorySpy, times(1)).delete(deleted);
        verifyNoMoreInteractions(repositorySpy);

        assertEquals(deleted, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenContactIsNotFound() throws NotFoundException {
        doReturn(null).when(repositorySpy).findOne(ID);

        repositorySpy.deleteById(ID);

        verify(repositorySpy, times(1)).deleteById(ID);
        verify(repositorySpy, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositorySpy);
    }
}
