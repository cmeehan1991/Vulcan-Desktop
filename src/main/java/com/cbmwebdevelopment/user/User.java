/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import com.cbmwebdevelopment.connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author cmeehan
 */
public class User {

    /**
     * Log the user in if the user exists.
     * 
     * @param username
     * @param password
     * @return 
     */
    public boolean signIn(String username, String password) {
        String sql = "SELECT ID FROM USERS WHERE (USERNAME = ? OR EMAIL = ?) AND PASSWORD = MD5(?)";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String id = rs.getString("ID");
                if(!id.isEmpty()){
                    System.setProperty("USER_ID", id);
                    System.setProperty("USER_IS_LOGGED_IN", "true");
                    return true;
                }
            }            
            return false;            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }
}
