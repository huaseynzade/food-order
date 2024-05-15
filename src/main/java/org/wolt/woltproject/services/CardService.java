package org.wolt.woltproject.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;

import org.wolt.woltproject.maps.CardMap;
import org.wolt.woltproject.models.CardRequestDto;
import org.wolt.woltproject.models.CardResponseDto;
import org.wolt.woltproject.repositories.CardRepository;
import org.wolt.woltproject.repositories.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardMap cardMap;
    private final CardRepository repository;
    private final UserRepository userRepository;
    public List<CardResponseDto> showCards() {
        log.info("ActionLog.CardService.showCards method is started");
       List<CardResponseDto> list = repository.findAll().stream().map(e -> cardMap.toDto((CardEntity) e)).toList();
        log.info("ActionLog.CardService.showCards method is finished");
       return list;
    }

    @Transactional
    public void createNewCard(CardRequestDto cardRequestDto) {
        log.info("ActionLog.CardService.createNewCard method is started");

        CardEntity cardEntity = cardMap.toEntity(cardRequestDto);

        if (!userRepository.existsById(cardRequestDto.getUserId())) {
            throw new NotFoundException("Not Found User");
        }
        UserEntity userEntity = userRepository.findById(cardRequestDto.getUserId()).orElseThrow(() -> new NotFoundException("User Not Found"));
        userEntity.setUserId(cardRequestDto.getUserId());
        log.info("Card is creating for user {}", userEntity.getFullName());
        cardEntity.setUser(userEntity);
        cardEntity.setFullName(userEntity.getFullName());
        repository.save(cardEntity);
        log.info("ActionLog.CardService.createNewCard method is finished");
    }

    public List<CardResponseDto> showCardsOfUser(Integer userId) {
        log.info("ActionLog.CardService.showCardsOfUser method is started");

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (userEntity.getCardEntity().isEmpty()) {
            throw new NotFoundException("No Card");
        }
        List<CardResponseDto> dto = userEntity.getCardEntity().stream().map(cardMap::toDto).toList();
        log.info("ActionLog.CardService.showCardsOfUser method is finished");
        return dto;
    }

    public void deleteCard(Integer id)
    {
        log.info("ActionLog.CardService.deleteCard method is started");

        if (repository.existsById(id)){
            repository.deleteById(id);
            log.info("ActionLog.CardService.deleteCard method is finished");
        }else{
            throw new NotFoundException("Not found");
        }
    }

}