create table final_project.cards
(
    amount      double precision,
    card_id     serial
        primary key,
    cvv         varchar(3),
    exp_date    date,
    user_id     integer
        constraint fkcmanafgwbibfijy2o5isfk3d5
            references final_project.users,
    card_number varchar(16),
    full_name   varchar(255)
);

alter table final_project.cards
    owner to postgres;


