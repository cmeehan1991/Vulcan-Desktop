/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author cmeehan
 */
public class DBConnection {
    
    private final String REMOTE_DB_USER = "cmeehan_vulcan";
    private final String REMOTE_DB_PASS = "&ll8Ey}CY%{^";
    private final String LOCAL_DB_USER = "root";
    private final String LOCAL_DB_PASS = "root";
    private final String REMOTE_URL = "jdbc:mysql://ns8139.hostgator.com/cmeehan_vulcan?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String LOCAL_URL = "jdbc:mysql://localhost:3306/vulcan";
    private Connection connection;
    public Connection connect(){        
        try{
            connection = DriverManager.getConnection(LOCAL_URL, LOCAL_DB_USER, LOCAL_DB_PASS);
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
        }
        
        return connection;
    }
    
}
