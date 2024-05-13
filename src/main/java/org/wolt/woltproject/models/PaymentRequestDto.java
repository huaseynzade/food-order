package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentRequestDto {
    public PaymentRequestDto(){
        this.paymentDate = LocalDate.now();
    }
    private Integer id;

    private Integer userId;

    private Integer cardId;

    @JsonIgnore
    private LocalDate paymentDate;


}
