create table final_project.users
(
    birth_date      date,
    is_activated    boolean not null,
    register_date   date,
    user_id         serial
        primary key,
    address         varchar(255),
    email           varchar(255),
    full_name       varchar(255),
    password        varchar(255),
    phone_number    varchar(255),
    role            varchar(255)
        constraint users_role_check
            check ((role)::text = ANY
                   (ARRAY [('CUSTOMER'::character varying)::text, ('RESTAURANT'::character varying)::text, ('COURIER'::character varying)::text, ('ADMIN'::character varying)::text])),
    username        varchar(255),
    activation_code integer,
    code_sent_time  timestamp(6)
);

alter table final_project.users
    owner to postgres;