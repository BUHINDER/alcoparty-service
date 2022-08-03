create table if not exists event
(
    id            uuid primary key default gen_random_uuid(),
    info          text,
    type          varchar not null,
    location      varchar not null,
    status        varchar not null,
    start_date    bigint  not null,
    end_date      bigint,
    event_creator uuid    not null,
    version       integer not null default 1
);

create table if not exists event_alcoholic
(
    id           uuid primary key default gen_random_uuid(),
    event_id     uuid    not null,
    alcoholic_id uuid    not null,
    is_banned    boolean not null,
    version      integer not null default 1,
    foreign key (event_id) references event (id),
    unique (alcoholic_id, event_id)
);

create table if not exists event_photo
(
    id       uuid primary key default gen_random_uuid(),
    event_id uuid    not null,
    photo_id uuid    not null,
    type     varchar,
    version  integer not null default 1,
    foreign key (event_id) references event (id)
);

create table if not exists invitation_link
(
    id           uuid primary key default gen_random_uuid(),
    event_id     uuid    not null,
    usage_amount integer not null default 1,
    expires_at   bigint  not null,
    version      integer not null default 1,
    foreign key (event_id) references event (id)
);
