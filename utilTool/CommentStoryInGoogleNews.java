package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.CommentStoryInGoogleNewsJsonData;
import struct.DensityOfRatioJsonData;

public class CommentStoryInGoogleNews {
	
	/*** get the story-commentPercentage when appearing in Google News ***/
	public List<ArrayList<CommentStoryInGoogleNewsJsonData>> getCommentDistribution() throws Exception
	{
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
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
//		Connection con = util.dbConnection();
		Connection con = util.mysqlConnection();
		if(con == null)
			return numblist;
		
		System.out.println("calculate the comment percentage for each story ... ");
		
		/***
		String countStartSql = "SELECT s.NewsStoryID, s.StartTime, COUNT(c.CommentID) AS 'number' "
				+ "FROM comments c, newsarticle a, newsline l, newsstory s "
				+ "WHERE a.HasComments = 1 AND c.ArticleID = a.ArticleID AND a.NewsLineID = l.NewsLineID "
				+ "AND l.NewsStoryID = s.NewsStoryID AND c.Time <= s.StartTime "
				+ "GROUP BY s.NewsStoryID, s.StartTime";
		***/	
		int totalStoryNumb = countStory(con);
		System.out.println("total story number: " + totalStoryNumb);
		ArrayList<long[]> storyList = getCommentCountOfStory(con);
//		ArrayList<long[]> storyList = simpleCommentCountOfStory(con);
		System.out.println("number of story with comment condition: " + storyList.size());
		String appearStr = "";
		String disappearStr = "";
		int calStory = 0;
		
		for(int i=0; i<storyList.size(); i++)
		{
			long[] storyRecord = storyList.get(i);
			long storyID = storyRecord[0];
//			int storyCountName = getStoryNumberByName(storyID, con);
//			if(storyCountName > 1)
//				continue;
			calStory++;
			long commentAppearNum = storyRecord[1];
			long commentDisappearNum = storyRecord[2];
			long commentTotalNum = storyRecord[3];
			double commentAppearPercent = (double)commentAppearNum*100 / (double)commentTotalNum;
			double commentDisppearPercent = (double)commentDisappearNum*100 / (double)commentTotalNum;
//			if(commentAppearPercent<=10)
//				System.out.println(storyID+": commentAppearPercent "+commentAppearPercent);
//			if(commentDisppearPercent<=30)
//				System.out.println(storyID+": commentDisppearPercent "+commentDisppearPercent);
			
			appearStr = appearStr + commentAppearPercent + ",";
			disappearStr = disappearStr + commentDisppearPercent + ",";
			
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
			
			//increase the responding story number list by 1
			int originAppNumb = appearNumblist.get(appearIndex).getStoryNumber();
			appearNumblist.get(appearIndex).setStoryNumber(originAppNumb + 1);
			int originDisNumb = disappearNumblist.get(disappearIndex).getStoryNumber();
			disappearNumblist.get(disappearIndex).setStoryNumber(originDisNumb + 1);	
		}
		
		System.out.println("number of story calculated: " + calStory);
		System.out.println("appear comment percentage: \n"+appearStr);
		System.out.println("disappear comment percentage: \n"+disappearStr);
	
		/***
		String countSql = "SELECT * FROM newsstory";
		PreparedStatement storyStatement = con.prepareStatement(countSql);		
		ResultSet rs = storyStatement.executeQuery();
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
			
			//increase the responding story number list by 1
			int originAppNumb = appearNumblist.get(appearIndex).getStoryNumber();
			appearNumblist.get(appearIndex).setStoryNumber(originAppNumb + 1);
			int originDisNumb = disappearNumblist.get(disappearIndex).getStoryNumber();
			disappearNumblist.get(disappearIndex).setStoryNumber(originDisNumb + 1);		
		}
		if(rs != null)
			rs.close();
		if(storyStatement != null)
			storyStatement.close();
		***/
		
				
		
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
		
		
		/**		
		//determine where the min is in the list
		int minAppearIndex = (int)minAppearPercentage / interval;
		int minDisappearIndex = (int)minDisappearPercentage / interval;
		
		//cut the part which is less than the min 
		for(int i=minAppearIndex-1; i>=0; i--)
			appearNumblist.remove(i);
		for(int i=minDisappearIndex-1; i>=0; i--)
			disappearNumblist.remove(i);
		***/
		
		//add the appear and disapper list into result
		numblist.add(appearNumblist);
		numblist.add(disappearNumblist);
		
		return numblist;
	}
	
	/*** get the number of stories ***/
	int countStory(Connection con)
	{
		int count = 0;
		String countSql = "SELECT count(*) FROM newsstory";
		try
		{
			PreparedStatement storyStatement = con.prepareStatement(countSql);		
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
				count = rs.getInt(1);
			
			if(rs!=null)
				rs.close();
			if(storyStatement!=null)
				storyStatement.close();
		}catch(Exception e)
		{
			System.out.println("Fail to count story!\n"+e);
		}
	
		
		return count;
	}
	
	
	
	/*** count the comments before story startTime and endTime ***/
	ArrayList<long[]> getCommentCountOfStory(Connection con)
	{
		ArrayList<long[]> storyList = new ArrayList<long[]>();
		String countSql = "SELECT s.NewsStoryID, "
				+ "sum(CASE WHEN c.Time <= s.StartTime THEN 1 ELSE 0 END) AS CountBeforeStart, "
				+ "sum(CASE WHEN c.Time <= s.EndTimeInGoogle THEN 1 ELSE 0 END) AS CountBeforeEnd, "
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
				long[] record = new long[4];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong("CountBeforeStart");
				record[2] = rs.getLong("CountBeforeEnd");
				record[3] = rs.getLong("total");
				if(record[3] > 100)
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
	
	
	/*** get the comments number before the time ***/
	long  getCommentNumberUntilTime(long storyID, Timestamp time, Connection con)
	{
		long number = 0;	
		String sql = "SELECT count(c.CommentID) as 'number' FROM comments c, newsarticle a, newsline l where l.NewsStoryID=? "
				+ "and a.HasComments=1 and c.ArticleID=a.ArticleID  and a.NewsLineID=l.NewsLineID and c.Time<=?";
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(sql);
			ps.setLong(1, storyID);
			ps.setTimestamp(2, time);
			
//			ps.setQueryTimeout(300); //in seconds
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
				number = rs.getLong("number");
			}
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
		}catch(Exception e){
			System.err.println("getCommentNumberUntilTime ERROR: "+e);
		}finally{
			
		}

		return number;
	}
	
	
	/*** get the comments number for the story ***/
	long getCommentNumberByStoryID(long storyID, Connection con)
	{
		long number = 0;
//		String sql = "SELECT count(*) FROM comments c, newsarticle a, newsline l "
//			+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
		String sql = "SELECT sum(s.RevisitCommentCount) FROM articlestatistics s, newsarticle a, newsline l "
			+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";	
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getLong(1);
			}
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
		}catch(Exception e){
			System.out.println("Fail in getCommentNumberByStoryID: "+e);
		}		
		
		return number;
	}
	
	/*** get the story number with the same name ***/
	int getStoryNumberByName(long storyID, Connection con)
	{
		int number = 0;
		String sql = "select count(b.NewsStoryID) from newsstory a, newsstory b where a.name=b.name and a.NewsStoryID=?";	
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, storyID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				number = rs.getInt(1);
			}
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
		}catch(Exception e){
			System.out.println("Fail in getStoryNumberByName: "+e);
		}		
		
		return number;
	}
	
	
	public static void main(String[] args)
	{
		Util util = new Util();
		Connection con = util.mysqlConnection();
		CommentStoryInGoogleNews obj = new CommentStoryInGoogleNews();
		try{
			obj.getCommentDistribution();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	/*** stupid way to count comment: per story, per article ***/
	ArrayList<long[]> simpleCommentCountOfStory(Connection con)
	{
		//return list of storyID, CountBeforeStart, CountBeforeEnd, Total
		ArrayList<long[]> countList = new ArrayList<long[]>();
		
		ArrayList<String[]> storyList = getStoryList(con);
		for(String[] storyObj: storyList)
		{
			//calculate for each story
			long storyID = Long.parseLong(storyObj[0]);
			String appTime = storyObj[1];
			String disappTime = storyObj[2];
			ArrayList<Long> articleList = getArticleListByStory(storyID, con);
			//count the comment of this story by traversing articles
			long totalCount = 0;
			long befStartCount = 0;
			long befEndCount = 0;
			for(long articleID: articleList)
			{
				//CountBeforeStart, CountBeforeEnd, Total in this article
				long[] curComCount = getCommentCountByArticle(articleID, appTime, disappTime, con);
				befStartCount = befStartCount + curComCount[0];
				befEndCount = befEndCount + curComCount[1];
				totalCount = totalCount + curComCount[2];
			}
			System.out.println("story: "+storyID+", StartTime: "+appTime+", EndTimeInGoogle: "+disappTime+", articles: "+articleList.size());
			System.out.println("befStartCount: "+befStartCount+", befEndCount: "+befEndCount+", totalCount: "+totalCount);
			//keep into the list
			long[] eachStory = new long[4];
			eachStory[0] = storyID;
			eachStory[1] = befStartCount;
			eachStory[2] = befEndCount;
			eachStory[3] = totalCount;
			if(totalCount > 100)
				countList.add(eachStory);
		}
		
		return countList;
	}


	/*** story id list ***/
	ArrayList<String[]> getStoryList(Connection con)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		String countSql = "SELECT NewsStoryID, StartTime, EndTimeInGoogle FROM newsstory where StartTime > '2016-01-06 16:05:07'";
		try
		{
			PreparedStatement storyStatement = con.prepareStatement(countSql);		
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
			{
				String[] record = new String[3];
				record[0] = String.valueOf(rs.getLong(1));
				record[1] = String.valueOf(rs.getTimestamp(2));
				record[2] = String.valueOf(rs.getTimestamp(3));
				list.add(record);
			}
			
			if(rs!=null)
				rs.close();
			if(storyStatement!=null)
				storyStatement.close();
		}catch(Exception e)
		{
			System.out.println("Fail to select all story!\n"+e);
		}
			
		return list;
	}
	
	/*** article id list by story ***/
	ArrayList<Long> getArticleListByStory(long storyID, Connection con)
	{
		ArrayList<Long> list = new ArrayList<Long>();
		String countSql = "SELECT a.articleID FROM newsarticle a, newsline l "
				+ "WHERE a.HasComments = 1 AND a.NewsLineID = l.NewsLineID AND l.NewsStoryID=?";
		try
		{
			PreparedStatement storyStatement = con.prepareStatement(countSql);	
			storyStatement.setLong(1, storyID);
			ResultSet rs = storyStatement.executeQuery();
			while(rs.next())
				list.add(rs.getLong(1));
			
			if(rs!=null)
				rs.close();
			if(storyStatement!=null)
				storyStatement.close();
		}catch(Exception e)
		{
			System.out.println("Fail to select articles!\n"+e);
		}
			
		return list;
	}
	
	/**
	 * get count of comment in this article
	 * @param articleID
	 * @param startTime
	 * @param endTime
	 * @param con
	 * @return count list
	 */
	long[] getCommentCountByArticle(long articleID, String startTime, String endTime, Connection con)
	{
		long[] res = new long[3];
		String countSql = "SELECT sum(CASE WHEN c.Time <= ? THEN 1 ELSE 0 END) AS CountBeforeStart, "
				+ "sum(CASE WHEN c.Time <= ? THEN 1 ELSE 0 END) AS CountBeforeEnd, "
				+ "count(c.CommentID) AS total FROM comments c "
				+ "WHERE c.articleID=? and cast(c.Time as time(0)) != '00:00:00'";
		try
		{
			PreparedStatement ps = con.prepareStatement(countSql);	
			ps.setString(1, startTime);
			ps.setString(2, endTime);
			ps.setLong(3, articleID);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				res[0] = rs.getLong(1);
				res[1] = rs.getLong(2);
				res[2] = rs.getLong(3);
			}
			
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
		}catch(Exception e)
		{
			System.out.println("Fail to select comments!\n"+e);
		}
		
		return res;
	}
}
