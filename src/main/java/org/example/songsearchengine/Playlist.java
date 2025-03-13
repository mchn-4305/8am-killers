package org.example.songsearchengine;

public class Playlist {
    private String id;
    private String name;
    private String user_id;

    public Playlist(String id, String name, String user_id){
        this.id=id;
        this.name=name;
        this.user_id=user_id;
    }

    public String getId(){
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }
}
