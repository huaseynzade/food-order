package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.PaymentEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;

import org.wolt.woltproject.maps.CardMap;
import org.wolt.woltproject.models.CardRequestDto;
import org.wolt.woltproject.models.CardResponseDto;
import org.wolt.woltproject.repositories.CardRepository;
import org.wolt.woltproject.repositories.PaymentRepository;
import org.wolt.woltproject.repositories.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardMap cardMap;
    private final CardRepository repository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final JwtService jwtService;
    public List<CardResponseDto> showCards() {
        log.info("ActionLog.CardService.showCards method is started");
       List<CardResponseDto> list = repository.findAll().stream().map(cardMap::toDto).toList();
        log.info("ActionLog.CardService.showCards method is finished");
       return list;
    }

    @Transactional
    public void createNewCard(CardRequestDto cardRequestDto, HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        cardRequestDto.setUserId(userId);
        log.info("ActionLog.CardService.createNewCard method is started for user id {}", userId);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        CardEntity cardEntity = cardMap.toEntity(cardRequestDto);

        allocateCardToUser(cardEntity,userEntity);
        repository.save(cardEntity);
        log.info("ActionLog.CardService.createNewCard method is finished for user id {}", userId);
    }

    public List<CardResponseDto> showCardsOfUser(HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.CardService.showCardsOfUser method is started for user {}", userId);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        checkIfUserHasCards(userEntity);
        List<CardResponseDto> dto = userEntity.getCardEntity().stream().map(cardMap::toDto).toList();
        log.info("ActionLog.CardService.showCardsOfUser method is finished for user {}", userId);
        return dto;
    }

    public void deleteCard(Integer id)
    {
        log.info("ActionLog.CardService.deleteCard method is started for card id {}", id);
            CardEntity cardEntity = repository.findById(id).orElseThrow(() -> new NotFoundException("Card Not Found"));
            breakConnectivity(cardEntity);
            repository.deleteById(id);
            log.info("ActionLog.CardService.deleteCard method is finished for card id {}", id);

    }

    //    Seperated Methods for short main methods
    //
    //
    //
    //
    //
    //



    public void checkIfUserHasCards(UserEntity userEntity){
        if (userEntity.getCardEntity().isEmpty()) {
            throw new NotFoundException("No Card");
        }
    }

    public void allocateCardToUser(CardEntity cardEntity, UserEntity userEntity){
        cardEntity.setUser(userEntity);
        cardEntity.setFullName(userEntity.getFullName());
    }

    public void breakConnectivity(CardEntity cardEntity){
        if(paymentRepository.existsByCard(cardEntity)) {

            List<PaymentEntity> paymentEntities = paymentRepository.findAllByCard(cardEntity);

            for (PaymentEntity payment : paymentEntities) {
                payment.setCard(null);
                paymentRepository.save(payment);
            }
        }
    }

}