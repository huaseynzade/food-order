package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Random;

@Entity
@Table(schema = "final_project",name = "cards")
@Data
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;

    @Size(min = 16,max = 16)
    private String cardNumber;


    private LocalDate expDate;

    @Size(max = 3,min = 3)
    private String CVV;

    private String fullName;



    private Double amount = generateAmount();


    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userId")
    private UserEntity user;



    public Double generateAmount(){
        Random random = new Random();
        return random.nextDouble(1000) + 1;
    }
}
