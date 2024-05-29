package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    public PaymentRequestDto(){
        this.paymentDate = LocalDate.now();
    }
    @JsonIgnore
    private Integer id;

    @JsonIgnore
    private Integer userId;

    @NotNull(message = "Card ID cannot be null")
    private Integer cardId;

    @JsonIgnore
    private LocalDate paymentDate;


}
