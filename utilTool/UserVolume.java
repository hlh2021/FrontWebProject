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

import struct.UserVolumeJsonData;

public class UserVolume {
	
	public List<UserVolumeJsonData> getUserVolumeList(int topStoryNumber) throws Exception
	{
		List<UserVolumeJsonData> numblist = new ArrayList<UserVolumeJsonData>();	
		
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist; 
		
		String countSql = "SELECT l.NewsStoryID, count(distinct(c.AuthorName)) as author FROM comments c, newsarticle a, newsline l "
        		+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID group by l.NewsStoryID order by author desc";
//		String countSql = "SELECT l.NewsStoryID, sum(s.UserNumber) as author FROM articlestatistics s, newsarticle a, newsline l "
//				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID group by l.NewsStoryID order by author desc";
		
//        PreparedStatement storyStatement = con.prepareStatement(countSql);
//        ResultSet rs = storyStatement.executeQuery();
        
        //select the first topStoryNumber stories according to user number
        int i = 1;
//        while(rs.next())
//        {
        	if(i <= topStoryNumber)
        	{
        		UserVolumeJsonData json = new UserVolumeJsonData();
//        		long id = rs.getLong(1);
//        		long number = rs.getLong(2);
        		long id = 1159;
        		long number = getTotalUserNumber(id, con);	
        		String storyName = selectStoryNameByID(id, con);
				System.out.println("story id: "+id+", name: "+storyName+", number: "+number);
				
				long foxCount = getUserNumberByOutlet(id, "foxnews, Fox News", con); //combine different names
				long dmCount = getUserNumberByOutlet(id, "Daily Mail", con);
				long guardCount = getUserNumberByOutlet(id, "theguardian, The Guardian", con); 
				long wshpCount = getUserNumberByOutlet(id, "washingtonpost, Washington Post, Washington Post (blog)", con); 
				long nytCount = getUserNumberByOutlet(id, "New York Times", con);
				long wsjCount = getUserNumberByOutlet(id, "wsj, Wall Street Journal (blog)", con);
				long cnnCount = getUserNumberByOutlet(id, "cnn, CNN International", con);
				long bbcCount = getUserNumberByOutlet(id, "bbc, BBC News", con); 
				long timeCount = getUserNumberByOutlet(id, "TIME", con);
				long nydnCount = getUserNumberByOutlet(id, "New York Daily News", con);
				long marketCount = getUserNumberByOutlet(id, "MarketWatch", con);
				long seattleCount = getUserNumberByOutlet(id, "The Seattle Times", con);
				long aljazeeraCount = getUserNumberByOutlet(id, "AlJazeera.com", con);
				long nyPostCount = getUserNumberByOutlet(id, "New York Post", con);
								
				json.setStory(storyName);
				json.setTotalUserNumber(number);
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
//        	else
//				break;
			i++;
//        }
		
		return numblist;
	}
	
	public List<UserVolumeJsonData> getUserVolumeListForAll(int topStoryNumber) throws NamingException, SQLException
	{
		/**
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		**/
		Util util = new Util();
		Connection con = util.dbConnection();
		
		String storySql = "SELECT * FROM newsstory";
		PreparedStatement storyStatement = con.prepareStatement(storySql);
		ResultSet rs = storyStatement.executeQuery();
		
		List<UserVolumeJsonData> numblist = new ArrayList<UserVolumeJsonData>();	
		List<Map.Entry<Long, Long>> list = sortStoryByUser(rs, con);  //the first is storyID, second is users number
		
		//select the first topStoryNumber stories according to user number
		int i = 1;
		for(Map.Entry<Long, Long> mapping:list)
		{			
			if(i <= topStoryNumber)
			{
				UserVolumeJsonData json = new UserVolumeJsonData();
				long id = mapping.getKey();				
				String storyName = selectStoryNameByID(id, con);
				System.out.println("story id: "+id+", name: "+storyName);
				long number = mapping.getValue();
				
				long foxCount = getUserNumberByOutlet(id, "foxnews, Fox News", con); //combine different names
				long dmCount = getUserNumberByOutlet(id, "Daily Mail", con);
				long guardCount = getUserNumberByOutlet(id, "theguardian, The Guardian", con); 
				long wshpCount = getUserNumberByOutlet(id, "washingtonpost, Washington Post, Washington Post (blog)", con); 
				long nytCount = getUserNumberByOutlet(id, "New York Times", con);
				long wsjCount = getUserNumberByOutlet(id, "wsj, Wall Street Journal (blog)", con);
				long cnnCount = getUserNumberByOutlet(id, "cnn, CNN International", con);
				long bbcCount = getUserNumberByOutlet(id, "bbc, BBC News", con); 
				long timeCount = getUserNumberByOutlet(id, "TIME", con);
				long nydnCount = getUserNumberByOutlet(id, "New York Daily News", con);
								
				json.setStory(storyName);
				json.setTotalUserNumber(number);
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
				
				numblist.add(json);
			}
			else
				break;
			i++; 
        } 
		
		return numblist;
		
	}
	
	
	private List<Map.Entry<Long, Long>> sortStoryByUser(ResultSet rs, Connection con) throws SQLException
	{
		Map<Long, Long> numberMap = new TreeMap<Long, Long>();  //the first is storyID, second is comments number
		
		while(rs.next())
		{
			long storyID = rs.getLong(1);
//			System.out.println("storyID: "+storyID);
			long userNumb = getTotalUserNumber(storyID, con);	
//			System.out.println("user number: "+userNumb);
			numberMap.put(storyID, userNumb);
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
	
	private long getTotalUserNumber(long storyID, Connection con) throws SQLException
	{
		long count = 0;
		
		String sql = "SELECT count(distinct(AuthorName)) FROM comments c, newsarticle a, newsline l "
				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";
//		String sql = "SELECT sum(s.UserNumber) FROM articlestatistics s, newsarticle a, newsline l "
//				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=?";

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
//		System.out.println("searching the user number for story "+storyID);
		ResultSet rs = statement.executeQuery();
	
		while(rs.next())
			count = rs.getLong(1);
//		System.out.println("user number is "+count);
		return count;
	}
	
	private long getUserNumberByOutlet(long storyID, String outlet, Connection con) throws SQLException
	{
		long count = 0;
		
		String[] outletArray = outlet.split(", ");
		String outletStr = "";
		for(int i=0;i<outletArray.length;i++)
			outletStr = outletStr+"'"+outletArray[i]+"',";
		outletStr = outletStr.substring(0, outletStr.length()-1);
		
		String sql = "SELECT count(distinct(c.AuthorName)) FROM comments c, newsarticle a, newsline l, newsoutlets o "
				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=? "
				+ "and a.NewsOutletID=o.NewsOutletID and o.Name in (" + outletStr + ")";
//		String sql = "SELECT sum(s.UserNumber) FROM articlestatistics s, newsarticle a, newsline l, newsoutlets o "
//				+ "where s.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=? "
//				+ "and a.NewsOutletID=o.NewsOutletID and o.Name in (" + outletStr + ")";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, storyID);
		
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
	
	public static void main(String[] args)
	{
		try{
			UserVolume ct = new UserVolume();
			List ctList = ct.getUserVolumeList(5);
			
			Util util = new Util();
			String jsonStr = util.ReturnJson(ctList);
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in UserVolumeServlet");
		}
	}
	

}
