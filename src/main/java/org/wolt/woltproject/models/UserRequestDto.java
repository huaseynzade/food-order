package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.wolt.woltproject.enums.RoleEnum;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    @JsonIgnore
    private Integer userId;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
    private String password;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;


    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+994\\d{9}$", message = "Phone number must be in the format +994xxxxxxxxx")
    private String phoneNumber;
    @NotNull(message = "Register date cannot be null")
    private LocalDate registerDate;
    @NotNull(message = "Role cannot be null")
    private RoleEnum role;
}
