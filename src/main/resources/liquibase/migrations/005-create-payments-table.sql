create table final_project.payments
(
    card_id      integer
        constraint fkmxoosig1d0lf4hrmauoo4s6mj
            references final_project.cards,
    id           serial
        primary key,
    order_id     integer
        unique
        constraint fk81gagumt0r8y3rmudcgpbk42l
            references final_project.orders,
    payment_date date,
    user_id      integer
        constraint fkj94hgy9v5fw1munb90tar2eje
            references final_project.users,
    status       varchar(255)
        constraint payments_status_check
            check ((status)::text = ANY
        ((ARRAY ['CANCELLED'::character varying, 'PENDING'::character varying, 'WRONG_CARD_INFO'::character varying, 'NOT_ENOUGH_BALANCE'::character varying, 'SUCCESSFUL'::character varying, 'DECLINED_BY_BANK'::character varying])::text[]))
    );

alter table final_project.payments
    owner to postgres;

