package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wolt.woltproject.config.myannotations.Under18;
import org.wolt.woltproject.enums.RoleEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "final_project",name = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String username;
    private String password;
    @Email
    private String email;
    private String fullName;


    private boolean isActivated = false;
    @Under18
    private LocalDate birthDate;

    private String address;

    private String phoneNumber;
    private LocalDate registerDate;



    private Integer activationCode;
    // Default Value for AuthService 89
    private LocalDateTime codeSentTime = LocalDateTime.of(2020,12,12,12,11,12);

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CardEntity> cardEntity;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ReviewsEntity> reviews;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orderEntity;

}
