create table final_project.order_item
(
    item_id  integer not null
        constraint fkmnutcj4r9kc4c952ux3lips27
            references final_project.menu_items,
    order_id integer not null
        constraint fkt4dc2r9nbvbujrljv3e23iibt
            references final_project.orders
);