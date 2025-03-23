create sequence client_SEQ start with 1 increment by 10;
create sequence address_SEQ start with 1 increment by 10;
create sequence phone_SEQ start with 1 increment by 10;

create table client
(
    id         bigint not null primary key,
    address_id bigint,
    name       varchar(50)
);

create table address
(
    id     bigint not null primary key,
    street varchar(50)
);

create table phone
(
    id        bigint not null primary key,
    client_id bigint,
    number    varchar(15)
);

create table users
(
    id       bigint not null primary key,
    login    varchar,
    password varchar
);