package Quadbeat;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaylistService {
    private static final int MAX_PLAYLIST_SIZE = 100; // Maximum number of songs in a playlist
    private final ConcurrentHashMap<String, List<RecommendationDto>> playlists = new ConcurrentHashMap<>();

    public List<RecommendationDto> getPlaylist(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        return playlists.getOrDefault(sessionId, new ArrayList<>());
    }

    public void addToPlaylist(String sessionId, RecommendationDto song) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (song == null) {
            throw new IllegalArgumentException("Song cannot be null");
        }
        if (song.getSongId() == null || song.getSongId().trim().isEmpty()) {
            throw new IllegalArgumentException("Song ID cannot be null or empty");
        }

        List<RecommendationDto> playlist = playlists.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        // Check if song already exists in playlist
        if (playlist.stream().anyMatch(s -> s.getSongId().equals(song.getSongId()))) {
            throw new IllegalStateException("Song already exists in playlist");
        }
        
        // Check playlist size limit
        if (playlist.size() >= MAX_PLAYLIST_SIZE) {
            throw new IllegalStateException("Playlist has reached maximum size of " + MAX_PLAYLIST_SIZE + " songs");
        }
        
        playlist.add(song);
    }

    public void removeFromPlaylist(String sessionId, String songId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (songId == null || songId.trim().isEmpty()) {
            throw new IllegalArgumentException("Song ID cannot be null or empty");
        }

        List<RecommendationDto> playlist = playlists.get(sessionId);
        if (playlist != null) {
            playlist.removeIf(song -> song.getSongId().equals(songId));
        }
    }

    public void clearPlaylist(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        playlists.remove(sessionId);
    }
} 