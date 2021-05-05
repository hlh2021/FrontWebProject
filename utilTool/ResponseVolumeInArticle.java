package utilTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResponseVolumeInArticle {
	
	public static void main(String[] args)
	{
		ResponseVolumeInArticle resVol = new ResponseVolumeInArticle();
//		resVol.getResponseVolumeList(18); //fox
		resVol.getResponseVolumeList(9); //wsp
	}
	
	public List<long[]> getResponseVolumeList(long outletID)
	{
		List<long[]> numblist = new ArrayList<long[]>(); //delay, #comment of each article
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist; 
		
		int negResponse = 0;
		int more6Response = 0;
		int commentMore10k = 0;
		String delayStr = "";
		String volumeStr = "";
		//get the articleID, #comment, response delay in each article
		ArrayList<long[]> articleList = getArticleResponse(outletID, con);
		System.out.println("Total article size: "+articleList.size());
		for(long[] article : articleList)
		{
			long commentNum = article[1];
			long delayMin = article[2];
			
			//ignore articles with response delay <=0 or >6h
			if(delayMin <= 0)
			{
				negResponse++;
				continue;
			}
			else if(delayMin > 6*60)
			{
				more6Response++;
				continue;
			}
			
			//ignore articles with more than 1k comments
			if(commentNum > 10000)
			{
				commentMore10k++;
				continue;
			}
			
			delayStr = delayStr + String.valueOf(delayMin) + ",";
			volumeStr = volumeStr + String.valueOf(commentNum) + ",";
			
			//add the delay and #comment to numblist
			long[] record = new long[2];
			record[0] = delayMin;
			record[1] = commentNum;
			numblist.add(record);
		}//end of each article
		System.out.println("Negative response: "+negResponse);
		System.out.println("Response delay more than 6 hours: "+more6Response);
		System.out.println("Comment more than 10k: "+commentMore10k);
		System.out.println(delayStr);
		System.out.println(volumeStr);
		
		return numblist; 
	}
	
	public ArrayList<long[]> getArticleResponse(long outletID, Connection con)
	{
		ArrayList<long[]> articleList = new ArrayList<long[]>();
		String sql = "";
		
		if(outletID > 0)
		{
			sql = "select a.ArticleID, s.EndCrawlCommentCount as volume, "
					+ "datediff(minute,a.CommentConsistentPublishTime,s.TimeOfFirstComment) as response  "
					+ "from newsarticle a, articlestatistics s "
					+ "where a.ArticleID=s.ArticleID and a.NewsOutletID=? "
					+ "and a.CommentConsistentPublishTime is not null;";
		}
		else
		{
			sql = "select a.ArticleID, s.EndCrawlCommentCount as volume, "
					+ "datediff(minute,a.CommentConsistentPublishTime,s.TimeOfFirstComment) as response  "
					+ "from newsarticle a, articlestatistics s "
					+ "where a.ArticleID=s.ArticleID and a.NewsOutletID>? "
					+ "and a.CommentConsistentPublishTime is not null;";
		}
		try{
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, outletID);
			ResultSet rs = statement.executeQuery();
			
			while(rs.next())
			{
				long[] record = new long[3];
				record[0] = rs.getLong(1);
				record[1] = rs.getLong(2);
				record[2] = rs.getLong(3);
				articleList.add(record);
			}
			
			rs.close();
			statement.close();
		}catch(Exception e){
			System.out.println(e);
		}	
		
		return articleList;
	}

}
