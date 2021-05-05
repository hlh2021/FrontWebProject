package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.CommentVolumeJsonData;

public class CommentVolume {
	
	public List<CommentVolumeJsonData> getCommentVolumeList(int topStoryNumber) throws Exception
	{
		List<CommentVolumeJsonData> numblist = new ArrayList<CommentVolumeJsonData>();
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;  
		
		String storySql = "SELECT * FROM newsstory";
		PreparedStatement storyStatement = con.prepareStatement(storySql);
		ResultSet rs = storyStatement.executeQuery();
					
		List<Map.Entry<Long, Long>> list = sortStoryByComment(rs, con);  //the first is storyID, second is comments number
		
		//select the first topStoryNumber stories according to comments number
		int i = 1;
		for(Map.Entry<Long, Long> mapping:list)
		{			
			if(i <= topStoryNumber)
			{
				CommentVolumeJsonData json = new CommentVolumeJsonData();
				long id = mapping.getKey();
				long number = mapping.getValue();
				String storyName = selectStoryNameByID(id, con);
				System.out.println("story id: "+id+", name: "+storyName);
				
				long foxCount = getCommentNumberByOutlet(id, "foxnews, Fox News", con); //combine different names
				long dmCount = getCommentNumberByOutlet(id, "Daily Mail", con);
				long guardCount = getCommentNumberByOutlet(id, "theguardian, The Guardian", con); 
				long wshpCount = getCommentNumberByOutlet(id, "washingtonpost, Washington Post, Washington Post (blog)", con); 
				long nytCount = getCommentNumberByOutlet(id, "New York Times", con);
				long wsjCount = getCommentNumberByOutlet(id, "wsj, Wall Street Journal (blog)", con);
				long cnnCount = getCommentNumberByOutlet(id, "cnn, CNN International", con);
				long bbcCount = getCommentNumberByOutlet(id, "bbc, BBC News", con); 
				long timeCount = getCommentNumberByOutlet(id, "TIME", con);
				long nydnCount = getCommentNumberByOutlet(id, "New York Daily News", con);
				long marketCount = getCommentNumberByOutlet(id, "MarketWatch", con);
				long seattleCount = getCommentNumberByOutlet(id, "The Seattle Times", con);
				long aljazeeraCount = getCommentNumberByOutlet(id, "AlJazeera.com", con);
				long nyPostCount = getCommentNumberByOutlet(id, "New York Post", con);
								
				json.setStory(storyName);
				json.setTotalCommentNumber(number);
				json.setFromFox(foxCount);
				json.setFromDailyMail(dmCount);
				json.setFromGuardian(guardCount);
				json.setFromWashingtonPost(wshpCount);
				json.setFromNYTimes(nytCount);
				json.setFromWSJ(wsjCount);
				json.setFromCNN(cnnCount);
				json.setFromBBC(bbcCount);
				json.setFromTIME(timeCount);
				json.setFromNYDailyNews(nydnCount);
				json.setFromMarketWatch(marketCount);
				json.setFromSeattleTimes(seattleCount);
				json.setFromAlJazeera(aljazeeraCount);
				json.setFromNYPost(nyPostCount);
				
				numblist.add(json);
			}
			else
				break;
			i++; 
        } 
		
		storyStatement.close();
		
		return numblist;
	}
	
	private List<Map.Entry<Long, Long>> sortStoryByComment(ResultSet rs, Connection con) throws SQLException
	{
		Map<Long, Long> numberMap = new TreeMap<Long, Long>();  //the first is storyID, second is comments number
		
		while(rs.next())
		{
			long storyID = rs.getLong(1);
			long comNumb = getTotalCommentNumber(storyID, con);			
			numberMap.put(storyID, comNumb);
		}
		
		// descending sort, according to comparator
		List<Map.Entry<Long, Long>> list = new ArrayList<Map.Entry<Long, Long>>(numberMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Long, Long>>(){         
            public int compare(Entry<Long, Long> o1, Entry<Long, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
            
        });
		
		return list;
	}
		
	private long getTotalCommentNumber(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
//		String sql = "SELECT count(*) FROM comments c, newsarticle a, newsline l "
//				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
		String sql = "SELECT sum(s.EndCrawlCommentCount) FROM articlestatistics s, newsarticle a, newsline l "
				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
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
	
	private long getCommentNumberByOutlet(long storyID, String outlet, Connection con) throws SQLException
	{
		long count = 0;
		
		String[] outletArray = outlet.split(", ");
		String outletStr = "";
		for(int i=0;i<outletArray.length;i++)
			outletStr = outletStr+"'"+outletArray[i]+"',";
		outletStr = outletStr.substring(0, outletStr.length()-1);
//		String sql = "SELECT count(*) FROM comments c, newsarticle a, newsline l, newsoutlets o "
//				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=? "
//				+ "and a.NewsOutletID=o.NewsOutletID and o.Name in (" + outletStr + ")";
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
	

	private String selectStoryNameByID(long storyID, Connection con) throws SQLException
	{
		String name = "";
		
		String sql = "SELECT Name FROM newsstory where NewsStoryID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			name = rs.getString(1);
		
		if(rs != null)
			rs.close();
		if(statement != null)
			statement.close();
		
		return name;
	}

}
