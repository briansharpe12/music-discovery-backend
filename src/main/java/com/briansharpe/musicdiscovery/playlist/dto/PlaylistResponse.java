package com.briansharpe.musicdiscovery.playlist.dto;
import java.time.LocalDateTime;

public record PlaylistResponse(
        Long id,
        String name,
        String description,
        Long userId,
        String username,
        LocalDateTime createdAt
) { }