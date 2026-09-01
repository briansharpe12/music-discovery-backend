package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.artist.ArtistRepository;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    public Song createSong(String title, Integer durationSeconds, Long artistId) {
        Artist songArtist = artistRepository.findById(artistId).orElseThrow(()
                -> new IllegalArgumentException("Artist not found"));

        if (songRepository.existsByTitleIgnoreCaseAndArtistId(title, artistId)) {
            throw new IllegalArgumentException("Song by this artist already exists");
        }

        Song createdSong = new Song(title, durationSeconds, songArtist);

        return songRepository.save(createdSong);
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Song not found with provided id: " + id));
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song updateSong(Long id, String title, Integer durationSeconds, Long artistId) {
        Song updatingSong = songRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Song not found with provided id: " + id));

        Artist artist = artistRepository.findById(artistId).orElseThrow(()
                -> new ResourceNotFoundException("Artist not found with provided id: " + artistId));

        updatingSong.setTitle(title);
        updatingSong.setDurationSeconds(durationSeconds);
        updatingSong.setArtist(artist);

        return songRepository.save(updatingSong);
    }

    public void deleteSongById(Long id) {
        Song deletingSong = songRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Song not found with provided id: " + id));
        songRepository.delete(deletingSong);
    }
}