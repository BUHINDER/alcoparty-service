alter table event_alcoholic
    drop constraint event_alcoholic_event_id_fkey;
alter table event_photo
    drop constraint event_photo_event_id_fkey;
alter table invitation_link
    drop constraint invitation_link_event_id_fkey;

alter table event_alcoholic
    add foreign key (event_id) references event
        on delete cascade;
alter table event_photo
    add foreign key (event_id) references event
        on delete cascade;
alter table invitation_link
    add foreign key (event_id) references event
        on delete cascade;
