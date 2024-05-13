package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity,Integer> {
    List findAll();
    Optional findByUsername(String username);
}
