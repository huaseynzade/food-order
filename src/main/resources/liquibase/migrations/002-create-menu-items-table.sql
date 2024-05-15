create table final_project.menu_items
(
    menu_id      integer
        constraint fk6fwmu1a0d0hysfd3c00jxyl2c
            references final_project.menus,
    menu_item_id serial
        primary key,
    price        double precision,
    category     varchar(255),
    description  varchar(255),
    name         varchar(255)
);

alter table final_project.menu_items
    owner to postgres;
