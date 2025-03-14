package org.example.songsearchengine;

public class Album {
    private String albumId;
    private String name;

    public Album(String albumId, String name) {
        this.albumId = albumId;
        this.name = name;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getName() {
        return name;
    }
}
