package org.wolt.woltproject.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourierDetailsDto {

        private Integer id;
        private String name;
        private String phoneNumber;
}
