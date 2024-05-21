package org.wolt.woltproject.models;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String password;
    private String password2;
}
