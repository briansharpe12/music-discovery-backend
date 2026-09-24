package com.briansharpe.musicdiscovery.playlist.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlaylistRequest(
        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 500)
        String description
) { }