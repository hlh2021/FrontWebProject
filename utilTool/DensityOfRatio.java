package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.DensityOfRatioJsonData;

public class DensityOfRatio {
	

	public List<DensityOfRatioJsonData> getDensityList(String storyIDStr, String firstTimeStr, String secondTimeStr) throws Exception
	{
		List<DensityOfRatioJsonData> numblist = new ArrayList<DensityOfRatioJsonData>();
		long storyID = Integer.parseInt(storyIDStr);
		
		int interval = 5; //set x axis interval to be 0.02 in graph, after divided by 100
		int steps = 200 / interval;  //the maximum of x axis is 2, , after divided by 100
		//initial the list, set all densities to 0
		for(int i=1; i<=steps; i++)
		{
			DensityOfRatioJsonData json = new DensityOfRatioJsonData();
			json.setRatio(i*interval);  
			json.setDensity(0);
			numblist.add(json);
		}
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist; 		
		
		String divisionStr = "";
		ArrayList<Long> articleIDList = null;
		if(storyID == 0)
		{
			//find all articles that have comments
			articleIDList = findArticlesOrderByComment(con);
			//find articles that have >10 comments
//			articleIDList = findArticlesWithCommentLimit(con);
		}
		else
		{
			//find articles for this story		
			articleIDList = findArticlesByStoryID(storyID, con); 
		}
		
		System.out.println("article size: "+articleIDList.size()+", begin to count the comments for each article...");
		int ignoredNum1 = 0;
		int ignoredNum2 = 0;
		int first5PercentIndex = (int)(articleIDList.size() * 0.05);
		int last10PercentIndex = (int)(articleIDList.size() * 0.9);
		for(int i=0; i<articleIDList.size(); i++)
		{
			long articleID = articleIDList.get(i);
			//find the beginning time
			Timestamp beginningTime = getBeginningTime(articleID, con); 
//			System.out.println("beginning time: " + beginningTime + " , in article " + articleID);
			if(beginningTime == null)
			{
				ignoredNum1++;
				continue;
			}
			//determine the firstTime and secondTime
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(beginningTime);
			calendar.add(Calendar.HOUR, Integer.parseInt(firstTimeStr));
			Timestamp firstTime = new Timestamp(calendar.getTime().getTime());
			calendar.add(Calendar.HOUR, Integer.parseInt(secondTimeStr) - Integer.parseInt(firstTimeStr));
			Timestamp secondTime = new Timestamp(calendar.getTime().getTime());
//			System.out.println("firstTime: " + firstTime + ", secondTime: " + secondTime);
			
			//get the counts in this two time
			ArrayList<Long> count = getCounts(articleID, firstTime, secondTime, con);
			long count1 = count.get(0);
			long count2 = count.get(1);
			//calculate the ratio
			double divid = (double)count2 / (double)count1;			
			double log = Math.log(divid) / Math.log(10);
			divisionStr = divisionStr + divid + ",";
			int index = (int)(log * 100.0) / interval;  //determine the interval where log belong to in the list
//			System.out.println("count1:" + count1 + ", count2:" + count2 + ", log:" + log + ", index:" + index);
			
			if(index >= steps)
			{
				ignoredNum2++;
				continue;   //ignore the ratio that exceed the range 			
			}
						
			//increase the responding density by 1
			int originNumb = numblist.get(index).getDensity();
			numblist.get(index).setDensity(originNumb + 1);
		}
		System.out.println("ignored number: "+ignoredNum1+", "+ignoredNum2);
		divisionStr = divisionStr.substring(0, divisionStr.length()-1);
		util.WriteFile("division_alldata", divisionStr);
		
		return numblist;
	}
	
	
	
	private ArrayList<Long> findArticlesByStoryID(long storyID, Connection con)
	{
		ArrayList<Long> articleIDList = new ArrayList<Long>();
		
		String sql = "SELECT a.ArticleID FROM newsarticle a, newsline l "
				+ "where a.NewsLineID=l.NewsLineID and l.NewsStoryID=? and a.HasComments = true";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, storyID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
				articleIDList.add(rs.getLong(1));
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}		
		
		return articleIDList;
	}
	
	private ArrayList<Long> findArticlesWithCommentLimit(Connection con)
	{
		ArrayList<Long> articleIDList = new ArrayList<Long>();
		//about 77.5% articles
		String sql = "SELECT ArticleID FROM articlestatistics where EndCrawlCommentCount>10";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
				articleIDList.add(rs.getLong(1));
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}		
		
		return articleIDList;
	}
	
	private ArrayList<Long> findArticlesOrderByComment(Connection con)
	{
		ArrayList<Long> articleIDList = new ArrayList<Long>();
		String sql = "SELECT ArticleID, EndCrawlCommentCount FROM articlestatistics order by EndCrawlCommentCount desc";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
				articleIDList.add(rs.getLong(1));
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}		
		return articleIDList;
	}
	
	private ArrayList<ArrayList<Object>> findAllCommentsByArticleID(long articleID, Connection con)
	{
		ArrayList<ArrayList<Object>> commentList = new ArrayList<ArrayList<Object>>();
		
		String sql = "SELECT CommentID, Time FROM comments where ArticleID = ? order by Time";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, articleID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				long commentID = rs.getLong(1);
				Timestamp time = rs.getTimestamp(2);
				ArrayList<Object> record = new ArrayList<Object>();
				record.add(1, commentID);
				record.add(2, time);
				
				commentList.add(record);
			}
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}				
		
		return commentList;
	}
	
	private Timestamp getBeginningTime(long articleID, Connection con)
	{
		Timestamp beginning = null;
		String sql = "SELECT min(Time) FROM comments where ArticleID = ?";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, articleID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				beginning = rs.getTimestamp(1);
			}
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		return beginning;
	}
	
	private ArrayList<Long> getCounts(long articleID, Timestamp firstTime, Timestamp secondTime, Connection con)
	{
		ArrayList<Long> result = new ArrayList<Long>();
		
//		String sql = "select A.count1, B.count2 from "
//			+ "(select count(c.CommentID) as count1 from comments c where c.ArticleID = ? and c.Time<=?) A, "
//			+ "(select count(c.CommentID) as count2 from comments c where c.ArticleID = ? and c.Time<=?) B";
		String sql = "select sum(CASE WHEN c.Time <= ? THEN 1 ELSE 0 END) AS count1,"
				+ "sum(CASE WHEN c.Time <= ? THEN 1 ELSE 0 END) AS count2 "
				+ "from comments c where c.ArticleID = ?";
		
		try{
			PreparedStatement statement = con.prepareStatement(sql);
//			statement.setLong(1, articleID);
//			statement.setTimestamp(2, firstTime);
//			statement.setLong(3, articleID);
//			statement.setTimestamp(4, secondTime);
			statement.setTimestamp(1, firstTime);
			statement.setTimestamp(2, secondTime);
			statement.setLong(3, articleID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				long count1 = rs.getLong(1);
				long count2 = rs.getLong(2);
				result.add(count1);
				result.add(count2);
			}
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		return result;
	}
	

	
	public static void main(String[] args)
	{
		DensityOfRatio d = new DensityOfRatio();
		try{
			d.getDensityList("0", "2", "10");
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
