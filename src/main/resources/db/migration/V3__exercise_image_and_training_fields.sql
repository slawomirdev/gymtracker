alter table exercise add column if not exists image_url varchar(300);

alter table training add column if not exists intensity varchar(20);
alter table training add column if not exists location varchar(120);
alter table training add column if not exists body_weight numeric(5, 2);
alter table training add column if not exists duration_minutes int;
