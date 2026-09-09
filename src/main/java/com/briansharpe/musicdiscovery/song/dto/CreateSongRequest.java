package com.briansharpe.musicdiscovery.song.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSongRequest (
    @NotBlank
    @Size(max = 200)
    String title,

    @NotNull
    @Positive
    Integer durationSeconds,

    @NotNull
    Long artistId
){ }
