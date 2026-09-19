ALTER TABLE users
    ADD CONSTRAINT unique_users_username UNIQUE (username);

ALTER TABLE users
    ADD CONSTRAINT unique_users_email UNIQUE (email);