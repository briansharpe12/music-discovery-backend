package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.artist.ArtistRepository;
import org.springframework.stereotype.Service;

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

        Song createdSong =  new Song(title,durationSeconds,songArtist);

        return songRepository.save(createdSong);
    }


}
