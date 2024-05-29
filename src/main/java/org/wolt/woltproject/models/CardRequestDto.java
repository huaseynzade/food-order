package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.wolt.woltproject.entities.UserEntity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDto {
    @Size(min = 16,max = 16, message = "Size Must be 16")
    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must contain only digits")
    private String cardNumber;

//    @Pattern(regexp = "\\d{2}/\\d{2}")
    @Future(message = "Date has expired")
    private LocalDate expDate;

    @Size(max = 3, min = 3)
    @NotBlank()
    @Pattern(regexp = "\\d{3}", message = "Card CVV must contain only digits")
    private String CVV;

    @JsonIgnore
    private String fullName;

//    @NotNull(message = "User ID cannot be null")
    @JsonIgnore
    private Integer userId;
}
