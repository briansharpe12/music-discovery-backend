package com.briansharpe.musicdiscovery.playlistsong;
import com.briansharpe.musicdiscovery.playlist.Playlist;
import com.briansharpe.musicdiscovery.song.Song;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {

    @EmbeddedId
    private PlaylistSongId id;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    // constructors
    protected PlaylistSong() {}
    public PlaylistSong(Playlist playlist, Song song, Integer position) {
        setPlaylist(playlist);
        setSong(song);
        setPosition(position);
        this.id = new PlaylistSongId(playlist.getId(), song.getId());
    }

    // lifecycle
    @PrePersist
    protected void onCreate() {if (addedAt == null) {addedAt = LocalDateTime.now();}}

    // getters
    public PlaylistSongId getId() {return id;}
    public Playlist getPlaylist() {return playlist;}
    public Song getSong() {return song;}
    public Integer getPosition() {return position;}
    public LocalDateTime getAddedAt() {return addedAt;}

    // setters with validation
    private void setPlaylist(Playlist playlist) {
        if (playlist == null) {throw new IllegalArgumentException("Playlist cannot be null");}
        this.playlist = playlist;
    }

    private void setSong(Song song) {
        if (song == null) {throw new IllegalArgumentException("Song cannot be null");}
        this.song = song;
    }

    public void setPosition(Integer position) {
        if (position == null || position <= 0) {throw new IllegalArgumentException("Position must be greater than 0");}
        this.position = position;
    }
}
