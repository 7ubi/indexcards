-- Passwords are no longer used: login happens at Authentik with Google.
alter table user drop column password;
-- Accounts from the former password login cannot log in anymore (they have no oidc_subject). Rename them so their
-- usernames are free for the new Authentik accounts.
update user set username = concat('legacy-', left(username, 93)) where oidc_subject is null;
