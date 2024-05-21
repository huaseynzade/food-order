package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wolt.woltproject.entities.KeysEntity;
import org.wolt.woltproject.entities.UserEntity;

import java.util.Optional;

@Repository
public interface KeyRepository extends CrudRepository<KeysEntity, Integer> {

    Optional<KeysEntity> findByUserId(UserEntity userEntity);

    void deleteByUserId(UserEntity user);

}
