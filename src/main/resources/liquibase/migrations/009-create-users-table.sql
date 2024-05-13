create table final_project.users
(
    birth_date    date,
    register_date date,
    role          varchar(255)
        constraint users_role_check
            check ((role)::text = ANY ((ARRAY ['CUSTOMER'::character varying, 'OWNER'::character varying])::text[])),
    user_id       serial
        primary key,
    address       varchar(255),
    email         varchar(255),
    full_name     varchar(255),
    password      varchar(255),
    phone_number  varchar(255),
    username      varchar(255)
);

alter table final_project.users
    owner to postgres;