package com.packtpub.springdata.jpa.repository;

import com.mysema.query.types.Predicate;
import com.packtpub.springdata.jpa.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.packtpub.springdata.jpa.repository.ContactPredicates.firstOrLastNameStartsWith;

/**
 * @author Petri Kainulainen
 */
public class ContactRepositoryImpl implements PaginatingContactRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private QueryDslJpaRepository<Contact, Long> repository;

    @Override
    public long findContactCount(String searchTerm) {
        LOGGER.debug("Finding contact count for search term: {}", searchTerm);
        return repository.count(firstOrLastNameStartsWith(searchTerm));
    }

    @Override
    public List<Contact> findAllForPage(int pageIndex, int pageSize) {
        LOGGER.debug("Finding {} contacts for page {}", pageSize, pageIndex);

        Pageable pageSpec = buildPageSpecification(pageIndex, pageSize);
        Page<Contact> wanted = repository.findAll(pageSpec);

        return wanted.getContent();
    }

    @Override
    public List<Contact> findContactsForPage(String searchTerm, int pageIndex, int pageSize) {
        LOGGER.debug("Finding {} contacts for page {} with search term: {}", new Object[] {pageSize, pageIndex, searchTerm});

        Predicate searchCondition = firstOrLastNameStartsWith(searchTerm);
        Pageable pageSpec = buildPageSpecification(pageIndex, pageSize);
        Page<Contact> wanted = repository.findAll(searchCondition, pageSpec);

        return wanted.getContent();
    }

    /**
     * Creates the Pageable which is used to pass the pagination arguments to
     * Spring Data.
     * @param pageIndex The index of the wanted page.
     * @param pageSize  The size of the wanted page.
     * @return
     */
    private Pageable buildPageSpecification(int pageIndex, int pageSize) {
        return new PageRequest(pageIndex, pageSize, sortByLastNameAndFirstNameAsc());
    }

    /**
     * Creates a Sort object which specifies that the results
     * are sorted in ascending order by using last name and first name.
     * @return
     */
    private Sort sortByLastNameAndFirstNameAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "lastName"),
                new Sort.Order(Sort.Direction.ASC, "firstName")
        );
    }

    /**
     * An initialization method which is run after the bean has been constructed.
     * This ensures that the entity manager is injected before we try to use it.
     */
    @PostConstruct
    public void init() {
        JpaEntityInformation<Contact, Long> contactEntityInfo = new JpaMetamodelEntityInformation<Contact, Long>(Contact.class, entityManager.getMetamodel());
        repository = new QueryDslJpaRepository<Contact, Long>(contactEntityInfo, entityManager);
    }
}
