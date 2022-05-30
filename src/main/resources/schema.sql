drop table if exists phone CASCADE;
drop table if exists user CASCADE;

create table phone (
  id bigint generated by default as identity,
  city_code varchar(255),
  country_code varchar(255),
  number varchar(255),
  user_id binary not null,
  primary key (id)
);

create table user (
  id binary not null,
  active boolean,
  created timestamp not null,
  email varchar(255),
  last_login timestamp not null,
  last_modified timestamp not null,
  name varchar(255),
  password varchar(255),
  role varchar(255),
  token varchar(255),
  primary key (id)
);

alter table user add constraint USER_EMAIL_UK unique (email);
alter table phone add constraint PHONE_USER_FK foreign key (user_id) references user;
