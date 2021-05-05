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
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.ArticleVolumeJsonData;

public class ArticleVolume {
	
	public List<ArticleVolumeJsonData> getArticleVolumeList(int topStoryNumber) throws Exception
	{
		List<ArticleVolumeJsonData> numblist = new ArrayList<ArticleVolumeJsonData>();	
		
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
		
		List<Map.Entry<Long, Long>> list = sortStoryByArticle(rs, con);  //the first is storyID, second is articles number
//		List<Map.Entry<Long, Long>> list = sortStoryByComment(rs, con);  //the first is storyID, second is comments number
		
		//select the first topStoryNumber stories according to articles number
		int i = 1;
		for(Map.Entry<Long, Long> mapping:list)
		{			
			if(i <= topStoryNumber)
			{
				ArticleVolumeJsonData json = new ArticleVolumeJsonData();
				long id = mapping.getKey();
				System.out.println("story id: "+id);
				String storyName = selectStoryNameByID(id, con);
				long number = mapping.getValue();
				long articleKnown = getArticlesFromKnown(id, con);
				long articleWithCom = getArticlesWithComment(id, con);
				
				long foxCount = getArticleNumberByOutlet(id, "foxnews, Fox News", con); //combine different names
				long dmCount = getArticleNumberByOutlet(id, "Daily Mail", con);
				long guardCount = getArticleNumberByOutlet(id, "theguardian, The Guardian", con); 
				long wshpCount = getArticleNumberByOutlet(id, "washingtonpost, Washington Post, Washington Post (blog)", con); 
				long nytCount = getArticleNumberByOutlet(id, "New York Times", con);
				long wsjCount = getArticleNumberByOutlet(id, "wsj, Wall Street Journal (blog)", con);
				long cnnCount = getArticleNumberByOutlet(id, "cnn, CNN International", con);
				long bbcCount = getArticleNumberByOutlet(id, "bbc, BBC News", con); 
				long timeCount = getArticleNumberByOutlet(id, "TIME", con);
				long nydnCount = getArticleNumberByOutlet(id, "New York Daily News", con);
				long marketCount = getArticleNumberByOutlet(id, "MarketWatch", con);
				long seattleCount = getArticleNumberByOutlet(id, "The Seattle Times", con);
				long aljazeeraCount = getArticleNumberByOutlet(id, "AlJazeera.com", con);
				long nyPostCount = getArticleNumberByOutlet(id, "New York Post", con);
								
				json.setStory(storyName);
				json.setTotalArticleNumber(number);
				json.setArticlesFromKnown(articleKnown);
				json.setArticlesWithComment(articleWithCom);
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
		
		return numblist;
	}
	
	private List<Map.Entry<Long, Long>> sortStoryByArticle(ResultSet rs, Connection con) throws SQLException
	{
		Map<Long, Long> numberMap = new TreeMap<Long, Long>();  //the first is storyID, second is articles number
		
		while(rs.next())
		{
			long storyID = rs.getLong(1);
			long articleNumb = getTotalArticleNumber(storyID, con);			
			numberMap.put(storyID, articleNumb);
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
	
	private long getTotalArticleNumber(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(*) FROM newsarticle a, newsline l "
				+ "where a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	private long getArticlesFromKnown(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(*) FROM newsarticle a, newsline l, newsoutlets o, commentsystem s "
				+ "where a.NewsLineID=l.NewsLineID and l.NewsStoryID=? and a.NewsOutletID=o.NewsOutletID "
				+ "and o.SystemType=s.SystemType and s.CrawlAble=1";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		System.out.println("count the articles from known outlets....");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	private long getArticlesWithComment(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(*) FROM newsarticle a, newsline l "
				+ "where a.HasComments=1 and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		System.out.println("count the articles with comments....");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
		return count;
	}
	
	private long getArticleNumberByOutlet(long storyID, String outlet, Connection con) throws SQLException
	{
		long count = 0;
		
		String[] outletArray = outlet.split(", ");
		String outletStr = "";
		for(int i=0;i<outletArray.length;i++)
			outletStr = outletStr+"'"+outletArray[i]+"',";
		outletStr = outletStr.substring(0, outletStr.length()-1);
		String sql = "SELECT count(*) FROM newsarticle a, newsline l, newsoutlets o "
				+ "where l.NewsStoryID=? and o.Name in (" + outletStr + ") "
				+ "and a.NewsLineID=l.NewsLineID and a.HasComments=1 and a.NewsOutletID=o.NewsOutletID";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
//		System.out.println("count the articles from specified outlet....");
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			count = rs.getLong(1);
		
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
		
		return name;
	}

}
