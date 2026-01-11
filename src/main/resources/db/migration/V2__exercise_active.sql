alter table exercise
    add column active boolean not null default true;

update exercise
set active = true
where active is null;
