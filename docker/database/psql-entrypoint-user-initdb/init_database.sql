select 'create database user_db'
where not exists(select from pg_database where datname = 'user_db')
\gexec


\c user_db;


alter system set wal_level = 'logical';


create table if not exists app_user
(
    id          bigserial primary key,
    name        varchar(100),
    avatar      varchar(100),
    position    varchar(100),
    created_at  timestamptz default current_date,
    modified_at timestamptz default current_date
);

create table if not exists app_user_deleted
(
    id              bigserial primary key,
    user_id         bigint,
    name            varchar(100),
    avatar          varchar(100),
    position        varchar(100)
);


insert into app_user (name, avatar, position)
values ('UserName-1', 'UserAvatar-1', 'UserPosition-1'),
       ('UserName-2', 'UserAvatar-2', 'UserPosition-2'),
       ('UserName-3', 'UserAvatar-3', 'UserPosition-3');


create or replace function add_app_user_deleted() returns trigger AS
$$
begin
    insert into app_user_deleted (user_id, name, avatar, position)
    select id, name, avatar, position from app_user where id = OLD.id;
    return old;
end;
$$ language plpgsql;


drop trigger if exists delete_app_user on app_user;


create trigger delete_app_user
    before delete
    on app_user
    for each row
execute procedure add_app_user_deleted();
