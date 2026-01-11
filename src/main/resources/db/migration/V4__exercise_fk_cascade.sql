-- ensure training_set.exercise_id cascades on delete exercise
alter table training_set
    drop constraint if exists training_set_exercise_id_fkey;

alter table training_set
    add constraint training_set_exercise_id_fkey
        foreign key (exercise_id) references exercise(id) on delete cascade;
