package org.example.songsearchengine;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Backend {
    private static final String DB_URL = "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/8amkillers";
    private static final String DB_USER = "8amkillers";
    private static final String DB_PASSWORD = "eightamkillers";

//    hashes passwords to be stored in DB for new users
    private static String hashPassword(String pw){
        return BCrypt.hashpw(pw, BCrypt.gensalt(12));
    }

//    checks passwords input by users with DB pw
    private static boolean checkPassword(String pw, String hashedPw){
        return BCrypt.checkpw(pw,hashedPw);
    }

//    inserts a new user given a username and a password
    public static boolean insertUser(String username, String enteredPw){
        String ins = "insert into Users (username, user_password) values (?, ?)";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(ins);

            String hashedPw = hashPassword(enteredPw);

            ps.setString(1, username);
            ps.setString(2, hashedPw);
            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

//    returns whether a user entered a valid username and/or password
    public static boolean verifyLogin(String username, String enteredPw){
        String sel = "select user_password from Users where username = ?";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String storedPw = rs.getString("user_password");
                return checkPassword(enteredPw, storedPw);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

//    searches artists, albums, and songs for tuples where the name is like the query
//    returns a list of items that fit this description
    public static List<String> searchDB(String query){
        List<String> results = new ArrayList<>();

        String sel = "select artist_id as id, name, 'Artists' as table_name from Artists where name like ?" +
                " union " +
                "select album_id as id, album_name as name, 'Albums' as table_name from Albums where album_name like ?" +
                " union " +
                "select song_id as id, song_name as name, 'Songs' as table_name from Songs where song_name like ?";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            String search = "%" + query + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String id = rs.getString("id");
                String name = rs.getString("name");
                String tableName = rs.getString("table_name");

                results.add(tableName + ": " + name + "(ID: " + id + ")");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

//    returns the id of the current user
    public static int getCurrentUserId(){
        String sel = "select user_id from Users where username = ?";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);
            ps.setString(1, UserSession.getCurrentUser());

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("user_id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

//    returns whether a playlist was inserted successfully
    public static boolean insertPlaylist(String playlistTitle) {
        String ins = "insert into Playlists (user_id, playlist_name) values (?, ?)";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(ins);

            String user = "" + getCurrentUserId();
            ps.setString(1, user);
            ps.setString(2, playlistTitle);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    returns the id of a playlist given a playlist's title
    public static String getPlaylistId(String playlistTitle){
        return SceneController.playlistIdMap.get(playlistTitle);
    }

//    returns whether a playlist song was inserted successfully
public static boolean insertPlaylist_Songs(String playlistTitle, int song_id) {
    String ins = "INSERT INTO Playlist_Songs (playlist_id, song_id) VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement ps = conn.prepareStatement(ins)) {


        String pid = getPlaylistId(playlistTitle);
        if (pid == null) {
            System.out.println("Error: Playlist ID not found for " + playlistTitle);
            return false;
        }

        ps.setString(1, pid);
        ps.setInt(2, song_id);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


    //    returns whether a playlist song was deleted successfully
    public static boolean deletePlaylist_Songs(String playlistTitle, int song_id){
        String del = "delete from Playlist_Songs where playlist_id = ? and song_id = ?";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(del);
            ps.setInt(1, Integer.parseInt(getPlaylistId(playlistTitle)));
            ps.setInt(2, song_id);

            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

//    returns whether a playlist title was updated correctly
    public static boolean editPlaylistTitle(String oldPlaylistTitle, String newPlaylistTitle){
        String up = "update Playlists set playlist_name = ? where playlist_id = ?";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(up);
            ps.setString(1, newPlaylistTitle);
            ps.setString(2, getPlaylistId(oldPlaylistTitle));

            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

//    returns a list of all songs within a playlist
    public static List<Song> getAllPlaylistSongs(String playlistName){
        String sel = """
                select s.song_id as song_id, s.song_name as title, a.name as artist
                from Artists a
                inner join Songs s on s.artist_id = a.artist_id
                inner join Playlist_Songs ps on s.song_id = ps.song_id
                where ps.playlist_id = ?""";

        List<Song> results = new ArrayList<>();

        String playlistId = getPlaylistId(playlistName);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);
            ps.setString(1, playlistId);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String id = rs.getString("song_id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");

                results.add(new Song(id, title, artist));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return results;
    }

    public static List<String> getUserPlaylists(){
        String sel = """
                select playlist_id, playlist_name
                from Playlists
                where user_id = ?""";

        List<String> results = new ArrayList<>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);
            ps.setString(1, ""+getCurrentUserId());

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String playlistId = rs.getString("playlist_id");
                String playlistName = rs.getString("playlist_name");
                SceneController.playlistIdMap.put(playlistName, playlistId);
                results.add(playlistName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public static List<Song> getAllAlbumSongs(String albumName) {
        String query = """
            SELECT s.song_id, s.song_name
            FROM Songs s
            INNER JOIN Albums a ON s.album_id = a.album_id
            WHERE a.album_name = ?;
            """;

        List<Song> songs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, albumName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                songs.add(new Song(rs.getString("song_id"), rs.getString("song_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static String getArtistByAlbum(String albumName) {
        String query = """
        SELECT a.name 
        FROM Artists a
        INNER JOIN Albums alb ON a.artist_id = alb.artist_id
        WHERE alb.album_name = ?;
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, albumName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown Artist";
    }

    public static List<String> selectTop20Songs(){
        String sel = """
                select s.song_id, s.song_name, a.name as artist
                from Songs s\s
                inner join Artists a on s.artist_id = a.artist_id
                inner join (
                select s2.song_id
                from Songs s2\s
                inner join Playlist_Songs ps on s2.song_id = ps.song_id
                group by s2.song_id
                order by count(s2.song_id) desc
                limit 20
                ) temp2 on s.song_id = temp2.song_id""";

        List<String> results = new ArrayList<>();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String sid = rs.getString("song_id");
                String sname = rs.getString("song_name");
                String aname = rs.getString("artist");

                results.add(sid + ": " + sname + " by " + aname);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> selectTop20Artists(){
        String sel = """
                select a.name as artist
                from Artists a
                inner join Artist_Songs art_s on a.artist_id = art_s.artist_id
                inner join (
                select s.song_id
                from Songs s
                inner join Playlist_Songs ps on s.song_id = ps.song_id
                group by s.song_id
                order by count(s.song_id) desc
                limit 20
                ) temp on art_s.song_id = temp.song_id""";

        List<String> results = new ArrayList<>();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String aname = rs.getString("artist");
                results.add(aname);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> selectTop20Albums(){
        String sel = """
                select a.album_name as album
                from Albums a
                inner join (
                select s.album_id
                from Songs s
                inner join Playlist_Songs ps on s.song_id = ps.song_id
                group by s.album_id
                order by count(s.album_id) desc
                limit 20
                ) temp on a.album_id = temp.album_id;
                """;

        List<String> results = new ArrayList<>();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String aname = rs.getString("album");
                results.add(aname);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public static List<Song> getAllAvailableSongs() {
        // This should return a list of songs not yet in the playlist
        String query = """
            SELECT s.song_id, s.song_name, a.name AS artist
            FROM Songs s
            INNER JOIN Artists a ON s.artist_id = a.artist_id
            """;

        List<Song> songs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                songs.add(new Song(rs.getString("song_id"), rs.getString("song_name"), rs.getString("artist")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static boolean isAlbum(String name) {
        String query = "SELECT COUNT(*) FROM Albums WHERE album_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSong(String name) {
        String query = "SELECT COUNT(*) FROM Songs WHERE song_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isArtist(String name) {
        String query = "SELECT COUNT(*) FROM Artists WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getAlbumBySong(String songName) {
        String query = """
       SELECT alb.album_name
       FROM Albums alb
       INNER JOIN Songs s ON s.album_id = alb.album_id
       WHERE s.song_name = ?;
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, songName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("album_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Album> getAlbumsByArtist(String artistName) {
        String query = """
        SELECT a.album_id, a.album_name
        FROM Albums a
        INNER JOIN Artists ar ON a.artist_id = ar.artist_id
        WHERE ar.name = ?;
    """;

        List<Album> albums = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, artistName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String albumId = rs.getString("album_id");
                String albumName = rs.getString("album_name");

                albums.add(new Album(albumId, albumName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return albums;
    }

    public static boolean updateUsername(String oldUsername, String newUsername) {
        String query = "UPDATE Users SET username = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, newUsername);
            ps.setString(2, oldUsername);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if update was successful

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updatePassword(String username, String newPassword) {
        String updateQuery = "UPDATE Users SET user_password = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            String hashedPassword = hashPassword(newPassword);
            ps.setString(1, hashedPassword);
            ps.setString(2, username);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean doesUserExist(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, the username already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}























