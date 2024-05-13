create table final_project.payments
(
    card_entity_card_id integer
        unique
        constraint fkxg2gm7e9o40d2eeo5ep4l03y
            references final_project.cards,
    id                  serial
        primary key,
    payment_id          integer
        unique
        constraint fk47qargsqwk1eb17xnf21fnwxl
            references final_project.orders,
    status              smallint
        constraint payments_status_check
            check ((status >= 0) AND (status <= 3))
);

alter table final_project.payments
    owner to postgres;
