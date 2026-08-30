package com.briansharpe.musicdiscovery.artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends  JpaRepository<Artist, Long> {
    boolean existsByNameIgnoreCase(String name);
}
