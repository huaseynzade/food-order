package org.wolt.woltproject.models;

import lombok.Data;
import org.wolt.woltproject.enums.OrderStatusEnum;

@Data
public class OrderCourierDto {
    private Integer orderId;
    private String userName;
    private OrderStatusEnum status;
    private String restaurantName;

}
