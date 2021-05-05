package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.CommentJsonData;


public class CommentNumber {
	
	public ArrayList<Object> getCommentNumberOverTime(long storyID) throws Exception
	{
		ArrayList<Object> returnObject = new ArrayList<Object>();
		int storyAppearNumber = 0;
		int storyEndNumber = 0;
		List<CommentJsonData> numblist = new ArrayList<CommentJsonData>();
		
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return returnObject;
				
		//find the appear time and disappear time for this story
		storyID = 1230;
		Timestamp[] time = findTimeforStory(storyID, con);
		Timestamp startTime = time[0];
		Timestamp endTime = time[1];
		System.out.println("storyID: "+storyID+", startTime: "+startTime+", endTime: "+endTime);

		//find all comments and time for this story
		ArrayList<Timestamp> commentTimeList = sortAllCommentTime(storyID, con);
		Timestamp beginingCommentTime = commentTimeList.get(0);
		Timestamp lastCommentTime = commentTimeList.get(commentTimeList.size()-1);
		int intervalHourLowInt = (int)((lastCommentTime.getTime() - beginingCommentTime.getTime()) / (60*60*1000));
		System.out.println("comment size: "+commentTimeList.size()+", beginning: "+beginingCommentTime
				+", end: "+lastCommentTime+", intervalHourLowInt: "+intervalHourLowInt);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(beginingCommentTime);	
		calendar.add(Calendar.HOUR, 1);
		Timestamp compareTime = new Timestamp(calendar.getTime().getTime()); //the first compare time, 1 hour later
		long count = 0;         //keep the cumulative count before the compare time
		int currentHour = 1;	//keep the interval hours	
		for(Timestamp commentTime : commentTimeList)
		{						
			if((commentTime.getTime() - compareTime.getTime()) > 0)
			{
				//large than compare time, add the time and count into numblist	
				CommentJsonData comJson = new CommentJsonData();
				comJson.setTime(String.valueOf(currentHour));
				comJson.setCommentNumber(count);
				numblist.add(comJson);
				System.out.println(currentHour+" , "+count);
				
				if((commentTime.getTime() - compareTime.getTime()) <= (60*1000))
				{
					//large than compare time but limit in 1 hour, increase the compare time by 1 hour	
					calendar.add(Calendar.HOUR, 1);
					compareTime = new Timestamp(calendar.getTime().getTime());
					currentHour++;										
				}
				else
				{
					//large than compare time by more than 1 hour
					calendar.add(Calendar.HOUR, 1);
					compareTime = new Timestamp(calendar.getTime().getTime());
					currentHour++;
					while(commentTime.getTime() > compareTime.getTime())
					{	
						//save the count until currentHour
						CommentJsonData json = new CommentJsonData();
						json.setTime(String.valueOf(currentHour));
						json.setCommentNumber(count);
						numblist.add(json);
						System.out.println(currentHour+" , "+count);
						//increase the compare time by 1 hour
						calendar.add(Calendar.HOUR, 1);
						compareTime = new Timestamp(calendar.getTime().getTime());
						currentHour++;						
					}
				}
				count++;
			}
			else
				count++;			
		}
		//add the last interval and count into list
		CommentJsonData comJson = new CommentJsonData();
		comJson.setTime(String.valueOf(currentHour));
		comJson.setCommentNumber(count);
		numblist.add(comJson);
		
		storyAppearNumber = (int)((startTime.getTime() - beginingCommentTime.getTime()) / (60*60*1000));
		storyEndNumber = (int)((endTime.getTime() - beginingCommentTime.getTime()) / (60*60*1000)) + 1;
		System.out.println("storyAppearNumber: "+storyAppearNumber+", storyEndNumber: "+storyEndNumber);
		
		returnObject.add(numblist);
		returnObject.add(storyAppearNumber);
		returnObject.add(storyEndNumber);
		
		return returnObject;	
	}
	
	private Timestamp[] findTimeforStory(long id, Connection con)
	{
		Timestamp[] time = new Timestamp[2];
		
		String sql = "SELECT StartTime, EndTimeInGoogle FROM newsstory where NewsStoryID=?";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				time[0] = rs.getTimestamp(1);
				time[1] = rs.getTimestamp(2);
			}
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		}catch(Exception e){
			System.out.println("findTimeforStory: "+e);
		}		
		
		return time;
	}
	
	private ArrayList<Timestamp> sortAllCommentTime(long id, Connection con)
	{
		ArrayList<Timestamp> list = new ArrayList<Timestamp>();
		
		String sql = "SELECT c.Time FROM comments c, newsarticle a, newsline l where "
				+ "c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=? "
				+ "and cast(c.Time as time(0)) != '00:00:00'  order by c.Time";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getTimestamp(1));
			}
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		}catch(Exception e){
			System.out.println("sortAllCommentTime: "+e);
		}
		
		return list;
	}
	
	long getCommentNumberUntilTime(long storyID, Timestamp time, Connection con)
	{
		long number = 0;
		String sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=? and comments.ArticleID in "
				+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
				+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?))";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setTimestamp(1, time);
			ps.setLong(2, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong("number");
			}
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		}catch(Exception e){
			System.out.println("getCommentNumberUntilTime: "+e);
		}		
		
		return number;
	}
	
	
	
	
	public ArrayList<Object> getCommentNumberOverTime(String name, String fromDate, String toDate) throws ParseException, NamingException, SQLException
	{
		ArrayList<Object> returnObject = new ArrayList<Object>();
		int storyAppearNumber = 0;
		int storyEndNumber = 0;
		List<CommentJsonData> numblist = new ArrayList<CommentJsonData>();	
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = formatter.parse(fromDate);
		Timestamp time1 = new Timestamp(d1.getTime());				
		Date d2 = formatter.parse(toDate);
		Timestamp time2 = new Timestamp(d2.getTime());
		/**
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		**/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return returnObject;
		
		//find the story
		long storyID = 0;
		Timestamp startTime = time1;
		Timestamp endTime = time2;
		String sql = "SELECT * FROM newsstory where Name=? and StartTime>=? and EndTimeInGoogle<=?";
		PreparedStatement StoryStatement = con.prepareStatement(sql);
		StoryStatement.setString(1, name);
		StoryStatement.setTimestamp(2, time1);
		StoryStatement.setTimestamp(3, time2);
		ResultSet re = StoryStatement.executeQuery();
		while(re.next()){
			storyID = re.getLong(1);
			startTime = re.getTimestamp(4);
			endTime = re.getTimestamp(5);
		}
		if(re != null)
			re.close();
		if(StoryStatement != null)
			StoryStatement.close();
		System.out.println("story startTime: "+startTime);
		System.out.println("story endTime: "+endTime);
		
		Date commentAppearDate = d1;
		if(storyID > 0)
		{
			//this story exists
			ArrayList<String> dateList = getCommentDate(storyID, con);
			commentAppearDate = formatter.parse(dateList.get(0));
			System.out.println("comment appears at: "+commentAppearDate);
			for(int i=0; i<dateList.size(); i++)
			{
				String date = dateList.get(i);
				commentAppearDate = formatter.parse(date);
				CommentJsonData comJson = new CommentJsonData();
				comJson.setTime(date);
				//get the comment number in this date
				long totalNumber = getCommentNumberUntilDate(con, storyID, date);			
				comJson.setCommentNumber(totalNumber);				
				
				numblist.add(comJson);	
				
				//determine the interval days from story to first comment
				double eachDay = 24*60*60;
				double intervalSeconds1 = (double)(startTime.getTime() - commentAppearDate.getTime()) / (double)1000;
				if(intervalSeconds1 >= eachDay)
					storyAppearNumber = i+1;
				double intervalSeconds2 = (double)(endTime.getTime() - commentAppearDate.getTime()) / (double)1000;
				if(intervalSeconds2 >= eachDay)
					storyEndNumber = i+1;
			}
		}
		
		/***
		//determine the story appearing area in the graph
		double intervalSeconds1 = (double)(startTime.getTime() - commentAppearDate.getTime()) / (double)1000;
		double intervalSeconds2 = (double)(endTime.getTime() - commentAppearDate.getTime()) / (double)1000;
		double eachDay = 24*60*60;
		storyAppearNumber = (int)(intervalSeconds1/eachDay);
		storyEndNumber = (int)(intervalSeconds2/eachDay);
		if(storyAppearNumber < 0)
			storyAppearNumber = 0;
		if(storyEndNumber < 0)
			storyEndNumber = 0;
		***/
		returnObject.add(numblist);
		returnObject.add(storyAppearNumber);
		returnObject.add(storyEndNumber);
		
		return returnObject;		
	}
	

	private ArrayList<String> getCommentDate(long storyID, Connection con)
	{
		ArrayList<String> dateList = new ArrayList<String>();
		PreparedStatement statement;
		try{
			String datesql = "SELECT distinct DATE(comments.Time) as date FROM comments where comments.ArticleID in "
					+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
					+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?)) order by comments.Time";
			statement = con.prepareStatement(datesql);
			statement.setLong(1, storyID);
			ResultSet re = statement.executeQuery();
			while(re.next()){
				dateList.add(re.getDate("date").toString());
			}
			if(re != null)
				re.close();
			if(statement != null)
				statement.close();
		}catch(Exception e){
			System.out.println("Fail to select distict date for comments\n"+e);
		}
				
		return dateList;
	}
	
	long getCommentNumberUntilDate(Connection con, long storyID, String date)
	{
		long number = 0;
		String sql = "SELECT count(*) as 'number' FROM comments where DATE(comments.Time)<=? and comments.ArticleID in "
				+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
				+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?))";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, date);
			ps.setLong(2, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong("number");
			}
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		}catch(Exception e){
			System.out.println("getCommentNumberUntilDate: "+e);
		}		
		
		return number;
	}
	
	
	long getCommentNumber(Connection con, long storyID, String date)
	{
		long number = 0;
		String sql = "SELECT count(*) as 'number' FROM comments where DATE(comments.Time)=? and comments.ArticleID in "
				+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
				+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?))";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, date);
			ps.setLong(2, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong("number");
			}
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
		}catch(Exception e){
			System.out.println("getCommentNumber: "+e);
		}		
		
		return number;
	}

}
