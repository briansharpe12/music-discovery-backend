package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // constructors
    protected Playlist() {}
    public Playlist(String name, String description, User user) {
        setName(name);
        setDescription(description);
        setUser(user);
    }

    // lifecycle
    @PrePersist
    protected void onCreate() {if (createdAt == null) {createdAt = LocalDateTime.now();}}

    // getters
    public Long getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public User getUser() {return user;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    // setters
    public void setName(String name) {
        if (name == null || name.isBlank()) {throw new IllegalArgumentException("Playlist name cannot be null or empty");}
        if (name.length() > 200) {throw new IllegalArgumentException("Playlist name cannot be longer than 200 characters");}
        this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = null;
            return;
        }
        if (description.length() > 500) {throw new IllegalArgumentException("Playlist description cannot be longer than 500 characters");}
        this.description = description.trim();
    }

    public void setUser(User user) {
        if (user == null) {throw new IllegalArgumentException("Playlist must belong to a user");}
        this.user = user;
    }
}