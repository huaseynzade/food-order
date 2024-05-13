create table final_project.orders
(
    order_id     serial
        primary key,
    status       smallint
        constraint orders_status_check
            check ((status >= 0) AND (status <= 3)),
    total_amount double precision not null,
    user_id      integer
        unique
        constraint fk32ql8ubntj5uh44ph9659tiih
            references final_project.users
);

alter table final_project.orders
    owner to postgres;

