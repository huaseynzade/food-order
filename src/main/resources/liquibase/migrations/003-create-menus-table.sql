create table final_project.menus
(
    menu_id serial
        primary key,
    name    varchar(255)
);

alter table final_project.menus
    owner to postgres;

