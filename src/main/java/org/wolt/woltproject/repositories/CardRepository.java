package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.CardEntity;

import java.util.List;

public interface CardRepository extends CrudRepository<CardEntity,Integer>{
    List findAll();

}
