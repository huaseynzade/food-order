package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.wolt.woltproject.enums.OrderStatusEnum;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "final_project",name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status = OrderStatusEnum.PENDING;

    //    Method
    private double totalAmount;


    @OneToOne(cascade = CascadeType.ALL,mappedBy = "orderEntity")
    private PaymentEntity paymentEntity;


    @ManyToMany(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinTable(schema = "final_project",name = "order_item",joinColumns = @JoinColumn(name = "order_id"),inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<MenuItemsEntity> items = new ArrayList<>();
}
