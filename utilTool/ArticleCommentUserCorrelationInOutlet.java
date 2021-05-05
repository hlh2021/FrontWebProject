package utilTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import struct.OutletPercentageJsonData;

public class ArticleCommentUserCorrelationInOutlet {
	
	public static void main(String[] args)
	{
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return;
		ArticleCommentUserCorrelationInOutlet cor = new ArticleCommentUserCorrelationInOutlet();
		//get article, comment, user percentage of top outlet in each story 
		String outlet = "wsj, Wall Street Journal (blog)";
		String displayOutlet = "WSJ";
		cor.getPercentageInOutlet(outlet, displayOutlet, con);
		
	}
	
	public void getPercentageInOutlet(String outlet, String displayOutlet, Connection con)
	{
		List<OutletPercentageJsonData> articlelist = new ArrayList<OutletPercentageJsonData>();
		List<OutletPercentageJsonData> commentlist = new ArrayList<OutletPercentageJsonData>();
		List<OutletPercentageJsonData> userlist = new ArrayList<OutletPercentageJsonData>();
		String str = "";
		
		//split the outlets
		String[] outletArray = outlet.split(", ");
		String outletStr = "";
		for(int i=0;i<outletArray.length;i++)
			outletStr = outletStr+"'"+outletArray[i]+"',";
		outletStr = outletStr.substring(0, outletStr.length()-1);
		//get total user for all stories
		ArrayList<String[]> storyTotalList = readTotalUserFromDisk("D:/project/workspace/FrontWebProject/WebContent/json/User_Total.json");
		
		try{
			//get story list
			ArrayList<long[]> storyList = getTotalCommentNumber(con);
			for(int i=0; i<storyList.size(); i++)
			{
				long[] record = storyList.get(i);
				//calculate article, comment, user percentage for this story
				long storyID = record[0];
				System.out.println("Story ID: " + storyID);
//				str = str + storyID + ",";
				/***
				long totalCom = record[1];
				long comInOutlet = getCommentNumberByOutlet(storyID, outletStr, con);
				double comPercentage = (double)comInOutlet / (double)totalCom * 100;
				
				long totalArticle = getArticlesWithComment(storyID, con);
				long articleInOutlet = getArticleNumberByOutlet(storyID, outletStr, con);
				double articlePercentage = (double)articleInOutlet / (double)totalArticle * 100;
				**/
//				long totalUser = getTotalUserNumber(storyID, con);
				double totalUser = findTotalUser(storyTotalList, storyID);
				long userInOutlet = getUserNumberByOutlet(storyID, outletStr, con);
				double userPercentage = (double)userInOutlet / (double)totalUser * 100;
				
				//keep each percentage
				/***
				OutletPercentageJsonData articleJson = new OutletPercentageJsonData();
				articleJson.setStoryID(storyID);
				articleJson.setPercentage(articlePercentage);
				articlelist.add(articleJson);
				
				OutletPercentageJsonData comJson = new OutletPercentageJsonData();
				comJson.setStoryID(storyID);
				comJson.setPercentage(comPercentage);
				commentlist.add(comJson);
				**/
				OutletPercentageJsonData userJson = new OutletPercentageJsonData();
				userJson.setStoryID(storyID);
				userJson.setPercentage(userPercentage);
				userlist.add(userJson);
//							
			}
//			System.out.println(str);
			
			//write json string
			Util util = new Util();
			/**
			String articleStr = util.ReturnJson(articlelist);
			util.WriteFile("Article"+"_"+displayOutlet, articleStr);
			String comStr = util.ReturnJson(commentlist);
			util.WriteFile("Comment"+"_"+displayOutlet, comStr);
			**/
			String userStr = util.ReturnJson(userlist);
			util.WriteFile("User"+"_"+displayOutlet, userStr);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	private ArrayList<long[]> getTotalCommentNumber(Connection con) throws SQLException
	{
		ArrayList<long[]> storyList = new ArrayList<long[]>();
		
		String sql = "SELECT t.NewsStoryID, sum(s.EndCrawlCommentCount) "
				+ "FROM articlestatistics s, newsarticle a, newsline l, newsstory t  "
				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and t.NewsStoryID=l.NewsStoryID "
//				+ "and t.StartTime>'2016-01-06' "
				+ "group by t.NewsStoryID order by sum(s.EndCrawlCommentCount) desc";
		PreparedStatement statement = con.prepareStatement(sql);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
		{
			if(rs.getLong(2) > 100)  //keep story more than 100 comments
//			if(rs.getLong(2) > 1000) //keep story more than 1000 comments
			{
				long[] record = new long[2];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong(2);
				storyList.add(record);
			}				
		}			
		
		if(rs != null)
			rs.close();
		if(statement != null)
			statement.close();
		
		return storyList;
	}
	
	private long getCommentNumberByOutlet(long storyID, String outletStr, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT sum(s.EndCrawlCommentCount) FROM articlestatistics s, newsarticle a, newsline l, newsoutlets o "
				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=? "
				+ "and a.NewsOutletID=o.NewsOutletID and o.Name in (" + outletStr + ")";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		if(rs != null)
			rs.close();
		if(statement != null)
			statement.close();
		
		return count;
	}
	
	private long getArticlesWithComment(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(*) FROM newsarticle a, newsline l "
				+ "where a.HasComments=1 and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
//		System.out.println("count the articles with comments....");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	private long getArticleNumberByOutlet(long storyID, String outletStr, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(*) FROM newsarticle a, newsline l, newsoutlets o "
				+ "where a.NewsLineID=l.NewsLineID and a.HasComments=1 and l.NewsStoryID=? "
				+ "and a.NewsOutletID=o.NewsOutletID and o.Name in (" + outletStr + ")";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
//		System.out.println("count the articles from specified outlet....");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	private long getTotalUserNumber(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(distinct(AuthorName)) FROM comments c, newsarticle a, newsline l "
				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
//		System.out.println("searching the user number for story "+storyID);
		ResultSet rs = statement.executeQuery();
	
		while(rs.next())
			count = rs.getLong(1);
//		System.out.println("user number is "+count);
		return count;
	}
	
	private long getUserNumberByOutlet(long storyID, String outletStr, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(distinct(c.AuthorName)) FROM comments c, newsarticle a, newsline l, newsoutlets o "
				+ "where l.NewsStoryID=? and o.Name in (" + outletStr + ") "
				+ "and c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and a.NewsOutletID=o.NewsOutletID";
		
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	public ArrayList<String[]> readTotalUserFromDisk(String fileName)
	{
		ArrayList<String[]> storyList = new ArrayList<String[]>();
		//read the json file
		File file = new File(fileName);
		FileReader fileReader = null;  
        BufferedReader bufferedReader = null; 
        String content = "";
        try{
        	fileReader = new FileReader(file);  
            bufferedReader = new BufferedReader(fileReader);             
            String line = bufferedReader.readLine();
            while(line != null && line.length() > 0)
            {    
            	content = content + line;
            	line = bufferedReader.readLine();
            }
            
            content = "{\"records\":" + content + "}";
            JSONObject obj = new JSONObject(content);
            JSONArray array = obj.getJSONArray("records");
            for(int i = 0; i < array.length(); i++)
            {
            	JSONObject storyObj = (JSONObject) array.get(i);
            	String[] record = new String[2];
            	record[0] = String.valueOf(storyObj.getLong("storyID"));
            	record[1] = String.valueOf(storyObj.getDouble("percentage"));
            	
            	storyList.add(record);
            }
            
            
        }catch(Exception e){
			System.out.println("Fail to read json file!\n"+e);
		}finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
                if (fileReader != null) {  
                    fileReader.close();  
                }  
            } catch (IOException e) {  
            	System.out.println("Fail to close reader!\n"+e);
            }  
        } 
        
        return storyList;
	}
	
	public double findTotalUser(ArrayList<String[]> storyList, long storyID)
	{
		double totalUser = 0;
		for(String[] story : storyList)
		{
			long id = Long.parseLong(story[0]);
			if(id == storyID)
				totalUser = Double.parseDouble(story[1]);
		}
		return totalUser;
	}

}
