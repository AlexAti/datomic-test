create table users (
  userid serial primary key,
  name text
);

create table memelement (
  memelementid serial primary key,
  easiness real
);

create table answer (
  answerid serial primary key,
  userid int references users(userid),
  memelementid int references memelement(memelementid),
  repn int,
  easiness real,
  quality int,
  timestamp timestamp with time zone not null default now()
);

create table datomic_kvs (
   id text not null,
   rev integer,
   map text,
   val bytea,
   constraint pk_id primary key (id)
) with (
   oids=false
);
