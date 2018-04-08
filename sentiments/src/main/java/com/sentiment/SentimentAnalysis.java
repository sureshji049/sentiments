package com.sentiment;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.*;
import com.aylien.textapi.responses.*;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import java.io.*;
import java.sql.*;
import java.util.regex.*;


class SentimentAnalysis {
  // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/";

  //  Database credentials
  static final String USER = "root";
  static final String PASS = "";
  static int postiveSentiment = 0;
  static int neutralSentiment = 0;
  static int negativeSentiment = 0;

  public static void main(String[] args) throws Exception {
    TextAPIClient client = new TextAPIClient("3d147192", "748a193cfa564ecf4f93e33056491103");
    ConfigurationBuilder cb=new ConfigurationBuilder();
    cb.setDaemonEnabled(true);
    cb.setOAuthConsumerKey("3yLZPzGRyV79Egrxl8ZR4AaUU");
    cb.setOAuthConsumerSecret("wy9CfuS2oByXM4EOuXoJzu9wZT56ojHgmeq08DFTg7GztMUrbV");
    cb.setOAuthAccessToken("2913255103-svfLOktDN3Q5OktUXKOaGdWjLbwuhj7LrgcqrSL");
    cb.setOAuthAccessTokenSecret("HGap4EioLl5LvO9ERITPlLNeuokxapcLB2rLGpQxpyorO");
    TwitterFactory tf=new TwitterFactory(cb.build());
    Twitter tw=tf.getInstance();

    Query query = new Query("gst");
    QueryResult result = tw.search(query);
    System.out.println("#################################################################################################");
    System.out.println("Current GST Trends...");
    System.out.println("#################################################################################################");
    System.out.println("=================================================================================================");
    for (Status status : result.getTweets()) {
      System.out.println("@" + status.getUser().getScreenName() + ": " + removeUrl(status.getText().toString()).replaceAll("[^A-Za-z0-9 ,.!]", ""));
      SentimentParams.Builder builder = SentimentParams.newBuilder();
      builder.setText(status.getText());
      Sentiment sentiment = client.sentiment(builder.build());
      System.out.println("Sentiment: " + sentiment);
      try {
       PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("tweets.txt", true)));
        out.println(removeUrl(status.getText().toString()).replaceAll("[^A-Za-z0-9 ,.!]", ""));
        out.close();
        } catch (IOException e) {
      }
      insertIntoTable(status.getUser().getScreenName(), removeUrl(status.getText().toString()).replaceAll("[^A-Za-z0-9 ,.!]", ""), sentiment.toString());                 
      System.out.println("=================================================================================================");
    }
    countSentiments();
    System.out.println("Positive count: " + postiveSentiment);
    System.out.println("Negative count: " + negativeSentiment);
    System.out.println("Neutral count: " + neutralSentiment);
  }

 public static void insertIntoTable(String username, String tweet, String sentiment) throws Exception {

  Connection conn = null;
  Statement stmt = null;
  String CURRENT_DB_URL = "jdbc:mysql://localhost/sentiment_twitter";
  String final_type = " ";
  
  if (sentiment.contains("negative")){
    final_type = "negative";
  }else if (sentiment.contains("positive")){
    final_type = "positive";
  }else if (sentiment.contains("neutral")){
    final_type = "neutral";
  }

  try{
    Class.forName("com.mysql.cj.jdbc.Driver");

    //System.out.println("Connecting to database...");
    conn = DriverManager.getConnection(CURRENT_DB_URL, USER, PASS);

    //System.out.println("Inserting records into the table...");
    stmt = conn.createStatement();

    String sql = "INSERT INTO tweets (username, tweet, sentiment, type) VALUES ('"+ username + "'," + "'"+ tweet + "'," + "'"+ sentiment +"'," + "'"+ final_type +"')";
    stmt.executeUpdate(sql);
    //System.out.println("Inserted records into the table...");

   } catch(SQLException se) {
        //Handle errors for JDBC
        se.printStackTrace();
    } catch(Exception e) {
        //Handle errors for Class.forName
        e.printStackTrace();
    } finally {
        //finally block used to close resources
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
    
   // System.out.println("Done!");
    
    }//end main

public static void countSentiments(){

 Connection conn = null;
   Statement stmt = null;
     String CURRENT_DB_URL = "jdbc:mysql://localhost/sentiment_twitter";

   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");

      //STEP 3: Open a connection
     // System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(CURRENT_DB_URL, USER, PASS);
     // System.out.println("Connected database successfully...");
      
      //STEP 4: Execute a query
     // System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = "SELECT type FROM tweets";
      ResultSet rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
      while(rs.next()){
        
         String sentiment = rs.getString("type");

         if (sentiment.toString().contains("negative")){
     negativeSentiment += 1;
  }else if (sentiment.toString().contains("postive")){
    postiveSentiment += 1;
  }else if (sentiment.toString().contains("neutral")){
     neutralSentiment += 1;
  }
      }
      rs.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            conn.close();
      }catch(SQLException se){
      }// do nothing
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Got count!");
  
}

public static String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }
}