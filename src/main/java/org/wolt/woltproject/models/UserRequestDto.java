package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.wolt.woltproject.enums.RoleEnum;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    @JsonIgnore
    private Integer userId;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    private String email;
    private String fullName;

    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private LocalDate registerDate;
    private RoleEnum role;
}
