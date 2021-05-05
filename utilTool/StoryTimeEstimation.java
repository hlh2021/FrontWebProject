package utilTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StoryTimeEstimation {
	
	public static void main(String[] args)
	{
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return;
		
		estimation(con);
//		correlation(con, util);
	}
	
	public static void estimation(Connection con)
	{
		double threshold[] = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1};
		
		int outThresholdCount[][] = new int[10][2];  //each row keeps story number out of threshold for appear and diappear
		double outThreshold[][] = new double[10][2];  
		
		ArrayList<long[]> storyList = getEachCommentCountOfStory(con);
		int totalStory = storyList.size();
		System.out.println("number of story with comment: " + totalStory);
						
		for(int i=0; i<storyList.size(); i++)
		{
			long[] storyRecord = storyList.get(i);
			long storyID = storyRecord[0];
			long earlyApp = storyRecord[1];
			long estimateApp = storyRecord[2];
			long estimateDisapp = storyRecord[3];
			long lateDisapp = storyRecord[4];
			long commentTotalNum = storyRecord[5];
			
			//calculate proportion of difference for this story
			double distApp = (double)(estimateApp - earlyApp)/(double)commentTotalNum;
			double distDisapp = (double)(lateDisapp - estimateDisapp)/(double)commentTotalNum;
			
			for(int j=0; j<threshold.length; j++)
			{
				double thres = threshold[j];
				if(distApp > thres)
					outThresholdCount[j][0]++;
				if(distDisapp > thres)
					outThresholdCount[j][1]++;
			}
		}
		
		//change the story number to fraction
		for(int row=0; row<threshold.length; row++)
		{
			outThreshold[row][0] = (double)outThresholdCount[row][0]*100 / (double)totalStory;
			outThreshold[row][1] = (double)outThresholdCount[row][1]*100 / (double)totalStory;
			System.out.println(threshold[row]+":"+outThreshold[row][0]+","+outThreshold[row][1]+"; ");
		}
	}
	
	public static ArrayList<long[]> getEachCommentCountOfStory(Connection con)
	{
		ArrayList<long[]> storyList = new ArrayList<long[]>();
		String countSql = "SELECT s.NewsStoryID, "
				+ "sum(CASE WHEN c.Time <= DATEADD(minute,-10,s.StartTime) THEN 1 ELSE 0 END) AS count1, "
				+ "sum(CASE WHEN c.Time <= s.StartTime THEN 1 ELSE 0 END) AS count2, "
				+ "sum(CASE WHEN c.Time <= s.EndTimeInGoogle THEN 1 ELSE 0 END) AS count3, "
				+ "sum(CASE WHEN c.Time <= DATEADD(minute,10,s.EndTimeInGoogle) THEN 1 ELSE 0 END) AS count4, "
				+ "count(c.CommentID) AS total "
				+ "FROM comments c, newsarticle a, newsline l, newsstory s "
				+ "WHERE a.HasComments = 1 AND c.ArticleID = a.ArticleID AND a.NewsLineID = l.NewsLineID "
				+ "AND s.StartTime > '2016-01-06 16:05:07' "
				+ "AND cast(c.Time as time(0)) != '00:00:00' "
				+ "AND l.NewsStoryID = s.NewsStoryID GROUP BY s.NewsStoryID order by s.NewsStoryID";
		try
		{
			PreparedStatement storyStatement = con.prepareStatement(countSql);	
			
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
			{
				//for each story
				long[] record = new long[6];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong("count1");
				record[2] = rs.getLong("count2");
				record[3] = rs.getLong("count3");
				record[4] = rs.getLong("count4");
				record[5] = rs.getLong("total");
				storyList.add(record);
			}
			
			if(rs!=null)
				rs.close();
			if(storyStatement!=null)
				storyStatement.close();
		}catch(Exception e)
		{
			System.out.println("Fail to count comments for each story!\n"+e);
		}
		
		return storyList;
	}
	
	
	public static void correlation(Connection con, Util util)
	{
		ArrayList<long[]> storyList = getCommentCountTimeOfStory(con);
		int totalStory = storyList.size();
		System.out.println("number of story with comment: " + totalStory);
		
		String timeComApp = "";
		String comPerAppStr = "";
		String timeComDis = "";
		String comPerDisStr = "";
		
		double averageCommentPercentBeforeStart = 0;
		double averageCommentPercentAfterEnd = 0;
		double averageTimeBeforeStory = 0;
		double averageTimeAfterStory = 0;
		
		for(int i=0; i<storyList.size(); i++)
		{
			long[] storyRecord = storyList.get(i);
			long storyID = storyRecord[0];
			long commentTotalNum = storyRecord[1];
			long countBeforeStart = storyRecord[2];
			long countBeforeEnd = storyRecord[3];
			double secondBeforeStory = (double)storyRecord[4];
			double secondAfterStory = (double)storyRecord[5];
			double commentPercentBeforeStart = (double)countBeforeStart*100 / (double)commentTotalNum;
			double commentPercentAfterEnd = (double)(commentTotalNum-countBeforeEnd)*100 / (double)commentTotalNum;
			
			//to calcute the average time and missed comment percent
			averageCommentPercentBeforeStart = averageCommentPercentBeforeStart + commentPercentBeforeStart;
			averageCommentPercentAfterEnd = averageCommentPercentAfterEnd + commentPercentAfterEnd;
			averageTimeBeforeStory = averageTimeBeforeStory + secondBeforeStory;
			averageTimeAfterStory = averageTimeAfterStory + secondAfterStory;
			
			/**
			//to calcute the correaltion of time and missed comment percent
			int hourBeforeStory = 0;
			int hourAfterStory = 0;
			double eachHour = 60*60;  //each day
			//calculate the hours before story
			if(secondBeforeStory % eachHour == 0)
				hourBeforeStory = (int)(secondBeforeStory / eachHour);
			else
				hourBeforeStory = (int)(secondBeforeStory / eachHour) + 1;
			timeComApp = timeComApp + hourBeforeStory + ",";
			//calculate the hours after story
			if(secondAfterStory % eachHour == 0)
				hourAfterStory = (int)(secondAfterStory / eachHour);
			else
				hourAfterStory = (int)(secondAfterStory / eachHour) + 1;
			timeComDis = timeComDis + hourAfterStory + ",";
			
			comPerAppStr = comPerAppStr + commentPercentBeforeStart + ",";
			comPerDisStr = comPerDisStr + commentPercentAfterEnd + ",";
			***/
		}
		/**
		util.WriteFile("timeComApp2_100", timeComApp);
		util.WriteFile("timeComDis2_100", timeComDis);
		util.WriteFile("comPerAppStr2_100", comPerAppStr);
		util.WriteFile("comPerDisStr2_100", comPerDisStr);
		**/
		averageCommentPercentBeforeStart = averageCommentPercentBeforeStart /(double)storyList.size();
		averageCommentPercentAfterEnd = averageCommentPercentAfterEnd /(double)storyList.size();
		averageTimeBeforeStory = averageTimeBeforeStory /(double)storyList.size();
		averageTimeAfterStory = averageTimeAfterStory /(double)storyList.size();
		double eachHour = 60*60;
		if(averageTimeBeforeStory % eachHour == 0)
			averageTimeBeforeStory = (int)(averageTimeBeforeStory / eachHour);
		else
			averageTimeBeforeStory = (int)(averageTimeBeforeStory / eachHour) + 1;
		if(averageTimeAfterStory % eachHour == 0)
			averageTimeAfterStory = (int)(averageTimeAfterStory / eachHour);
		else
			averageTimeAfterStory = (int)(averageTimeAfterStory / eachHour) + 1;
		System.out.println("averageCommentPercentBeforeStart: "+averageCommentPercentBeforeStart);
		System.out.println("averageCommentPercentAfterEnd: "+averageCommentPercentAfterEnd);
		System.out.println("averageTimeBeforeStory: "+averageTimeBeforeStory);
		System.out.println("averageTimeAfterStory: "+averageTimeAfterStory);
		
	}
	
	public static ArrayList<long[]> getCommentCountTimeOfStory(Connection con)
	{
		ArrayList<long[]> storyList = new ArrayList<long[]>();
		String countSql = "SELECT s.NewsStoryID, count(c.CommentID) AS total, "
				+ "sum(CASE WHEN c.Time <= s.StartTime THEN 1 ELSE 0 END) AS CountBeforeStart,"
				+ "sum(CASE WHEN c.Time <= s.EndTimeInGoogle THEN 1 ELSE 0 END) AS CountBeforeEnd, "
				+ "datediff(second,min(c.Time),s.StartTime) as diffApp, "
//				+ "TIMESTAMPDIFF(second,min(c.Time),s.StartTime) as diffApp, "  //mysql
				+ "datediff(second,s.EndTimeInGoogle,max(c.Time)) as diffDis "
//				+ "TIMESTAMPDIFF(second,s.EndTimeInGoogle,max(c.Time)) as diffDis " //mysql
				+ "FROM comments c, newsarticle a, newsline l, newsstory s "
				+ "WHERE a.HasComments = 1 AND c.ArticleID = a.ArticleID AND "
				+ "a.NewsLineID = l.NewsLineID AND l.NewsStoryID = s.NewsStoryID "
				+ "AND s.StartTime > '2016-01-06 16:05:07' "
				+ "AND cast(c.Time as time(0)) != '00:00:00' "
				+ "GROUP BY s.NewsStoryID, s.StartTime, s.EndTimeInGoogle order by s.NewsStoryID";
		try
		{
			PreparedStatement storyStatement = con.prepareStatement(countSql);	
			
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
			{
				//for each story
				long[] record = new long[6];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong("total");
				record[2] = rs.getLong("CountBeforeStart");
				record[3] = rs.getLong("CountBeforeEnd");
				record[4] = rs.getLong("diffApp");
				record[5] = rs.getLong("diffDis");
//				if(record[1]>100 && record[5]>0 && record[5]<=2592000)
//				if(record[1]>100 && record[4]>0 && record[4]<=864000)
				if(record[1]>100) //more than 100 comment 
//				if(record[1]>100 && record[4]<=864000) //app limit to 10 days
//				if(record[1]>100 && record[5]<=2592000) //disapp limit to 30 days
					storyList.add(record);
			}
			
			if(rs!=null)
				rs.close();
			if(storyStatement!=null)
				storyStatement.close();
		}catch(Exception e)
		{
			System.out.println("Fail to count comments for each story!\n"+e);
		}
		
		return storyList;
	}

}
