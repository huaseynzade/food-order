package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.PaymentEntity;
import org.wolt.woltproject.entities.UserEntity;

import java.util.List;

public interface PaymentRepository extends CrudRepository<PaymentEntity,Integer> {
    List findAllByUser(UserEntity entity);
}
