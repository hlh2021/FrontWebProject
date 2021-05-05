package utilTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import com.google.gson.Gson;

import struct.ContinuedTimeJsonData;


public class ContinuedTime {
	
	/*** the time difference (in hour) between last comment and story disappears from Google News for each story ***/
	public List<ContinuedTimeJsonData> getContinuedHourList(String sinceDate, int fromHour, int toHour, int bucketHours) throws Exception
	{
		Map<Integer, Integer> lastMap = new TreeMap<Integer, Integer>();
		
		int i = 0;  //the first bucket hour
		if(fromHour % bucketHours == 0)
			i = fromHour;
		else
			i = (fromHour / bucketHours + 1) * bucketHours;		
		for(; i<toHour; i=i+bucketHours)
			lastMap.put(i, 0);  //initial the count to 0
		lastMap.put(i, 0);  //keep the last bucket hour

		List<ContinuedTimeJsonData> numblist = new ArrayList<ContinuedTimeJsonData>();	
		/***		
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;
		
		String hourStr = "";
		
		//find all the stories
		String storySql = "SELECT * FROM newsstory where newsstory.StartTime>?";
		PreparedStatement storyStatement = con.prepareStatement(storySql);
		storyStatement.setString(1, sinceDate);
		System.out.println("search the storis in getContinuedHourList .... ");
		ResultSet rs = storyStatement.executeQuery();
		while(rs.next())
		{
			long storyID = rs.getLong(1);
			Timestamp storyEndtime = rs.getTimestamp(5);
			// get the end time of comments for this story
			Timestamp commentEndtime = findEndCommentTimeByStoryID(storyID, con);
			if(commentEndtime == null)
				commentEndtime = storyEndtime;  // if there is no comment for this story
			
			// calculate the continued hours
			int lastHours = 0;
			if(commentEndtime.getTime() > storyEndtime.getTime())
			{
				double lastSeconds = (double)(commentEndtime.getTime() - storyEndtime.getTime()) / (double)1000;
				double eachHour = 60*60;
				if(lastSeconds % eachHour == 0)
					lastHours = (int)(lastSeconds / eachHour);
				else
					lastHours = (int)(lastSeconds / eachHour) + 1;
			}
			if(lastHours >= 1 && lastHours <= 720)
				hourStr = hourStr + lastHours + ",";
			
			//find the proper bucket for this lastHours in lastMap
			int properBucketHour = 0;
			if(lastHours % bucketHours == 0)
				properBucketHour = lastHours;
			else
				properBucketHour = (lastHours / bucketHours + 1) * bucketHours;			
			
			// keep the lastHours in lastMap, ignore the story whose last comment time exceeds toHour
			if(lastMap.containsKey(properBucketHour))
			{
				int count = lastMap.get(properBucketHour);
				count += 1;
				lastMap.put(properBucketHour, count);
			}
			
					
		}// end of each story
		hourStr = hourStr.substring(0, hourStr.length()-1);
		util.WriteFile("continuedHour1_720", hourStr);
		
		// keep the lastHours in json format
		Iterator it = lastMap.keySet().iterator();
		while (it.hasNext()) 
		{
			int hours = Integer.parseInt(it.next().toString());
			int number = lastMap.get(hours);
			ContinuedTimeJsonData duarJson = new ContinuedTimeJsonData();
			duarJson.setLastDays(hours);   //the continued hours
			duarJson.setStoryNumber(number);
			numblist.add(duarJson);
			System.out.println(hours+" , "+number);
		}
				
		con.close();
		
		return numblist;
	}

	/*** the time difference (in day) between last comment and story disappears from Google News for each story ***/
	public List<ContinuedTimeJsonData> getContinuedDayList(String sinceDate) throws Exception
	{
		List<ContinuedTimeJsonData> numblist = new ArrayList<ContinuedTimeJsonData>();	
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;
		
		String storySql = "SELECT * FROM newsstory where newsstory.StartTime>?";
		PreparedStatement storyStatement = con.prepareStatement(storySql);
		storyStatement.setString(1, sinceDate);
		System.out.println("search the storis in getContinuedDayList .... ");
		ResultSet rs = storyStatement.executeQuery();
		
		Map<Integer, Integer> lastMap = new TreeMap<Integer, Integer>();
//		lastMap.put(0, 0);  //initial to 0
		lastMap.put(1, 0);   //change the first value to be 1
		int maxLast = 1;		
		int numberOfZero = 0;  //the continued interest time for story is 0 day

		while(rs.next())
		{
			long storyID = rs.getLong(1);
			Timestamp storyEndtime = rs.getTimestamp(5);
			
			// get the end time of comments for this story
			Timestamp commentEndtime = findEndCommentTimeByStoryID(storyID, con);
			if(commentEndtime == null)
				commentEndtime = storyEndtime;  // if there is no comment for this story
			
			// calculate the last time
			int lastDays = 0;
			if(commentEndtime.getTime() > storyEndtime.getTime())
			{
				double lastSeconds = (double)(commentEndtime.getTime() - storyEndtime.getTime()) / (double)1000;
				double eachDay = 24*60*60;
				lastDays = (int)(lastSeconds/eachDay)+1;
			}
			if(lastDays == 267)
			System.out.println("story: "+storyID+", lastDays: "+lastDays);
			
			if(lastDays == 0)
			{
				numberOfZero++;
				continue;
			}
			
			// increase lastMap until reach current lastDays
			if(lastDays > maxLast)
			{				
				for(int i=maxLast+1; i<=lastDays; i++)
				{
					lastMap.put(i, 0);
				}
				maxLast = lastDays;					
			}
			// keep the lastDays in lastMap
			if(lastMap.containsKey(lastDays))
			{
				int count = lastMap.get(lastDays);
				count += 1;
				lastMap.put(lastDays, count);
			}
			else
				System.out.println("increase map error!");
			
		} // end of each story
		System.out.println("The number of stories whose continued interest time is 0 day: "+numberOfZero);
		
		// keep the lastDays in json format
		Iterator it = lastMap.keySet().iterator();
		while (it.hasNext()) 
		{
			int days = Integer.parseInt(it.next().toString());
			int number = lastMap.get(days);
			ContinuedTimeJsonData duarJson = new ContinuedTimeJsonData();
			duarJson.setLastDays(days);
			duarJson.setStoryNumber(number);
			numblist.add(duarJson);
			System.out.println(days+" , "+number);
		}		
		
		con.close();
		return numblist;
	}
	

	
	// get the end time of comments for this story
	private Timestamp findEndCommentTimeByStoryID(long id, Connection con)
	{
		Timestamp commentEndtime = null;
		try
		{
			String commentSql = "SELECT MAX(s.TimeOfLastComment) FROM articlestatistics s, newsarticle a, newsline l "
					+ "where s.TimeOfLastComment is not null and s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
			PreparedStatement commentStatement = con.prepareStatement(commentSql);
			commentStatement.setLong(1, id);
			ResultSet commentRS = commentStatement.executeQuery();
			while(commentRS.next())
				commentEndtime = commentRS.getTimestamp(1);	
		}catch(Exception e)
		{
			System.out.println(e);
		}			
		
		return commentEndtime;
	}
	
	//get comment number after story disappearance
	private ArrayList<long[]> findCommentNumberByStoryIDTime(String sinceDate, Connection con)
	{
		ArrayList<long[]> storyList = new ArrayList<long[]>();
		String sql = "SELECT s.NewsStoryID, count(c.commentID) as numb, "
				+ "sum(CASE WHEN c.Time <= s.EndTimeInGoogle THEN 1 ELSE 0 END) AS count1, "
				+ "sum(CASE WHEN c.Time>s.EndTimeInGoogle AND c.Time<=DATEADD(day,30,s.EndTimeInGoogle) "
				+ "THEN 1 ELSE 0 END) AS count2 "
				+ "FROM newsstory s, newsline l, newsarticle a, comments c "
				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID "
				+ "and l.NewsStoryID=s.NewsStoryID and s.StartTime>? "
				+ "group by s.NewsStoryID order by s.NewsStoryID;";
		
		try
		{			
			PreparedStatement storyStatement = con.prepareStatement(sql);
			storyStatement.setString(1, sinceDate);
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
			{
				long[] record = new long[4];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong(2);	
				record[2] = rs.getLong(3);	
				record[3] = rs.getLong(4);	
				storyList.add(record);
			}//end of each story
		}catch(Exception e){
			System.out.println("Query error!");
		}
		
		return storyList;
	}
	
	public void calculateCommentAfterDisappear()
	{
		Util util = new Util();
		Connection con = util.dbConnection();
		double averageIncre = 0;
		int storyNum = 0;
		
		//find all the stories
		ArrayList<long[]> storyList = findCommentNumberByStoryIDTime("2016-01-10", con);
		for(long[] story : storyList)
		{
			long storyID = story[0];
			long total = story[1];
			long numComBefore = story[2];
			long numComIn30 = story[3];
			if(numComBefore == 0)
				continue;
			storyNum++;
			double addtionPercent = (double)numComIn30*100 / (double)numComBefore;
			System.out.println(addtionPercent);
			averageIncre = averageIncre + addtionPercent;
		}//end of each story
		averageIncre = averageIncre / (double)storyNum;
		System.out.println("average increment: "+averageIncre);
	}
	
	public static void main(String[] args)
	{
		ContinuedTime ct = new ContinuedTime();
		ct.timeDiff();
	}
	
	public void timeDiff()
	{
		Util util = new Util();
		Connection con = util.dbConnection();
		
		String sql = "select ArticleID, CommentConsistentPublishTime, CommentConsistentRetrievedPublishTime, "
				+ "DATEDIFF(MINUTE, CommentConsistentPublishTime, CommentConsistentRetrievedPublishTime) as dif from newsarticle "
				+ "where CommentConsistentPublishTime is not null and CommentConsistentRetrievedPublishTime is not null "
				+ "and NewsOutletID in (142, 3, 94, 18, 26, 51) "
				+ "and DATEDIFF(MINUTE, CommentConsistentPublishTime, CommentConsistentRetrievedPublishTime)>0 "
				+ "and DATEDIFF(day, CommentConsistentPublishTime, CommentConsistentRetrievedPublishTime)<10;";
		
		try
		{			
			PreparedStatement storyStatement = con.prepareStatement(sql);
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
			{
				
				
			}//end of each story
		}catch(Exception e){
			System.out.println("Query error!");
		}
	}

}
