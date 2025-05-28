package model;

public class Edge {
    private String targetSongId;
    private double weight;

    public Edge(String targetSongId, double weight) {
        this.targetSongId = targetSongId;
        this.weight = weight;
    }

    public String getTargetSongId() {
        return targetSongId;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{targetSongId='" + targetSongId + "', weight=" + weight + '}';
    }
} 