-- Users bring their own Gemini API key for the AI card generation. It is stored AES-GCM encrypted
-- (see ApiKeyEncryptor); the hint holds only the last characters so the UI can show which key is saved.
alter table user add column ai_api_key_encrypted varchar(1024);
alter table user add column ai_api_key_hint varchar(8);
