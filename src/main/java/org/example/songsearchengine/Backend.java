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
            Class.forName("com.mysql.jdbc.Driver");
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
            Class.forName("com.mysql.jdbc.Driver");
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
            Class.forName("com.mysql.jdbc.Driver");
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

    public static int getCurrentUserId(){
        String sel = "select user_id from Users where username = " + UserSession.getCurrentUser();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("user_id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean insertPlaylist(String playlistTitle) {
        String ins = "insert into Playlsts (user_id, playlist_name) values (?, ?)";
        try{
            Class.forName("com.mysql.jdbc.Driver");
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

    public static int getPlaylistId(String playlistTitle){
        String sel = "select playlist_id from Playlists where playlist_name = ?";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);
            ps.setString(1, playlistTitle);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("playlist_id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean insertPlaylist_Songs(String playlistTitle, int song_id){
        String ins = "insert into Playlist_Songs (playlist_id, song_id) values (?, ?)";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(ins);

            String pid = "" + getPlaylistId(playlistTitle);
            ps.setString(1, pid);
            ps.setString(2, ""+song_id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deletePlaylist_Songs(String playlistTitle, int song_id){
        String del = "delete from Playlist_Songs ps where ps.playlist_name = ? and ps.song_id = ?";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(del);
            ps.setString(1, playlistTitle);
            ps.setString(2, ""+song_id);

            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean editPlaylistTitle(String playlistTitle){
        String up = "update Playlists set playlist_name = ? where playlist_id = ?";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(up);
            ps.setString(1, playlistTitle);
            ps.setString(2, ""+getPlaylistId(playlistTitle));

            ps.executeUpdate();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}























