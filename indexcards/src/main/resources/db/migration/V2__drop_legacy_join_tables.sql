-- Before User.projects / Project.indexCards were mapped with mappedBy, Hibernate kept these relations in the join
-- tables user_projects / project_index_cards. ddl-auto=update never dropped them, and their foreign keys now block
-- deleting users and projects. Old projects and index cards may only be linked through them, so copy the links into
-- project.user_id / indexcards.project_id before dropping the tables.

-- Databases created after the mapping change never had these tables.
create table if not exists user_projects (user_user_id bigint not null, projects_project_id bigint not null) engine=InnoDB;
create table if not exists project_index_cards (project_project_id bigint not null, index_cards_indexcard_id bigint not null) engine=InnoDB;

update project p
set p.user_id = (select min(up.user_user_id) from user_projects up where up.projects_project_id = p.project_id)
where p.user_id is null;

update indexcards c
set c.project_id = (select min(pic.project_project_id) from project_index_cards pic where pic.index_cards_indexcard_id = c.indexcard_id)
where c.project_id is null;

drop table user_projects;
drop table project_index_cards;
