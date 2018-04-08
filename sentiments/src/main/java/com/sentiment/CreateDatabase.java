package com.sentiment;

import java.sql.*;

class CreateDatabase{
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";
   
    public static void main(String[] args) throws Exception {
    Connection conn = null;
    Statement stmt = null;
    try {
        Class.forName("com.mysql.jdbc.Driver");

        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, "");

        System.out.println("Creating database...");
        stmt = conn.createStatement();

        String sql = "CREATE DATABASE sentiment_twitter";
        stmt.executeUpdate(sql);
        System.out.println("Database created successfully...");
        String CURRENT_DB_URL = "jdbc:mysql://localhost/sentiment_twitter";
        Class.forName("com.mysql.jdbc.Driver");

        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(CURRENT_DB_URL, USER, "");

        System.out.println("Creating table in given database...");
        stmt = conn.createStatement();

        sql = "CREATE TABLE tweets " +
        "(id INT(11) AUTO_INCREMENT, " +
        " username VARCHAR(255), " + 
        " tweet VARCHAR(255), " + 
        " sentiment VARCHAR(255), " + 
        " type VARCHAR(255), " + 
        " PRIMARY KEY ( id ))"; 

        stmt.executeUpdate(sql);
        System.out.println("Created table in given database...");

    } catch(SQLException se) {
        se.printStackTrace();
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        try{
            if(stmt!=null)
                stmt.close();
        } catch(SQLException se2) {
        }

        try {
            if(conn!=null)
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } //end finally try
    }//end try
    
    System.out.println("Done!");
    
    }
}