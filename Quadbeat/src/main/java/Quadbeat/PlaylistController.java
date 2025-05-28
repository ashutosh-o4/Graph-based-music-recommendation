package Quadbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<?> getPlaylist(@RequestParam String sessionId) {
        try {
            List<RecommendationDto> playlist = playlistService.getPlaylist(sessionId);
            return ResponseEntity.ok(playlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("An error occurred while fetching the playlist"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToPlaylist(@RequestParam String sessionId, @RequestBody RecommendationDto song) {
        try {
            playlistService.addToPlaylist(sessionId, song);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Song added to playlist successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("An error occurred while adding to playlist"));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromPlaylist(@RequestParam String sessionId, @RequestParam String songId) {
        try {
            playlistService.removeFromPlaylist(sessionId, songId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Song removed from playlist successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("An error occurred while removing from playlist"));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearPlaylist(@RequestParam String sessionId) {
        try {
            playlistService.clearPlaylist(sessionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Playlist cleared successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("An error occurred while clearing playlist"));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
} 