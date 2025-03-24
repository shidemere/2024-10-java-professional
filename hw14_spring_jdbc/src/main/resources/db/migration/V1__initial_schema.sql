create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table address
(
    id        bigserial not null primary key,
    client_id bigint REFERENCES client (id),
    street    varchar(50)
);

create table phone
(
    id        bigserial not null primary key,
    client_id bigint REFERENCES client (id),
    number    varchar(15)
);