package org.example.songsearchengine;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Backend {
    private static final String DB_URL = "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/8amkillers";
    private static final String DB_USER = "8amkillers";
    private static final String DB_PASSWORD = "eightamkillers";

//    hashes passwords to be stored in DB for new users
    public static String hashPassword(String pw){
        return BCrypt.hashpw(pw, BCrypt.gensalt(12));
    }

//    checks passwords input by users with DB pw
    public static boolean checkPassword(String pw, String hashedPw){
        return BCrypt.checkpw(pw,hashedPw);
    }

//    inserts a new user given a username and a password
    public static void insertUser(String username, String enteredPw){
        String ins = "insert into Users (username, user_password) values (?, ?)";
        try{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(ins);

            String hashedPw = hashPassword(enteredPw);

            ps.setString(1, username);
            ps.setString(2, hashedPw);
            ps.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    returns whether a user entered a valid username and/or password
    public static boolean verifyLogin(String username, String enteredPw){
        String sel = "select user_password from users where username = ?";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sel);

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String storedPw = rs.getString("user_password");
                return checkPassword(enteredPw, storedPw);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }

//    searches artists, albums, and songs for tuples where the name is like the query
//    returns a list of items that fit this description
    public static List<String> searchDB(String query){
        List<String> results = new ArrayList<>();

        String sel = "select artist_id as id, name, 'Artists' as table_name from Artists where name like ?" +
                "union" +
                "select album_id as id, album_name as name, 'Albums' as table_name from Albums where album_name like ?" +
                "union" +
                "select song_id as id, song_name as name, 'Songs' as table_name from Songs where song_name like ?";

        try{
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
            throw new RuntimeException(e);
        }
        return results;
    }
}























