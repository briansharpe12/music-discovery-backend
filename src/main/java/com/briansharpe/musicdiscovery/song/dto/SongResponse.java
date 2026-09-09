package com.briansharpe.musicdiscovery.song.dto;
public record SongResponse(
        Long id,
        String title,
        Integer durationSeconds,
        Long artistId,
        String artistName
) { }

