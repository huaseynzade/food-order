package org.wolt.woltproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(schema = "final_project",name = "menus")
@Data
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;
    private String name;


    @OneToOne(cascade = CascadeType.ALL,mappedBy = "menu")
    private RestaurantEntity restaurant;

    @JsonIgnore
    @OneToMany(mappedBy = "menu")
    private List<MenuItemsEntity> items;
}
