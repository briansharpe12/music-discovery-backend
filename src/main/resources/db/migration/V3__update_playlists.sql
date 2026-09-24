ALTER TABLE playlists
    ADD COLUMN description VARCHAR(500);

CREATE INDEX idx_playlists_user_id
    ON playlists(user_id);