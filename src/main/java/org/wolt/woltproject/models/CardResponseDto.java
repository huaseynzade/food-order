package org.wolt.woltproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDto {
    private String cardId;
    private String cardNumber;
    private String expDate;

    private String fullName;

    private Integer amount;
}
