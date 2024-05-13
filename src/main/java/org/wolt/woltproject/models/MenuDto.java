package org.wolt.woltproject.models;

import lombok.Data;

@Data
public class MenuDto {
    private Integer menuId;
    private String name;

    private Integer restaurant_id;

}
