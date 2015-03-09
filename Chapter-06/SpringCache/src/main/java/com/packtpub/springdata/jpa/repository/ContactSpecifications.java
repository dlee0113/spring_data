package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.model.Contact;
import com.packtpub.springdata.jpa.model.Contact_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * A specification builder class which is used to build JPA specifications.
 * @author Petri Kainulainen
 */
public class ContactSpecifications {

    /**
     * Builds a specification which ensures that the first or last name
     * of a contact starts with the given search term when case is ignored.
     * @param searchTerm    The used search term.
     * @return
     */
    public static Specification<Contact> firstOrLastNameStartsWith(final String searchTerm) {
        return new Specification<Contact>() {
            @Override
            public Predicate toPredicate(Root<Contact> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String likePattern = getLikePattern(searchTerm);

                return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.<String>get(Contact_.firstName)), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.<String>get(Contact_.lastName)), likePattern)
                );
            }

            private String getLikePattern(final String searchTerm) {
                return searchTerm.toLowerCase() + "%";
            }
        };
    }
}
