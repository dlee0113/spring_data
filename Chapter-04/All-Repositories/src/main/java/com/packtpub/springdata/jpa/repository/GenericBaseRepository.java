package com.packtpub.springdata.jpa.repository;

import com.packtpub.springdata.jpa.service.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * An implementation for the generic repository which is extended by actual
 * repository implementations.
 * @author Petri Kainulainen
 */
public class GenericBaseRepository<T, ID extends Serializable> extends QueryDslJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericBaseRepository.class);

    public GenericBaseRepository(JpaEntityInformation<T, ID> entityMetadata, EntityManager entityManager) {
        super(entityMetadata, entityManager);
    }

    @Override
    public T deleteById(ID id) throws NotFoundException {
        LOGGER.debug("Deleting an entity with id: {}", id);

        T deleted = findOne(id);
        if (deleted == null) {
            LOGGER.debug("No entity found with id: {}", id);
            throw new NotFoundException("No entity found with id: " + id);
        }

        delete(deleted);
        LOGGER.debug("Deleted entity: {}", deleted);

        return deleted;
    }
}
