package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.artist.Artist;
import jakarta.persistence.*;

@Entity
@Table (name = "songs")
public class Song {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @ManyToOne
    @JoinColumn (name = "artist_id", nullable = false)
    private Artist artist;


    //constructors
    protected Song() {}
    public Song(String title, Integer durationSeconds, Artist artist) {
        setTitle(title);
        setDurationSeconds(durationSeconds);
        setArtist(artist);
    }

    //getters
    public Long getId() {return id;}
    public String getTitle() {return title;}
    public Integer getDurationSeconds() {return durationSeconds;}
    public Artist getArtist() {return artist;}

    //setters
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot be longer than 200 characters");
        }
        this.title = title.trim();
    }

    public void setDurationSeconds(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0 ) {
            throw new IllegalArgumentException("durationSeconds must be greater than 0 seconds");
        }
        this.durationSeconds = durationSeconds;
    }

    public void setArtist(Artist artist) {
        if (artist == null) {
            throw new IllegalArgumentException("artist cannot be null, a song must have an artist");
        }
        this.artist = artist;
    }
}

