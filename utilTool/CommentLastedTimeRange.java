package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.CommentLastedTimeJsonData;

public class CommentLastedTimeRange {
	

	
	/*** the time difference (in hour) between last comment and first comment for each article ***/
	public List<CommentLastedTimeJsonData> getLastedTimeList(int timeRangeIn)
	{
		List<CommentLastedTimeJsonData> numblist = new ArrayList<CommentLastedTimeJsonData>();
		Map<Integer, Integer> lastMap = new TreeMap<Integer, Integer>();
		lastMap.put(1, 0);  //initial to 0, set the continued time to be no less than 1 hour
		int maxLast = 1;
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();		
		***/
		Util util = new Util();
		Connection con = util.mysqlConnection();
		if(con == null)
			return numblist;
		
		//find all articles that have comments
		System.out.println("find all articles that have comments....");
		ArrayList<Long> articleIDList = findAllArticlesWithComment(con);
		System.out.println("article size: "+articleIDList.size()+", begin to calculate the comments time range for each article...");
		int articlesNoComment = 0;
		int articlesOutRange = 0;
		int articlesLastZero = 0;
		int articlesFirstCommentLarger = 0;
		for(long articleID : articleIDList)
		{
			//find the interval hours in comments for this article
			ArrayList<Timestamp> timeList = getTimeByArticleID(articleID, con);
			if(timeList.size() == 0)
			{
				articlesNoComment++;
				continue;
			}
			Timestamp firstCommentTime = timeList.get(0);
			Timestamp lastCommentTime = timeList.get(1);
			if(firstCommentTime==null || lastCommentTime==null)
				continue;
			//use the min of lastCommentTime and storyEndTime to be lastTime
//			Timestamp storyEndTime = findStoryEndTimeByArticleID(articleID, con); //the disappeared time in Google News			
//			Timestamp lastTime = lastCommentTime.getTime() <= storyEndTime.getTime() ? lastCommentTime : storyEndTime;
			//use lastCommentTime to be lastTime
			Timestamp lastTime = lastCommentTime;
			
			//calculate the interval hours
			double eachHour = 60*60;
			double intervalSeconds = (double)(lastTime.getTime() - firstCommentTime.getTime()) / (double)1000;
			if(intervalSeconds < 0)
			{
				articlesFirstCommentLarger++;
				continue;
			}
			int intervalHours = 0;
			if(intervalSeconds % eachHour == 0)
				intervalHours = (int)(intervalSeconds / eachHour);
			else
				intervalHours = (int)(intervalSeconds / eachHour) + 1;
			
			//ignore the article if the intervalHours out of range
			if(intervalHours > timeRangeIn)
			{
				articlesOutRange++;
				System.out.println("articlesOutRange: "+articleID);
				continue;
			}
			
			if(intervalHours == 0)
			{
				articlesLastZero++;
				continue;
			}
			
//			if(intervalHours == 73)
//				System.out.print(articleID+",");
			
			// increase lastMap until reach current intervalHours
			if(intervalHours > maxLast)
			{				
				for(int i=maxLast+1; i<=intervalHours; i++)
				{
					lastMap.put(i, 0);
				}
				maxLast = intervalHours;					
			}
			// keep the intervalHours in lastMap
			if(lastMap.containsKey(intervalHours))
			{
				int count = lastMap.get(intervalHours);
				count += 1;
				lastMap.put(intervalHours, count);
			}
			else
				System.out.println("increase map error!");			
			
		}
		System.out.println("\nThe number of articles without comment but hasComment is true: " + articlesNoComment);
		System.out.println("The number of articles whose first comment is later than disapperd time: " + articlesFirstCommentLarger);
		System.out.println("The number of articles whose comment duration is out of range: " + articlesOutRange);
		System.out.println("The number of articles whose comment duration is 0 hour: " + articlesLastZero);
		
		Iterator it = lastMap.keySet().iterator();
		while (it.hasNext()) 
		{
			int hours = Integer.parseInt(it.next().toString());
			int number = lastMap.get(hours);
			CommentLastedTimeJsonData lastedTimeJson = new CommentLastedTimeJsonData();
			lastedTimeJson.setCommentTimeRange(hours);
			lastedTimeJson.setArticleNumber(number);
			numblist.add(lastedTimeJson);
//			System.out.println(hours+" , "+number);
		}
		
		//close db
		try{
			con.close();
		}catch(Exception e){
			System.out.println("Close db error!");
		}
		
		
		return numblist;
	}
	
	
	private ArrayList<Long> findAllArticlesWithComment(Connection con)
	{
		ArrayList<Long> articleIDList = new ArrayList<Long>();
		
//		String sql = "SELECT a.ArticleID FROM newsarticle a where a.HasComments = 1";
		//for specific outlet: Guardian:26,13990, WSP:9,80, NYT:51,9408, DM:142,8760, WSJ:3,94, Fox:18,3482
		String sql = "SELECT a.ArticleID FROM newsarticle a where a.HasComments = 1 and NewsOutletID in (26,13990)";
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
	
	public ArrayList<Timestamp> getTimeByArticleID(long articleID, Connection con)
	{
		ArrayList<Timestamp> timeList = new ArrayList<Timestamp>();
		
		String sql = "SELECT TimeOfFirstComment, TimeOfLastComment FROM articlestatistics where ArticleID=?";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, articleID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				timeList.add(rs.getTimestamp(1));
				timeList.add(rs.getTimestamp(2));
			}
		}catch(Exception e){
			System.out.println(e);
		}	
		
		return timeList;
	}
	
	private Timestamp findStoryEndTimeByArticleID(long articleID, Connection con)
	{
		Timestamp endTime = null;
		String sql = "select s.EndTimeInGoogle from newsstory s, newsline l, newsarticle a "
				+ "where a.ArticleID=? and a.NewsLineID=l.NewsLineID and l.NewsStoryID=s.NewsStoryID";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, articleID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
				endTime = rs.getTimestamp(1);
		}catch(Exception e){
			System.out.println(e);
		}	
		
		return endTime;
	}
	
	
	public static void main(String[] args)
	{
		CommentLastedTimeRange com = new CommentLastedTimeRange();
		com.getLastedTimeList(6000);
		
	}

}
