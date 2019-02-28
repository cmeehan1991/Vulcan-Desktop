/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class User {

    private final String URL_STRING = "http://www.meehanwoodworking.com/vulcan/classes/Users.php";
    private final String ENC = "UTF-8";

    public boolean signIn(String username, String password) {
        try {
            // Create the URL connection
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            // Set the data
            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("sign_in", ENC);
            data += "&" + URLEncoder.encode("username", ENC) + "=" + URLEncoder.encode(username, ENC);
            data += "&" + URLEncoder.encode("password", ENC) + "=" + URLEncoder.encode(password, ENC);
            
            // Write to the stream
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            // Parse the response
            JSONObject jsonObject = new JSONObject(line);
            
            if (!jsonObject.getBoolean("success")) {
                return false;
            }
            if (!jsonObject.getString("ID").isEmpty() && jsonObject.getBoolean("success")) {
                System.setProperty("USER_ID", jsonObject.getString("ID"));
                System.setProperty("LOGGED_IN", "true");
                return true;
            }
            
            return false;            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }
}
