package com.briansharpe.musicdiscovery.artist.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArtistRequest(
        @NotBlank
        @Size(max = 200)
        String name

)  {}