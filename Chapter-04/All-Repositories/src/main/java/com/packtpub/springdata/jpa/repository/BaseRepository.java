package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.service.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * The base interface for all Spring Data JPA repositories.
 * @author Petri Kainulainen
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, QueryDslPredicateExecutor<T> {

    /**
     * Deletes an entity
     * @param id    The id of the deleted entity.
     * @return  The deleted entity
     * @throws NotFoundException if an entity is not found with the given id.'
     */
    public T deleteById(ID id) throws NotFoundException;
}
