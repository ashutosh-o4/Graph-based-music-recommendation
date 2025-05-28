package  Quadbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommend")
    public ResponseEntity<List<RecommendationDto>> recommend(
            @RequestParam String songId,
            @RequestParam GraphType graphType,
            @RequestParam AlgoType algorithm) {
        long start = System.currentTimeMillis();
        System.out.println("Selected algorithm: " + algorithm);
        System.out.println("Graph type: " + graphType);
        
        // Get recommendations and limit to 10 items
        List<RecommendationDto> recommendations = recommendationService.recommend(songId, graphType, algorithm)
            .stream()
            .limit(10)
            .collect(Collectors.toList());
        
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + " ms");
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(@RequestParam String query) {
        try {
            System.out.println("Searching for: " + query);
            List<RecommendationDto> results = recommendationService.searchSongs(query);
            if (results.isEmpty()) {
                return ResponseEntity.ok()
                    .body(Map.of("message", "No songs found matching your search"));
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.err.println("Search error: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to search songs: " + e.getMessage()));
        }
    }
} 