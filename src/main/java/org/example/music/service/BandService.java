package org.example.music.service;

import org.example.music.entity.Band;
import org.example.music.external.LyricsClient;
import org.example.music.repository.BandRepository;
import org.springframework.cache.annotation.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "bands")
public class BandService {

    private final BandRepository repository;
    private final LyricsClient lyricsClient;

    public BandService(BandRepository repository, LyricsClient lyricsClient) {
        this.repository = repository;
        this.lyricsClient = lyricsClient;
    }

    @Cacheable(key = "#id")
    public Band getById(Long id) throws ChangeSetPersister.NotFoundException {
        return repository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    public List<Band> listAll() {
        return repository.findAll();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Band create(Band band) {
        return repository.save(band);
    }

    @Transactional
    @CachePut(key = "#id")
    public Band update(Long id, Band data) throws ChangeSetPersister.NotFoundException {
        Band band = getById(id);
        band.setName(data.getName());
        band.setGenre(data.getGenre());
        band.setFormedIn(data.getFormedIn());
        return repository.save(band);
    }

    @Transactional
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public String getLyrics(Long id, String albumTitle) throws ChangeSetPersister.NotFoundException {
        Band band = getById(id);
        return lyricsClient.fetchLyrics(band.getName(), albumTitle);
    }
}
