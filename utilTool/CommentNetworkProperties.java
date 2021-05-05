package utilTool;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import struct.DensityCountJsonData;
import struct.ManyTreeNode;

public class CommentNetworkProperties {
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getDistributionForOutletStory(String outletName, long storyID) throws Exception
	{
		ArrayList<Object> returnObject = new ArrayList<Object>(); 		
		List<DensityCountJsonData> sizeList = new ArrayList<DensityCountJsonData>();		
		List<DensityCountJsonData> depthList = new ArrayList<DensityCountJsonData>();
		List<DensityCountJsonData> logDepthList = new ArrayList<DensityCountJsonData>();
		List<DensityCountJsonData> widthList = new ArrayList<DensityCountJsonData>();
		List<DensityCountJsonData> userList = new ArrayList<DensityCountJsonData>();
		
		Map<Integer, Integer> sizeMap = new TreeMap<Integer, Integer>();
		int startSize = 6;
		int maxLogSize = startSize;
		int gapSize = 2;  //bucket the size to 10~(+0.2), the magnitude is 10 when show it in x-axis	
		sizeMap.put(startSize, 0);  //initial the frequency of depth=1 to be 0
		int maxIntSize = 0;
		
		Map<Integer, Integer> depthMap = new TreeMap<Integer, Integer>();
		depthMap.put(1, 0);  //initial the frequency of depth=1 to be 0
		int maxDepth = 1;
		
		Map<Integer, Integer> logDepthMap = new TreeMap<Integer, Integer>();
		int startLogDepth = 5; //start from 10~0.5, which is no more than depth 3
		int maxLogDepth = startLogDepth;
		int gapLogDepth = 1;  //bucket the size to 10~(+0.1), the magnitude is 10 when show it in x-axis	
		logDepthMap.put(startLogDepth, 0);  
		
		Map<Integer, Integer> widthMap = new TreeMap<Integer, Integer>();
		int startWidth = 6;
		int maxWidth = startWidth;
		int gapWidth = 1;
		widthMap.put(startWidth, 0); 
		int maxIntWidth = 0;
		
		Map<Integer, Integer> userMap = new TreeMap<Integer, Integer>();
		int startUserNumber = 6;
		int maxUser = startUserNumber;
		int gapUser = 1;
		userMap.put(startUserNumber, 0); 
		int maxIntUser = 0;
		
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return returnObject;
		
		//find all articles
		ArrayList<Long> articleList = getArticleListByOutletStory(outletName, storyID, con);
		System.out.println("get "+articleList.size()+" articles from "+outletName);
		int ignoreArticleNum = 0;
		for(long articleID : articleList)
		{
			int[] param = getStatisticsByArticle(articleID, con); //CommentCount, DepthOfTree, Width, UserNumber
//			System.out.println("size:"+param[0]+", depth:"+param[1]+" for article "+articleID);
			if(param[0]==0)
			{
				ignoreArticleNum++;
				continue;	
			}
			
			//take log for the size
			int size = param[0];
			if(size > maxIntSize)
				maxIntSize = size;
			double logSize = Math.log(size) * 10 / Math.log(10);
			int intervalSize = 0;
			if(logSize < startSize)
				intervalSize = startSize;
			else
				intervalSize = ((int)(logSize/(double)gapSize) + 1) * gapSize;  //in the bucket [0, 2)
			
			//for depth
			int intervalDepth = param[1];
			if(intervalDepth >= 18)
				System.out.println("The depth is "+intervalDepth+" for article "+articleID);
			//for logDepth
			double logDepth = Math.log(intervalDepth) * 10 / Math.log(10);
			int intervalLogDepth = 0;
			if(logDepth < startLogDepth)
				intervalLogDepth = startLogDepth;
			else
				intervalLogDepth = ((int)(logDepth/(double)gapLogDepth) + 1) * gapLogDepth;  //in the bucket [0, 1)
			
			//for width
			int width = param[2];
			if(width > maxIntWidth)
				maxIntWidth = width;
			double logWidth = Math.log(width) * 10 / Math.log(10);
			int intervalWidth = 0;
			if(logWidth < startWidth)
				intervalWidth = startWidth;
			else
				intervalWidth = ((int)(logWidth/(double)gapWidth) + 1) * gapWidth;  //in the bucket [0, 1)
			
			//for distinct user
			int user = param[3];
			if(user > maxIntUser)
				maxIntUser = user;
			double logUser = Math.log(user) * 10 / Math.log(10);
			int intervalUser = 0;
			if(logUser < startUserNumber)
				intervalUser = startUserNumber;
			else
				intervalUser = ((int)(logUser/(double)gapUser) + 1) * gapUser;  //in the bucket [0, 1)
			
			//update maxLogSize and sizeMap
			ArrayList<Object> sizeObject = updateMap(maxLogSize, intervalSize, gapSize, sizeMap);
			maxLogSize = (int)sizeObject.get(0);			
			sizeMap = (Map<Integer, Integer>)sizeObject.get(1);
			
			//update maxDepth and depthMap
			ArrayList<Object> depthObject = updateMap(maxDepth, intervalDepth, 1, depthMap);
			maxDepth = (int)depthObject.get(0);			
			depthMap = (Map<Integer, Integer>)depthObject.get(1);
			
			//update maxLogDepth and logDepthMap
			ArrayList<Object> logDepthObject = updateMap(maxLogDepth, intervalLogDepth, gapLogDepth, logDepthMap);
			maxLogDepth = (int)logDepthObject.get(0);			
			logDepthMap = (Map<Integer, Integer>)logDepthObject.get(1);
			
			//update maxWidth and widthMap
			ArrayList<Object> widthObject = updateMap(maxWidth, intervalWidth, gapWidth, widthMap);
			maxWidth = (int)widthObject.get(0);			
			widthMap = (Map<Integer, Integer>)widthObject.get(1);
			
			//update maxUser and userMap
			ArrayList<Object> userObject = updateMap(maxUser, intervalUser, gapUser, userMap);
			maxUser = (int)userObject.get(0);			
			userMap = (Map<Integer, Integer>)userObject.get(1);
			
		} //end of each article
		System.out.println("ignore article number: "+ignoreArticleNum);
		System.out.println("max size: "+maxIntSize);
		System.out.println("max width: "+maxIntWidth);
		System.out.println("max user: "+maxIntUser);
		
		
		//add all data to List by iterating Map, using magnitude=1 or 10
		sizeList = convertMapToJsonList(10, sizeList, sizeMap);
		depthList = convertMapToJsonList(1, depthList, depthMap);
		logDepthList = convertMapToJsonList(10, logDepthList, logDepthMap);
		widthList = convertMapToJsonList(10, widthList, widthMap);
		userList = convertMapToJsonList(10, userList, userMap);
		
		con.close();
		
		returnObject.add(sizeList);
		returnObject.add(depthList);
		returnObject.add(logDepthList);
		returnObject.add(widthList);
		returnObject.add(userList);
		return returnObject;
	}
	
	//increase the map and keep the record, update the maximum
	private ArrayList<Object> updateMap(int maxValue, int currentInterval, int gap, Map<Integer,Integer > map)
	{
		ArrayList<Object> returnObject = new ArrayList<Object>();
		//increase depthMap until reach current depth
		if(currentInterval > maxValue)
		{				
			for(int i=maxValue+gap; i<=currentInterval; i=i+gap)
			{
				map.put(i, 0);
			}
			maxValue = currentInterval;					
		}
		//keep the intervalDepth in depthMap
		if(map.containsKey(currentInterval))
		{
			int count = map.get(currentInterval);
			count += 1;
			map.put(currentInterval, count);
		}
		else
			System.out.println("increase map error!"+maxValue+","+currentInterval+","+gap);	
		
		returnObject.add(maxValue);
		returnObject.add(map);
		return returnObject;
	}
		
	private List<DensityCountJsonData> convertMapToJsonList(int magnitude, List<DensityCountJsonData> jsonList, Map<Integer, Integer> map)
	{
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) 
		{
			int interval = Integer.parseInt(it.next().toString());
			int count = map.get(interval);			
//			double density = (double)count / (double)(articleList.size()-ignoreArticleNum);
			double density = (double)count;
			DensityCountJsonData json = new DensityCountJsonData();
			DecimalFormat f = new DecimalFormat("##.0");
			double intervalShow = (double)interval / (double)magnitude;
			intervalShow = Double.parseDouble(f.format(intervalShow));
			json.setInterval(intervalShow);
			json.setDensity(density);			
			jsonList.add(json);
		}
		
		return jsonList;
	}
	
	
	private ArrayList<Long> getArticleListByOutletStory(String outlet, long storyID, Connection con) throws SQLException
	{
		ArrayList<Long> list = new ArrayList<Long>();
		String sql = "";
		PreparedStatement statement = null;
		if(outlet.equals("null") && storyID==0)
		{
			sql = "SELECT ArticleID FROM newsarticle where HasComments=1";
			statement = con.prepareStatement(sql);
		}
		else if(storyID==0)
		{
			sql = "SELECT a.ArticleID FROM newsarticle a, newsoutlets o "
				+ "where a.NewsOutletID=o.NewsOutletID and a.HasComments=1 and o.Name=?";
			statement = con.prepareStatement(sql);
			statement.setString(1, outlet);	
		}
		else if(outlet.equals("null"))
		{
			sql = "SELECT  a.ArticleID FROM newsarticle a, newsline l "
				+ "where a.NewsLineID=l.NewsLineID and a.HasComments=1 and l.NewsStoryID=?";
			statement = con.prepareStatement(sql);
			statement.setLong(1, storyID);
		}	
		
		ResultSet rs = statement.executeQuery();
		while(rs.next())
			 list.add(rs.getLong(1));
		
		rs.close();
		statement.close();
		
		return list;
	}
	
	private int[] getStatisticsByArticle(long articleID, Connection con) throws SQLException
	{
		int[] info = new int[]{0, 0, 0, 0};		
		String sql = "SELECT EndCrawlCommentCount, DepthOfTree, Width, UserNumber from articlestatistics where ArticleID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, articleID);	
		
		ResultSet rs = statement.executeQuery();
		while(rs.next())
		{
			info[0] = rs.getInt(1);
			info[1] = rs.getInt(2);
			info[2] = rs.getInt(3);
			info[3] = rs.getInt(4);
		}
		
		statement.close();
		
		return info;
	}
	
	
	
}
