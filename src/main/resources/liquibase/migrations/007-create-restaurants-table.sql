create table final_project.restaurants
(
    creation_date date,
    menu          integer
        unique
        constraint fk35xs5921w5r80goowpeaw3qe3
            references final_project.menus,
    owner_user_id integer
        unique
        constraint fkt1fj7dabdq74u9hwwkrxe7yrq
            references final_project.users,
    rating        double precision not null,
    restaurant_id serial
        primary key,
    address       varchar(255),
    description   varchar(255),
    name          varchar(255),
    phone_number  varchar(255)
);

alter table final_project.restaurants
    owner to postgres;

