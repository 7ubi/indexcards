-- Projects can be archived manually or automatically once their exam date has passed. auto_archived_exam_date is the
-- exam date auto-archiving last fired for, so a project the user unarchives again is not re-archived for the same date.
alter table project add column archived bit not null default 0;
alter table project add column auto_archived_exam_date date;
