package com.briansharpe.musicdiscovery.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 75)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //Constructor + JPA required empty Constructor
    protected User() {}
    public User(String username, String email) {
        setUsername(username);
        setEmail(email);
    }

    @PrePersist
    protected void onCreate() {createdAt = LocalDateTime.now();}

    //Getters
    public Long getId() {return id;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    //Setters
    public void setUsername(String username){
        if (username == null || username.isBlank()){throw new IllegalArgumentException("Username cannot be null or blank");}
        if (username.length() > 20){throw new IllegalArgumentException("Username cannot be longer than 20 characters");}
        this.username = username.trim();
    }
    public void setEmail(String email) {
        if (email == null || email.isBlank()){throw new IllegalArgumentException("Email cannot be null or blank");}
        if (email.length() > 75){throw new IllegalArgumentException("Email cannot be longer than 75 characters");}
        this.email = email.trim();
    }











}
