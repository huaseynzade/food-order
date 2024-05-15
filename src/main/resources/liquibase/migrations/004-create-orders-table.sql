create table final_project.orders
(
    order_id     serial
        primary key,
    total_amount double precision not null,
    user_id      integer
        constraint fk32ql8ubntj5uh44ph9659tiih
            references final_project.users,
    status       varchar(255)
        constraint orders_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING'::character varying, 'ACCEPTED'::character varying, 'PREPARING'::character varying, 'DECLINED'::character varying, 'ARRIVED'::character varying, 'IN_COURIER'::character varying])::text[]))
    );

alter table final_project.orders
    owner to postgres;
