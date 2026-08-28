package com.briansharpe.musicdiscovery.artist;
import jakarta.persistence.*;

@Entity
@Table ( name = "artists")
public class Artist {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(name = "artist_name", nullable = false, length = 200)
    private String name;

    //Constructors
    protected Artist() {};
    public Artist(String name) {setName(name);}

    //getters
    public Long getId() {return id;}
    public String getName() {return name;}

    //Setter
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Artist name cannot be null or blank");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Artist name cannot be longer than 200 characters");
        }
        this.name = name.trim();
    }
}
