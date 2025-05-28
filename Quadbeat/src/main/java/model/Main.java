package model;

public class Main {
    public static void main(String[] args) {
        
        String csvPath = "Dataset/songs_dataset.csv";
        String seedSongId = "S003";  

        GraphBuilder builder = new GraphBuilder();
        builder.loadSongsFromCSV(csvPath);
        builder.buildSongBasedGraph();
        builder.buildArtistBasedGraph();
        builder.buildMoodBasedGraph();

         
        builder.recommend(seedSongId, GraphBuilder.GraphType.SONG_BASED, GraphBuilder.AlgoType.BFS);
        System.out.println();
        // Run Dijkstra (Best Match)
        builder.recommend(seedSongId, GraphBuilder.GraphType.SONG_BASED, GraphBuilder.AlgoType.DIJKSTRA);
    }
} 