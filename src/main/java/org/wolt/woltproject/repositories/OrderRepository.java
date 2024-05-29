package org.wolt.woltproject.repositories;

import org.hibernate.query.Order;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.OrderStatusEnum;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity,Integer> {
    List<OrderEntity> findAll();

    Optional<OrderEntity> findByUserId(UserEntity userEntity);

    void deleteByOrderId(Integer orderId);

    Optional<OrderEntity> findByStatusAndUserId(OrderStatusEnum orderStatusEnum, UserEntity user);

    List<OrderEntity> findAllByUserId(UserEntity entity);


    List<OrderEntity> findAllByStatusIs(OrderStatusEnum status);

    List<OrderEntity> findAllByUserIdAndStatusIsNot(UserEntity userEntity, OrderStatusEnum statusEnum);

    boolean existsByUserIdAndStatusIs(UserEntity user, OrderStatusEnum statusEnum);



}
