package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wolt.woltproject.entities.PaymentEntity;
import org.wolt.woltproject.models.PaymentRequestDto;
import org.wolt.woltproject.models.PaymentResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentMap {
//    @Mapping(source = "cardId",target = "cardEntity.cardId")
    PaymentEntity toEntity(PaymentRequestDto requestDto);


//    @Mapping(source = "cardEntity.cardNumber",target = "cardNumber")
//    @Mapping(source = "orderEntity.orderId",target = "orderId")
    PaymentResponseDto toDto(PaymentEntity entity);


}
