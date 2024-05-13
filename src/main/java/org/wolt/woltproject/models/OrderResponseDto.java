package org.wolt.woltproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.OrderStatusEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Integer orderId;
    private String userName;
    private OrderStatusEnum status;
    private double totalAmount;

    private List<MenuItemDto> items;


}
