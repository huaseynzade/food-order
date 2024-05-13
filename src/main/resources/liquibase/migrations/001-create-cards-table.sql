create table final_project.cards
(
    amount      integer,
    card_id     serial
        primary key,
    user_id     integer
        constraint fkcmanafgwbibfijy2o5isfk3d5
            references final_project.users,
    card_number varchar(255),
    cvv         varchar(255),
    exp_date    date,
    full_name   varchar(255)
);

alter table final_project.cards
    owner to postgres;

