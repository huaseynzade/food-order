create table final_project.restaurant_owners
(
    id            serial
        primary key,
    restaurant_id integer
        constraint fk2xq318jx2crg13ta4kjllea5w
            references final_project.restaurants
);

alter table final_project.restaurant_owners
    owner to postgres;

