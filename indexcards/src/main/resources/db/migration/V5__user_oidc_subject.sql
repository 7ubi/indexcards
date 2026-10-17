-- Users log in via Authentik (OIDC, Google as source). The access token's subject identifies the account; it is set
-- when the account is created on the first login. Accounts from the former password login keep null.
alter table user add column oidc_subject varchar(255);
alter table user add constraint uk_user_oidc_subject unique (oidc_subject);
