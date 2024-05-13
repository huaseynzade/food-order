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

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        CardEntity card = cardRepository.findById(dto.getCardId()).orElseThrow(() -> new NotFoundException("Card Not Found"));
        OrderEntity order;
        if (orderRepository.findByStatusAndUserId(OrderStatusEnum.PENDING,user).isEmpty()){
            throw new NotFoundException("Not Found Order");
        }
        order = (OrderEntity) orderRepository.findByStatusAndUserId(OrderStatusEnum.PENDING,user).get();
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
    }

    public List getHistory(Integer userId){
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("Not Found");
        }
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (repository.findAllByUser(user).isEmpty()){
            throw new NotFoundException("Not Found any payment");
        }

        return repository.findAllByUser(user).stream().map(e -> {
            var eDto = map.toDto((PaymentEntity) e);
            eDto.setOrderId(((PaymentEntity) e).getOrderEntity().getOrderId());
            List<CardEntity> cardList = ((PaymentEntity) e).getUser().getCardEntity();
            for (CardEntity cardEntity:cardList){
                if (cardEntity.getCardId() == ((PaymentEntity) e).getCard().getCardId()){
                    eDto.setCardNumber(cardEntity.getCardNumber());
                }
            }
            return eDto;
        }).toList();
    }

    public PaymentResponseDto getById(Integer id){
        if (!repository.existsById(id)){
            throw new NotFoundException("Not Found");
        }
        var entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Payment Not Found"));
        var paymentDto = map.toDto(entity);
        paymentDto.setCardNumber(entity.getCard().getCardNumber());
        paymentDto.setOrderId(entity.getOrderEntity().getOrderId());
        return paymentDto;
    }

    public void cancelOrder(Integer paymentId){
        log.info("Cancel order method for {}",paymentId);
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
        }else {
            throw new WrongOperation("You can't cancel preparing order");
        }
        log.info("Order Cancelled for Payment {}", paymentId);
    }


}