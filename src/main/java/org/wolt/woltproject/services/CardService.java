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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardMap cardMap;
    private final CardRepository repository;
    private final UserRepository userRepository;
    public List showCards() {
        var entities = repository.findAll();
        return entities.stream().map(e -> cardMap.toDto((CardEntity) e)).toList();
    }

    @Transactional
    public void createNewCard(CardRequestDto cardRequestDto) {

        CardEntity cardEntity = cardMap.toEntity(cardRequestDto);

        if (!userRepository.existsById(cardRequestDto.getUserId())) {
            throw new NotFoundException("Not Found User");
        }
        UserEntity userEntity = userRepository.findById(cardRequestDto.getUserId()).get();
        userEntity.setUserId(cardRequestDto.getUserId());
        log.info("Card is creating for user {}", userEntity.getFullName());
        cardEntity.setUser(userEntity);
        cardEntity.setFullName(userEntity.getFullName());
        repository.save(cardEntity);
        log.info("New Card Created with id : {}", cardEntity.getCardId());

    }

    public List<CardResponseDto> showCardsOfUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        UserEntity userEntity = userRepository.findById(userId).get();
        if (userEntity.getCardEntity().isEmpty()) {
            throw new NotFoundException("No Card");
        }
        return userEntity.getCardEntity().stream().map(cardMap::toDto).toList();
    }

    public void deleteCard(Integer id)
    {
        if (repository.existsById(id)){
            repository.deleteById(id);
        }else{
            throw new NotFoundException("Not found");
        }
    }

}