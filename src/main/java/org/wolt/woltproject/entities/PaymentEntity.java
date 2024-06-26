package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.wolt.woltproject.enums.PaymentStatus;

import java.time.LocalDate;

@Entity
@Table(schema = "final_project",name = "payments")
@Data
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;


    private LocalDate paymentDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @Override
    public String toString() {
        return "PaymentEntity{" +
                "id=" + id +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", user=" + (user != null ? user.getUserId() : null) +
                ", card=" + (card != null ? card.getCardId() : null) +
                '}';
    }
}
