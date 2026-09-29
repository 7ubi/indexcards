-- Users get a role so admins can access /api/admin/**. Promote the first admin by hand:
--   update user set role = 'ADMIN' where username = '<username>';
alter table user add column role varchar(20) not null default 'USER';
-- Signup date for the admin analytics; users created before this migration keep null.
alter table user add column created_at datetime(6);
