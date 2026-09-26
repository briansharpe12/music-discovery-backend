package com.briansharpe.musicdiscovery.playlistsong;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlaylistSongId implements Serializable {

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "song_id")
    private Long songId;

    // constructors
    protected PlaylistSongId() {}
    public PlaylistSongId(Long playlistId, Long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    // getters
    public Long getPlaylistId() {return playlistId;}
    public Long getSongId() {return songId;}

    // equality
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        PlaylistSongId that = (PlaylistSongId) o;
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(songId, that.songId);
    }

    @Override
    public int hashCode() {return Objects.hash(playlistId, songId);}
}