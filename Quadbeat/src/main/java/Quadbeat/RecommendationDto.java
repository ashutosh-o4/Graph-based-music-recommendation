package  Quadbeat;

public class RecommendationDto {
    private String songId;
    private String title;
    private String artist;
    private double score;

    public RecommendationDto() {}
    public RecommendationDto(String songId, String title, String artist, double score) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.score = score;
    }
    public String getSongId() { return songId; }
    public void setSongId(String songId) { this.songId = songId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
} 