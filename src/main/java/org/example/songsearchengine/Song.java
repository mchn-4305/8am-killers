package org.example.songsearchengine;

public class Song {
    private String id;
    private String name;
    private String artist;

    public Song(String id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    public Song(String id, String name){
        this.id = id;
        this.name = name;
        this.artist = "";
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }
}