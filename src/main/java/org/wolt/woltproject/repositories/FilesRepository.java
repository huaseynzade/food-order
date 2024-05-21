package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wolt.woltproject.entities.FilesEntity;

@Repository
public interface FilesRepository extends CrudRepository<FilesEntity,Integer> {
}
