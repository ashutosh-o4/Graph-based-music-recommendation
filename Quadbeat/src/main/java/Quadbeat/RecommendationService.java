package Quadbeat;

import model.GraphBuilder;
import model.Song;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class RecommendationService {
    private final GraphBuilder graphBuilder;

    public RecommendationService() {
        this.graphBuilder = new GraphBuilder();
        System.out.println("Initializing RecommendationService...");
        graphBuilder.loadSongsFromCSV("../Dataset/songs_dataset.csv");
        graphBuilder.buildSongBasedGraph();
        graphBuilder.buildArtistBasedGraph();
        graphBuilder.buildMoodBasedGraph();
        System.out.println("Initialization complete.");
    }

    public Optional<String> findSongIdByMood(String mood) {
        System.out.println("\n=== Mood-based Song Search ===");
        System.out.println("Input mood: '" + mood + "'");
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        List<Song> songsWithMood = graphBuilder.getSongsByMood(mood);
        System.out.println("Found " + songsWithMood.size() + " songs with mood: " + mood);
        
        if (songsWithMood.isEmpty()) {
            System.out.println("WARNING: No songs found with mood: " + mood);
            return Optional.empty();
        }
        
        // Get the first song with the highest popularity
        Song selectedSong = songsWithMood.stream()
                .max((s1, s2) -> Double.compare(s1.getPopularity(), s2.getPopularity()))
                .get();
        
        System.out.println("Selected seed song: " + selectedSong.getTitle() + " by " + selectedSong.getArtist() + 
                         " (ID: " + selectedSong.getSongId() + ", Mood: " + selectedSong.getMood() + ", Popularity: " + selectedSong.getPopularity() + ")");
        System.out.println("=== End of Mood Search ===\n");
        
        return Optional.of(selectedSong.getSongId());
    }

    public List<RecommendationDto> recommend(String seedSongId, GraphType graphType, AlgoType algoType) {
        System.out.println("\nReceived recommendation request:");
        System.out.println("- Seed Song ID: " + seedSongId);
        System.out.println("- Graph Type: " + graphType);
        System.out.println("- Algorithm: " + algoType);
        
        // For mood-based recommendations, try to find a song with the given mood
        if (graphType == GraphType.MOOD_BASED) {
            String originalSeedSongId = seedSongId;
            seedSongId = findSongIdByMood(seedSongId)
                    .orElseThrow(() -> new IllegalArgumentException("No songs found with mood: " + originalSeedSongId));
            System.out.println("Using seed song ID for mood " + originalSeedSongId + ": " + seedSongId);
        }

        // Get the seed song's title and artist
        Song seedSong = graphBuilder.getSongById(seedSongId);
        String seedTitle = seedSong != null ? seedSong.getTitle() : null;
        String seedArtist = seedSong != null ? seedSong.getArtist() : null;

        // Call the real recommend method from GraphBuilder
        List<String> recs = graphBuilder.recommend(
                seedSongId,
                model.GraphBuilder.GraphType.valueOf(graphType.name()),
                model.GraphBuilder.AlgoType.valueOf(algoType.name())
        );

        System.out.println("Found " + recs.size() + " recommendations");
        
        // Map to RecommendationDto (parse songId and score from recs)
        List<RecommendationDto> recommendations = recs.stream().map(rec -> {
            String[] parts = rec.split(" ");
            String songId = parts[0];
            double score = 0.0;
            if (rec.contains("weight:")) {
                try {
                    score = Double.parseDouble(rec.replaceAll(".*weight: ", "").replace(")", ""));
                } catch (Exception e) { score = 0.0; }
            }
            Song song = graphBuilder.getSongById(songId);
            if (song == null) {
                System.out.println("Warning: Could not find song with ID: " + songId);
                return null;
            }
            return new RecommendationDto(
                songId,
                song.getTitle(),
                song.getArtist(),
                score
            );
        })
        .filter(dto -> dto != null)
        // Filter out exact duplicates and very similar songs
        .filter(dto -> {
            if (seedTitle == null || seedArtist == null) return true;
            
            // Normalize strings for comparison
            String recTitle = dto.getTitle().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String recArtist = dto.getArtist().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String seedNormTitle = seedTitle.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String seedNormArtist = seedArtist.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            
            // Skip if it's the exact same song
            if (recTitle.equals(seedNormTitle) && recArtist.equals(seedNormArtist)) {
                return false;
            }
            
            // Skip if it's a remix/cover of the same song
            if ((recTitle.contains(seedNormTitle) || seedNormTitle.contains(recTitle)) &&
                (recArtist.contains(seedNormArtist) || seedNormArtist.contains(recArtist))) {
                return false;
            }
            
            return true;
        })
        // Remove duplicates within recommendations
        .collect(Collectors.collectingAndThen(
            Collectors.toMap(
                dto -> dto.getTitle().toLowerCase() + "|" + dto.getArtist().toLowerCase(),
                dto -> dto,
                (existing, replacement) -> existing.getScore() > replacement.getScore() ? existing : replacement
            ),
            map -> new ArrayList<>(map.values())
        ));
        
        System.out.println("Returning " + recommendations.size() + " recommendations (excluding duplicates)");
        recommendations.forEach(rec -> 
            System.out.println("- " + rec.getTitle() + " by " + rec.getArtist() + " (Score: " + rec.getScore() + ")")
        );
        
        return recommendations;
    }

    public List<RecommendationDto> searchSongs(String query) {
        System.out.println("\n=== Song Search Request ===");
        System.out.println("Query: " + query);
        
        List<Song> matchingSongs = graphBuilder.searchSongs(query);
        
        // Group songs by title and artist to remove duplicates
        List<RecommendationDto> results = matchingSongs.stream()
            .collect(Collectors.groupingBy(
                song -> song.getTitle() + "|" + song.getArtist(),
                Collectors.maxBy((s1, s2) -> Double.compare(s1.getPopularity(), s2.getPopularity()))
            ))
            .values()
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(song -> new RecommendationDto(
                song.getSongId(),
                song.getTitle(),
                song.getArtist(),
                song.getPopularity()
            ))
            .collect(Collectors.toList());
        
        System.out.println("Returning " + results.size() + " unique search results");
        return results;
    }

    public Optional<String> findSongIdByTitleAndArtist(String title, String artist) {
        return graphBuilder.findExactSong(title, artist)
                .map(Song::getSongId);
    }
} 