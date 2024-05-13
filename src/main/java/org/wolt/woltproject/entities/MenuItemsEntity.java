package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "final_project",name = "menu_items")
@Data
public class MenuItemsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuItemId;

    private String name;
    private String category;
    private String description;

    private Double price;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "menu_id")
    private MenuEntity menu;

    @ManyToMany(cascade = CascadeType.PERSIST,mappedBy = "items", fetch = FetchType.LAZY)
    private List<OrderEntity> orderEntity = new ArrayList<>();

}
