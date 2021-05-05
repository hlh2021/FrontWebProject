package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import struct.CommentJsonData;
import struct.CommentStoryInGoogleNewsJsonData;
import struct.UserCommentJsonData;
import struct.UserVolumeJsonData;

public class testConnectionToDB {
	
	public static void main(String[] args)
	{
		 String DRIVER_CLASS = "com.mysql.jdbc.Driver";      
		 String DATABASE_URL = "jdbc:mysql://localhost:3306/oldnewsdb";   
		 String USER_NAME = "root";    
		 String PASSWORD = "root";  
		 Connection con;
		 
		 try {  
	        Class.forName(DRIVER_CLASS).newInstance();
	        con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD);  
	       
	        List<ArrayList<CommentStoryInGoogleNewsJsonData>> numblist = new ArrayList<ArrayList<CommentStoryInGoogleNewsJsonData>>();
			
			ArrayList<CommentStoryInGoogleNewsJsonData> appearNumblist = new ArrayList<CommentStoryInGoogleNewsJsonData>();
			ArrayList<CommentStoryInGoogleNewsJsonData> disappearNumblist = new ArrayList<CommentStoryInGoogleNewsJsonData>();
			
			int interval = 5; 
			int steps = 100 / interval;  
			//initial the list, set all densities to 0
			for(int i=1; i<=steps; i++)
			{
				CommentStoryInGoogleNewsJsonData json = new CommentStoryInGoogleNewsJsonData();
				json.setCommentPercentage(i*interval);  
				json.setStoryNumber(0);
				appearNumblist.add(json);
			}
			for(int i=1; i<=steps; i++)
			{
				CommentStoryInGoogleNewsJsonData json = new CommentStoryInGoogleNewsJsonData();
				json.setCommentPercentage(i*interval);  
				json.setStoryNumber(0);
				disappearNumblist.add(json);
			}
			double minAppearPercentage = 100;
			double minDisappearPercentage = 100;
			
			String storySql = "SELECT * FROM newsstory";
			PreparedStatement storyStatement = con.prepareStatement(storySql);
			System.out.println("calculate the comment percentage for each story ... ");
			ResultSet rs = storyStatement.executeQuery();
			int ignoreStoryNumb = 0;
			int totalStoryNumb = 0;
			while(rs.next())
			{
				totalStoryNumb++;
				long storyID = rs.getLong(1);
				Timestamp storyStartTime = rs.getTimestamp(4);
				Timestamp storyEndtime = rs.getTimestamp(5);
				long commentTotalNum = getCommentNumberByStoryID(storyID, con);
				if(commentTotalNum == 0)
				{
					ignoreStoryNumb++;
					continue;				
				}
				
				//the comments number and percentage when the story appears
				long commentAppearNum = getCommentNumberUntilTime(storyID, storyStartTime, con); 
				double commentAppearPercent = (double)commentAppearNum*100 / (double)commentTotalNum;
				
				//the comments number and percentage when the story disappears
				long commentDisappearNum = getCommentNumberUntilTime(storyID, storyEndtime, con);
				double commentDisppearPercent = (double)commentDisappearNum*100 / (double)commentTotalNum;
				
				//update the minAppearPercentage and minDisappearPercentage
				if(commentAppearPercent < minAppearPercentage)
					minAppearPercentage = commentAppearPercent;
				if(commentDisppearPercent < minDisappearPercentage)
					minDisappearPercentage = commentDisppearPercent;
				
				//determine the bucket where the percentage is in the list
				int appearIndex = (int)commentAppearPercent / interval;				
				if(appearIndex == steps)
					appearIndex--;
				int disappearIndex = (int)commentDisppearPercent / interval;
				if(disappearIndex == steps)
					disappearIndex--;
				if(disappearIndex < 10)
					System.out.println("Story: "+storyID+", percent: "+commentDisppearPercent
							+", disappear: "+commentDisappearNum+", total: "+commentTotalNum);
				
				//increase the responding story number list by 1
				int originAppNumb = appearNumblist.get(appearIndex).getStoryNumber();
				appearNumblist.get(appearIndex).setStoryNumber(originAppNumb + 1);
				int originDisNumb = disappearNumblist.get(disappearIndex).getStoryNumber();
				disappearNumblist.get(disappearIndex).setStoryNumber(originDisNumb + 1);		
			}
			
			System.out.println("ignore story number: " + ignoreStoryNumb);
			System.out.println("total story number: " + totalStoryNumb);
			
			//for appearNumblist
			for(int i=steps-1; i>=0; i--)
			{			
				int commentPercentage = appearNumblist.get(i).getCommentPercentage();
				//remove the part which is less than the min 
				if(commentPercentage < minAppearPercentage)
					appearNumblist.remove(i);
				else
				{
					//determine the portion of stories falling in this comment percentage bucket
					int storyNumber = appearNumblist.get(i).getStoryNumber();
					double storyPercentage = (double)storyNumber*100 / (double)totalStoryNumb;
					appearNumblist.get(i).setStoryPercentage(storyPercentage);
				}
			}
			
			//for disappearNumblist
			for(int i=steps-1; i>=0; i--)
			{			
				int commentPercentage = disappearNumblist.get(i).getCommentPercentage();
				//remove the part which is less than the min 
				if(commentPercentage < minDisappearPercentage)
					disappearNumblist.remove(i);
				else
				{
					//determine the portion of stories falling in this comment percentage bucket
					int storyNumber = disappearNumblist.get(i).getStoryNumber();
					double storyPercentage = (double)storyNumber*100 / (double)totalStoryNumb;
					disappearNumblist.get(i).setStoryPercentage(storyPercentage);
				}
			}		
			
			
		

			
		 }catch(Exception  e){
			 System.out.println("error");
		 }
		 
	}
	
	
	/*** get the comments number before the time ***/
	static long  getCommentNumberUntilTime(long storyID, Timestamp time, Connection con)
	{
		long number = 0;
		String sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=? and comments.ArticleID in "
				+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
				+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?))";		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(sql);
			ps.setTimestamp(1, time);
			ps.setLong(2, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong("number");
			}
		}catch(Exception e){
			try{
                if(ps!=null)
                	ps.close();
             }catch(SQLException se2){
            	 System.err.println("getCommentNumberUntilTime: ERROR in closing statement!!!");           
             }// nothing we can do
		}		

		return number;
	}
	
	
	/*** get the comments number for the story ***/
	static long getCommentNumberByStoryID(long storyID, Connection con)
	{
		long number = 0;
		String sql = "SELECT count(*) as 'number' FROM comments where comments.ArticleID in "
				+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
				+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID=?))";		
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong("number");
			}
		}catch(Exception e){
			System.out.println("getCommentNumberByStoryID: "+e);
		}		
		
		return number;
	}
	
	
}
