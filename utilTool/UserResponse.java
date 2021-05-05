package utilTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import struct.CommentLastedTimeJsonData;

public class UserResponse {
	
	/*** the time difference (in hour) between article and first comment for each article ***/
	public List<CommentLastedTimeJsonData> getResponseList(long outletID)
	{
//		outletID = (long)142;   //daily mail
//		outletID = (long)18;   //fox news
//		outletID = (long)26;   //theguardian
		outletID = (long)9;   //washington post
//		outletID = (long)51;   //nytimes
//		outletID = (long)3;   //wsj
		List<CommentLastedTimeJsonData> numblist = new ArrayList<CommentLastedTimeJsonData>();
		Map<Integer, Integer> lastMap = new TreeMap<Integer, Integer>();
		lastMap.put(1, 0);  //initial to 0, set the response to be no less than 1 hour
		int maxLast = 1;
		int articleNegResponse = 0;
		String responseStr = "";
		
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;
		
		//find articles in this outlet, with converted response time in minutes
		System.out.println("find articles and response time ....");
		ArrayList<long[]> articleList = getArticleResponse(outletID, con);
		System.out.println("article size: "+articleList.size()+", begin to calculate the response for each article...");
		
		for(long[] article: articleList)
		{
			double eachHour = 60; 
			double intervalMinutes = article[1];
			if(intervalMinutes <= 0)
			{
				articleNegResponse++;
				continue;
			}
			responseStr = responseStr + intervalMinutes + ",";
			int intervalHours = 0;
			if(intervalMinutes % eachHour == 0)
				intervalHours = (int)(intervalMinutes / eachHour);
			else
				intervalHours = (int)(intervalMinutes / eachHour) + 1;		
			
			if(intervalHours > 12)
				System.out.println(article[0]);
			
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
		
		System.out.println("The number of articles whose article time is later than comment: " + articleNegResponse);
		
		System.out.println(responseStr);
		
		Iterator it = lastMap.keySet().iterator();
		while (it.hasNext()) 
		{
			int hours = Integer.parseInt(it.next().toString());
			int number = lastMap.get(hours);
			CommentLastedTimeJsonData responseTimeJson = new CommentLastedTimeJsonData();
			responseTimeJson.setCommentTimeRange(hours);
			responseTimeJson.setArticleNumber(number);
			numblist.add(responseTimeJson);
			System.out.println(hours+" , "+number);
		}
		
		
		//close db
		try{
			con.close();
		}catch(Exception e){
			System.out.println("Close db error!");
		}		
		
		return numblist;
	}
	
	
	public ArrayList<long[]> getArticleResponse(long outletID, Connection con)
	{
		ArrayList<long[]> articleList = new ArrayList<long[]>();
		
		String sql = "select a.ArticleID, datediff(minute,a.CommentConsistentPublishTime,s.TimeOfFirstComment) as response "
				+ "from newsarticle a, articlestatistics s where a.ArticleID=s.ArticleID "
				+ "and a.CommentConsistentPublishTime is not null and a.NewsOutletID=?";
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, outletID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				long[] record = new long[2];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong(2);
				articleList.add(record);
			}
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}	
		
		return articleList;
	}
	
	
	public static void main(String[] args)
	{
		UserResponse util = new UserResponse();
		util.getResponseList(0);
	}
	
}
