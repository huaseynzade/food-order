package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.PaymentEntity;
import org.wolt.woltproject.entities.UserEntity;

import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<PaymentEntity,Integer> {
    List<PaymentEntity> findAllByUser(UserEntity entity);
    List<PaymentEntity> findAllByCard(CardEntity entity);

    boolean existsByCard(CardEntity entity);

}
