package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.wolt.woltproject.entities.UserEntity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CardRequestDto {
    @Size(min = 16,max = 16, message = "Size Must be 16")
    @NotBlank
    private String cardNumber;
//    @Size(min = 5, max = 7)
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Invalid expiry date. Use MM/yy format")
    @Future(message = "Date has expired")
    private LocalDate expDate;
    @Size(max = 3, min = 3, groups = PostValidation.class)
    @NotBlank(groups = PostValidation.class)
    private String CVV;

    @JsonIgnore
    private String fullName;


    private Integer userId;
}
