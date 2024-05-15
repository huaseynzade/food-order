create table final_project.reviews
(
    id         serial
        primary key,
    rating     double precision,
    restaurant integer
        constraint fkl75bjsgakeoiuj3cixhgq8er3
            references final_project.restaurants,
    user_id    integer
        constraint fkcgy7qjc1r99dp117y9en6lxye
            references final_project.users,
    comment    varchar(255)
);

alter table final_project.reviews
    owner to postgres;

