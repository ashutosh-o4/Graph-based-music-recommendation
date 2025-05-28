package model;

public class Song {
    private String songId;
    private String title;
    private String artist;
    private String language;
    private String genre;
    private int releaseYear;
    private String mood;
    private double tempo;
    private double popularity;

    public Song(String songId, String title, String artist, String language, String genre, 
               int releaseYear, String mood, double tempo, double popularity) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.language = language;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.mood = mood;
        this.tempo = tempo;
        this.popularity = popularity;
    }

    // Getters
    public String getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
    public int getReleaseYear() { return releaseYear; }
    public String getMood() { return mood; }
    public double getTempo() { return tempo; }
    public double getPopularity() { return popularity; }

    @Override
    public String toString() {
        return "Song{" +
                "songId='" + songId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", mood='" + mood + '\'' +
                ", tempo=" + tempo +
                ", popularity=" + popularity +
                '}';
    }
} 