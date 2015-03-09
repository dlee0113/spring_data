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

    public long countContacts(@Param("searchTerm") String searchTerm);

    public List<Contact> findContacts(@Param("searchTerm") String searchTerm, Pageable page);
}
