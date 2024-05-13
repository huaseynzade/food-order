package org.wolt.woltproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.enums.PaymentStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private Integer id;

    private Integer orderId;

    private PaymentStatus status;
    private LocalDate paymentDate;

    private String cardNumber;



}
