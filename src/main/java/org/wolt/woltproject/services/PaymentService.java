package org.wolt.woltproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.PaymentEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.PaymentStatus;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.NotEnoughBalance;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.exceptions.WrongOperation;
import org.wolt.woltproject.maps.PaymentMap;
import org.wolt.woltproject.models.PaymentRequestDto;
import org.wolt.woltproject.models.PaymentResponseDto;
import org.wolt.woltproject.repositories.CardRepository;
import org.wolt.woltproject.repositories.OrderRepository;
import org.wolt.woltproject.repositories.PaymentRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository repository;
    private final PaymentMap map;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;

    public void create(PaymentRequestDto dto){
        Integer userId = dto.getUserId();
        log.info("ActionLog.PaymentService.create method is started id {}",userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        CardEntity card = cardRepository.findById(dto.getCardId()).orElseThrow(() -> new NotFoundException("Card Not Found"));
        OrderEntity order;
        if (orderRepository.findByStatusAndUserId(OrderStatusEnum.PENDING,user).isEmpty()){
            throw new NotFoundException("Not Found Order");
        }
        order = orderRepository.findByStatusAndUserId(OrderStatusEnum.PENDING,user).get();
        PaymentEntity payment = map.toEntity(dto);
        payment.setOrderEntity(order);

        payment.setUser(user);
        payment.setCard(card);
        if (order.getTotalAmount() > card.getAmount()){
            payment.setStatus(PaymentStatus.NOT_ENOUGH_BALANCE);

            throw new NotEnoughBalance("Balance is not enough.");
        }else {
            card.setAmount(card.getAmount() - order.getTotalAmount());
            order.setStatus(OrderStatusEnum.ACCEPTED);
            payment.setStatus(PaymentStatus.SUCCESSFUL);
            orderRepository.save(order);
            cardRepository.save(card);

        }

        repository.save(payment);
        log.info("ActionLog.PaymentService.create method is finished user id {} and created payment id {}",userId, payment.getId());

    }

    public List<PaymentResponseDto> getHistory(Integer userId){
        log.info("ActionLog.PaymentService.create method is started id {}",userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (repository.findAllByUser(user).isEmpty()){
            throw new NotFoundException("Not Found any payment");
        }
        List<PaymentResponseDto> list = repository.findAllByUser(user).stream().map(e -> {
            var eDto = map.toDto(e);
            eDto.setOrderId((e).getOrderEntity().getOrderId());
            eDto.setCardNumber(e.getCard().getCardNumber());
            return eDto;
        }).toList();


        log.info("ActionLog.PaymentService.create method is started id {}",userId);
        return list;
    }

    public PaymentResponseDto getById(Integer id){
        log.info("ActionLog.PaymentService.getById method is started");

        var entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Payment Not Found"));
        var paymentDto = map.toDto(entity);
        paymentDto.setCardNumber(entity.getCard().getCardNumber());
        paymentDto.setOrderId(entity.getOrderEntity().getOrderId());
        log.info("ActionLog.PaymentService.getById method is finished");

        return paymentDto;
    }

    public void cancelOrder(Integer paymentId){
        log.info("ActionLog.PaymentService.cancelOrder method is started for id {}", paymentId);
        if (!repository.existsById(paymentId)){
            throw new NotFoundException("Not Found Order");
        }

        PaymentEntity payment = repository.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment Not Found"));
        OrderEntity order = orderRepository.findById(payment.getOrderEntity().getOrderId()).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus() == OrderStatusEnum.ACCEPTED) {
            payment.setStatus(PaymentStatus.CANCELLED);
            order.setStatus(OrderStatusEnum.DECLINED);
            repository.save(payment);
            orderRepository.save(order);
            returnMoney(payment);
        }else {
            throw new WrongOperation("You can't cancel preparing order");
        }
        log.info("ActionLog.PaymentService.cancelOrder method is finished for id {}", paymentId);
    }

//     Return User's money
    public void returnMoney(PaymentEntity payment){
        CardEntity card = payment.getCard();
        card.setAmount(card.getAmount() + payment.getOrderEntity().getTotalAmount());
        cardRepository.save(card);
    }


}
