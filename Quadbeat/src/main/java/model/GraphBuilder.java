package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GraphBuilder {
    private List<Song> songs;
    private Map<String, List<Edge>> songBasedGraph;
    private Map<String, List<Edge>> artistBasedGraph;
    private Map<String, List<Edge>> moodBasedGraph;

    public GraphBuilder() {
        songs = new ArrayList<>();
        songBasedGraph = new HashMap<>();
        artistBasedGraph = new HashMap<>();
        moodBasedGraph = new HashMap<>();
    }

    public void loadSongsFromCSV(String csvFilePath) {
        System.out.println("\n=== Loading Songs Dataset ===");
        System.out.println("Attempting to load from: " + csvFilePath);
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                try {
                    // Use a more robust CSV parsing approach
                    String[] values = parseCSVLine(line);
                    if (values.length >= 9) {
                        String mood = values[6].trim();
                        Song song = new Song(
                            values[0].trim(),                       // song_id
                            values[1].trim(),                       // title
                            values[2].trim(),                       // artist
                            values[3].trim(),                       // language
                            values[4].trim(),                       // genre
                            Integer.parseInt(values[5].trim()),     // release_year
                            mood,                                   // mood
                            Double.parseDouble(values[7].trim()),   // tempo
                            Double.parseDouble(values[8].trim())    // popularity
                        );
                        songs.add(song);
                        
                        // Initialize graph nodes
                        songBasedGraph.put(song.getSongId(), new ArrayList<>());
                        artistBasedGraph.put(song.getSongId(), new ArrayList<>());
                        moodBasedGraph.put(song.getSongId(), new ArrayList<>());
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                    System.err.println("Error message: " + e.getMessage());
                }
            }
            
            System.out.println("Successfully loaded " + songs.size() + " songs from CSV.");
            // Print first few songs as sample
            System.out.println("\nSample of loaded songs:");
            songs.stream().limit(3).forEach(song -> 
                System.out.println("- " + song.getTitle() + " by " + song.getArtist() + 
                                 " (ID: " + song.getSongId() + ", Mood: " + song.getMood() + ")")
            );
            System.out.println("=== Dataset Loading Complete ===\n");
            
        } catch (IOException e) {
            System.err.println("Error loading dataset: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String[] parseCSVLine(String line) {
        // Remove unused variables
        int currentFieldIndex = 0;
        int expectedFields = 9;
        
        String[] fields = new String[expectedFields];
        StringBuilder sb = new StringBuilder();
        
        // Simple field splitting (works for most cases)
        String[] rawFields = line.split(",");
        
        // Handle cases where fields have quotes
        for (int i = 0; i < rawFields.length && currentFieldIndex < expectedFields; i++) {
            String field = rawFields[i].trim();
            
            // If the field starts with quote but doesn't end with quote, it spans multiple columns
            if (field.startsWith("\"") && !field.endsWith("\"")) {
                sb.append(field.substring(1));  // Remove starting quote
                
                // Continue reading until we find the ending quote
                while (++i < rawFields.length) {
                    sb.append(",").append(rawFields[i]);
                    if (rawFields[i].endsWith("\"")) {
                        // Remove ending quote
                        sb.setLength(sb.length() - 1);
                        break;
                    }
                }
                fields[currentFieldIndex++] = sb.toString();
                sb.setLength(0);
            } else if (field.startsWith("\"") && field.endsWith("\"")) {
                // Field is completely quoted
                fields[currentFieldIndex++] = field.substring(1, field.length() - 1);
            } else {
                // Normal field
                fields[currentFieldIndex++] = field;
            }
        }
        
        return fields;
    }

    public void buildSongBasedGraph() {
        // Build Song-Based Graph
        for (int i = 0; i < songs.size(); i++) {
            Song song1 = songs.get(i);
            
            for (int j = i + 1; j < songs.size(); j++) {
                Song song2 = songs.get(j);
                double weight = 0.0;
                
                // Same genre → +0.4
                if (song1.getGenre().equals(song2.getGenre())) {
                    weight += 0.4;
                }
                
                // Tempo difference ≤ 10 BPM → +0.3
                if (Math.abs(song1.getTempo() - song2.getTempo()) <= 10) {
                    weight += 0.3;
                }
                
                // Same mood → +0.2
                if (song1.getMood().equals(song2.getMood())) {
                    weight += 0.2;
                }
                
                // Popularity difference ≤ 0.1 → +0.1
                if (Math.abs(song1.getPopularity() - song2.getPopularity()) <= 0.1) {
                    weight += 0.1;
                }
                
                // Only add edge if weight is at least 0.3
                if (weight >= 0.3) {
                    songBasedGraph.get(song1.getSongId()).add(new Edge(song2.getSongId(), weight));
                    songBasedGraph.get(song2.getSongId()).add(new Edge(song1.getSongId(), weight));
                }
            }
        }
    }

    public void buildArtistBasedGraph() {
        // Build Artist-Based Graph
        for (int i = 0; i < songs.size(); i++) {
            Song song1 = songs.get(i);
            
            for (int j = i + 1; j < songs.size(); j++) {
                Song song2 = songs.get(j);
                double weight = 0.0;
                
                // Same artist → +0.6
                if (song1.getArtist().equals(song2.getArtist())) {
                    weight += 0.6;
                }
                
                // Same genre → +0.2
                if (song1.getGenre().equals(song2.getGenre())) {
                    weight += 0.2;
                }
                
                // Same mood → +0.1
                if (song1.getMood().equals(song2.getMood())) {
                    weight += 0.1;
                }
                
                // Tempo difference ≤ 10 BPM → +0.1
                if (Math.abs(song1.getTempo() - song2.getTempo()) <= 10) {
                    weight += 0.1;
                }
                
                // Only add edge if weight is at least 0.3
                if (weight >= 0.3) {
                    artistBasedGraph.get(song1.getSongId()).add(new Edge(song2.getSongId(), weight));
                    artistBasedGraph.get(song2.getSongId()).add(new Edge(song1.getSongId(), weight));
                }
            }
        }
    }

    public void buildMoodBasedGraph() {
        // Build Mood-Based Graph
        for (int i = 0; i < songs.size(); i++) {
            Song song1 = songs.get(i);
            
            for (int j = i + 1; j < songs.size(); j++) {
                Song song2 = songs.get(j);
                double weight = 0.0;
                
                // Same mood → +0.5
                if (song1.getMood().equals(song2.getMood())) {
                    weight += 0.5;
                }
                
                // Similar tempo (±10 BPM) → +0.2
                if (Math.abs(song1.getTempo() - song2.getTempo()) <= 10) {
                    weight += 0.2;
                }
                
                // Same genre → +0.2
                if (song1.getGenre().equals(song2.getGenre())) {
                    weight += 0.2;
                }
                
                // Popularity difference ≤ 0.1 → +0.1
                if (Math.abs(song1.getPopularity() - song2.getPopularity()) <= 0.1) {
                    weight += 0.1;
                }
                
                // Only add edge if weight is at least 0.3
                if (weight >= 0.3) {
                    moodBasedGraph.get(song1.getSongId()).add(new Edge(song2.getSongId(), weight));
                    moodBasedGraph.get(song2.getSongId()).add(new Edge(song1.getSongId(), weight));
                }
            }
        }
    }

    public int getSongCount() {
        return songs.size();
    }

    public int getEdgeCount(Map<String, List<Edge>> graph) {
        // Count total edges and divide by 2 (since it's an undirected graph)
        int totalEdges = 0;
        for (List<Edge> edges : graph.values()) {
            totalEdges += edges.size();
        }
        return totalEdges / 2;
    }

    public void printGraphStats() {
        // Print number of nodes and edges for each graph
        System.out.println("Song-Based Graph:");
        System.out.println("  Nodes: " + songBasedGraph.size());
        System.out.println("  Edges: " + getEdgeCount(songBasedGraph));
        printExampleConnections(songBasedGraph, 3);
        
        System.out.println("\nArtist-Based Graph:");
        System.out.println("  Nodes: " + artistBasedGraph.size());
        System.out.println("  Edges: " + getEdgeCount(artistBasedGraph));
        printExampleConnections(artistBasedGraph, 3);
        
        System.out.println("\nMood-Based Graph:");
        System.out.println("  Nodes: " + moodBasedGraph.size());
        System.out.println("  Edges: " + getEdgeCount(moodBasedGraph));
        printExampleConnections(moodBasedGraph, 3);
    }

    public void printExampleConnections(Map<String, List<Edge>> graph, int count) {
        int printed = 0;
        
        // Create a copy of entries to safely iterate
        List<Map.Entry<String, List<Edge>>> entries = new ArrayList<>(graph.entrySet());
        
        for (Map.Entry<String, List<Edge>> entry : entries) {
            List<Edge> edges = entry.getValue();
            if (!edges.isEmpty() && printed < count) {
                String songId = entry.getKey();
                Edge edge = edges.get(0);
                Song sourceSong = getSongById(songId);
                Song targetSong = getSongById(edge.getTargetSongId());
                
                if (sourceSong != null && targetSong != null) {
                    System.out.printf("     %s by %s → %s by %s (weight: %.2f)%n", 
                                     sourceSong.getTitle(), 
                                     sourceSong.getArtist(),
                                     targetSong.getTitle(), 
                                     targetSong.getArtist(),
                                     edge.getWeight());
                    printed++;
                }
            }
            
            if (printed >= count) {
                break;
            }
        }
        
        if (printed == 0) {
            System.out.println("     No connections found with weight >= 0.3");
        }
    }

    public Song getSongById(String songId) {
        for (Song song : songs) {
            if (song.getSongId().equals(songId)) {
                return song;
            }
        }
        return null;
    }

    public Map<String, List<Edge>> getSongBasedGraph() {
        return songBasedGraph;
    }

    public Map<String, List<Edge>> getArtistBasedGraph() {
        return artistBasedGraph;
    }

    public Map<String, List<Edge>> getMoodBasedGraph() {
        return moodBasedGraph;
    }

    // Enums for graph and algorithm types
    public enum GraphType {
        SONG_BASED, ARTIST_BASED, MOOD_BASED
    }

    public enum AlgoType {
        BFS, DIJKSTRA
    }

    /**
     * Recommend songs using the specified algorithm and graph type.
     * Prints output and timing as specified.
     */
    public List<String> recommend(String seedSongId, GraphType type, AlgoType algorithm) {
        Map<String, List<Edge>> graph = null;
        switch (type) {
            case SONG_BASED:
                graph = songBasedGraph;
                break;
            case ARTIST_BASED:
                graph = artistBasedGraph;
                break;
            case MOOD_BASED:
                graph = moodBasedGraph;
                break;
        }
        if (graph == null || !graph.containsKey(seedSongId)) {
            System.out.println("Seed song not found in graph.");
            return Collections.emptyList();
        }
        List<String> recommendations = new ArrayList<>();
        long start = System.currentTimeMillis();
        if (algorithm == AlgoType.BFS) {
            System.out.println("Algorithm: Quick Match (BFS)");
            recommendations = bfsRecommend(graph, seedSongId);
        } else {
            System.out.println("Algorithm: Best Match (Dijkstra)");
            recommendations = dijkstraRecommend(graph, seedSongId);
        }
        long end = System.currentTimeMillis();
        System.out.println("Seed: " + seedSongId);
        System.out.println("Top 10 Recommendations:");
        int idx = 1;
        for (String rec : recommendations) {
            System.out.println(idx + ". " + rec);
            idx++;
        }
        System.out.println("Total time taken: " + (end - start) + " ms");
        return recommendations;
    }

    // BFS-based recommendation (Quick Match)
    private List<String> bfsRecommend(Map<String, List<Edge>> graph, String seedSongId) {
        List<String> recommendations = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.add(seedSongId);
        visited.add(seedSongId);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            for (Edge edge : graph.get(current)) {
                if (!visited.contains(edge.getTargetSongId()) && edge.getWeight() >= 0.73) {
                    recommendations.add(edge.getTargetSongId() + " (weight: " + edge.getWeight() + ")");
                    visited.add(edge.getTargetSongId());
                    queue.add(edge.getTargetSongId());
                }
            }
        }
        
        return recommendations;
    }

    // Dijkstra-based recommendation (Best Match)
    private List<String> dijkstraRecommend(Map<String, List<Edge>> graph, String seedSongId) {
        List<String> recommendations = new ArrayList<>();
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<PathNode> pq = new PriorityQueue<>((a, b) -> 
            Double.compare(a.accWeight, b.accWeight));
        
        // Initialize distances
        for (String songId : graph.keySet()) {
            distances.put(songId, Double.MAX_VALUE);
        }
        distances.put(seedSongId, 0.0);
        
        pq.add(new PathNode(seedSongId, 0.0, new ArrayList<>()));
        
        while (!pq.isEmpty()) {
            PathNode current = pq.poll();
            
            if (current.accWeight > distances.get(current.songId)) {
                continue;
            }
            
            for (Edge edge : graph.get(current.songId)) {
                double newWeight = current.accWeight + edge.getWeight();
                
                if (newWeight < distances.get(edge.getTargetSongId()) && newWeight <= 1.5) {
                    distances.put(edge.getTargetSongId(), newWeight);
                    previous.put(edge.getTargetSongId(), current.songId);
                    
                    List<String> newPath = new ArrayList<>(current.path);
                    newPath.add(current.songId);
                    
                    pq.add(new PathNode(edge.getTargetSongId(), newWeight, newPath));
                    recommendations.add(edge.getTargetSongId() + " (weight: " + newWeight + ")");
                }
            }
        }
        
        return recommendations;
    }

    // Helper class for Dijkstra path
    private static class PathNode {
        String songId;
        double accWeight;
        List<String> path;
        PathNode(String songId, double accWeight, List<String> path) {
            this.songId = songId;
            this.accWeight = accWeight;
            this.path = path;
        }
    }

    public List<Song> getSongsByMood(String mood) {
        System.out.println("\n=== Searching Songs by Mood ===");
        System.out.println("Requested mood: '" + mood + "'");
        System.out.println("Total songs in database: " + songs.size());
        
        // Print unique moods in the dataset
        Set<String> uniqueMoods = songs.stream()
                .map(Song::getMood)
                .collect(Collectors.toSet());
        System.out.println("Available moods in dataset: " + uniqueMoods);
        
        List<Song> songsWithMood = songs.stream()
                .filter(song -> {
                    boolean matches = song.getMood().equalsIgnoreCase(mood);
                    if (matches) {
                        System.out.println("Found matching song: " + song.getTitle() + 
                                         " by " + song.getArtist() + 
                                         " (ID: " + song.getSongId() + 
                                         ", Mood: '" + song.getMood() + "')");
                    }
                    return matches;
                })
                .collect(Collectors.toList());
        
        System.out.println("Total matches found: " + songsWithMood.size());
        System.out.println("=== Mood Search Complete ===\n");
        return songsWithMood;
    }

    public List<Song> searchSongs(String query) {
        System.out.println("\n=== Searching Songs ===");
        System.out.println("Search query: '" + query + "'");
        System.out.println("Total songs in database: " + songs.size());
        
        String searchQuery = query.toLowerCase();
        List<Song> matchingSongs = songs.stream()
                .filter(song -> 
                    song.getTitle().toLowerCase().contains(searchQuery) ||
                    song.getArtist().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());
        
        System.out.println("Found " + matchingSongs.size() + " matching songs");
        matchingSongs.forEach(song -> 
            System.out.println("- " + song.getTitle() + " by " + song.getArtist() + 
                             " (ID: " + song.getSongId() + ")")
        );
        System.out.println("=== Search Complete ===\n");
        
        return matchingSongs;
    }

    public Optional<Song> findExactSong(String title, String artist) {
        System.out.println("\n=== Finding Exact Song ===");
        System.out.println("Title: '" + title + "'");
        System.out.println("Artist: '" + artist + "'");
        
        Optional<Song> song = songs.stream()
                .filter(s -> 
                    s.getTitle().equalsIgnoreCase(title) &&
                    s.getArtist().equalsIgnoreCase(artist))
                .findFirst();
        
        if (song.isPresent()) {
            System.out.println("Found song: " + song.get().getTitle() + " by " + song.get().getArtist());
        } else {
            System.out.println("No exact match found");
        }
        System.out.println("=== Find Complete ===\n");
        
        return song;
    }
} 