package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "User ID cannot be null")
    private Integer userId;

    @NotNull(message = "Card ID cannot be null")
    private Integer cardId;

    @JsonIgnore
    private LocalDate paymentDate;


}
