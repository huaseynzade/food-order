package org.wolt.woltproject.models;

import lombok.Data;
import org.wolt.woltproject.enums.RoleEnum;

import java.time.LocalDate;

@Data
public class UserResponseDto {

    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private LocalDate birthDate;
    private String phoneNumber;
    private RoleEnum role;
}
