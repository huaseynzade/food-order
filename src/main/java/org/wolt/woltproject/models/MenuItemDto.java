package org.wolt.woltproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto {
    private Integer menuItemId;
    private String name;
    private String category;
    private String description;
    private Double price;

    private Integer menuId;


}
