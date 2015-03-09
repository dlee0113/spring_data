package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Provides the repository methods for contacts.
 * @author Petri Kainulainen
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT COUNT(c) FROM Contact c WHERE LOWER(c.firstName) LIKE LOWER(:searchTerm) OR LOWER(c.lastName) LIKE LOWER(:searchTerm)")
    public long countContacts(@Param("searchTerm") String searchTerm);

    //@Query(value="SELECT c FROM Contact c WHERE (LOWER(c.firstName) LIKE LOWER(:searchTerm)) OR (LOWER(c.lastName) LIKE LOWER(:searchTerm)) ORDER BY c.lastName ASC, c.firstName ASC",
    //    countQuery = "SELECT COUNT(c) FROM Contact c WHERE LOWER(c.firstName) LIKE LOWER(:searchTerm) OR LOWER(c.lastName) LIKE LOWER(:searchTerm)"
    //)
    @Query("SELECT c FROM Contact c WHERE LOWER(c.firstName) LIKE LOWER(:searchTerm) OR LOWER(c.lastName) LIKE LOWER(:searchTerm)")
    public List<Contact> findContacts(@Param("searchTerm") String searchTerm, Pageable page);
}
