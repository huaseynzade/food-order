package org.wolt.woltproject.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wolt.woltproject.repositories.OrderRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final UserRepository repository;
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 5000)
    public void ao(){
        System.out.println(repository.findById(3).get().getEmail());
    }


    @Scheduled(fixedRate = 500)
    public void checkRepository(){
        for (int i = 0; i < orderRepository.count(); i++) {
            if (!orderRepository.existsById(i)){
                continue;
            }
            LocalDate paymentDate = orderRepository.findById(i).get().getPaymentEntity().getPaymentDate();
            Period period = Period.between(LocalDate.now(),paymentDate);
            if (period.getMonths() >=1){
                orderRepository.deleteByOrderId(i);
            }
        }
    }
}
