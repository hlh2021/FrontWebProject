package utilTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CommentPercentInArticle {
	
	public static void main(String[] args)
	{
		CommentPercentInArticle com = new CommentPercentInArticle();
		com.getCommentPercentForArticle();
	}
	
	public void getCommentPercentForArticle()
	{
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return;
		String path = "G:\\paper\\resultData\\3D.csv";
		NumberFormat formatter = new DecimalFormat("#0.00");
		long outletID = 18;
		int bucket = 6;
		int arrayLength = 72/bucket;
		String timeStr = "";
		for(int i=1; i<=arrayLength; i++)
			timeStr = timeStr + i*bucket + ",";
		timeStr = timeStr.substring(0, timeStr.length()-1);
		System.out.println(timeStr);	
//		writeFile(path, timeStr);
		
		ArrayList<Long> articleList = selectArticleListByOutlet(outletID, con);
//		System.out.println("article size: "+articleList.size());
		for(long articleID : articleList)
		{
//			long articleID = 4;
			String percentStr = "";
			ArrayList<Timestamp> timeList = getCommentListByArticle(articleID, con);
			int totalCom = timeList.size();
			if(totalCom == 0)
				continue;
			
			//if the time part of first comment is 00:00:00, ignore 
			Timestamp firstTime = timeList.get(0);
			if(firstTime.toString().contains("00:00:00"))
				continue;
			
			double[] percentArray = new double[arrayLength];
			//count the comment in each bucket
			for(Timestamp time : timeList)
			{
				long diffInMs = time.getTime() - firstTime.getTime();
				double eachBucketMs = bucket*60*60*1000;
				int index = (int)((double)diffInMs/eachBucketMs);
				for(int i_index=index; i_index<arrayLength; i_index++)
					percentArray[i_index]++;
//				if(index >= arrayLength)
//					break;   //ignore the comment out of 72 hours
//				percentArray[index]++;
			} // end of each comment time
			//calculate the percentage
			for(int i=0; i<percentArray.length; i++)
			{
				percentArray[i] = (double)percentArray[i]*100 / (double)totalCom;				
				percentStr = percentStr + formatter.format(percentArray[i]) + ",";
			}
			percentStr = percentStr.substring(0, percentStr.length()-1);
			System.out.println(percentStr);
//			writeFile(path, percentStr);
		}//end of each article
		
	}
	
	public ArrayList<Long> selectArticleListByOutlet(long outletID, Connection connect)
	{
		ArrayList<Long> articleList = new ArrayList<Long>();
		PreparedStatement Statement = null;
		try{
			String sql = "SELECT ArticleID FROM newsarticle where newsoutletID=? and HasComments=1";
            Statement = connect.prepareStatement(sql);
            Statement.setLong(1, outletID);

			ResultSet re = Statement.executeQuery();
			while(re.next())
				articleList.add(re.getLong(1));
							
			if(re != null)
				re.close();
			if(Statement != null)
				Statement.close();
		}catch(Exception e){
			System.out.println("Fail to select article ID by outletID\n"+e);
		}
		
		return articleList;
	}
	
	public ArrayList<Timestamp> getCommentListByArticle(long articleID, Connection connect)
	{
		ArrayList<Timestamp> list = new ArrayList<Timestamp>();
		
		String sql = "SELECT Time from comments where ArticleID=? order by Time";
		try{
			PreparedStatement statement = connect.prepareStatement(sql);
			statement.setLong(1, articleID);	
			
			ResultSet rs = statement.executeQuery();
			while(rs.next())
				list.add(rs.getTimestamp(1));
			
			if(rs != null)
				rs.close();						
			if(statement != null)
				statement.close();
		}catch(Exception e){
			System.out.println("Fail to select comments time by articleID\n"+e);
		}
		
		return list;
	}
	
	private void writeFile(String path, String str)
	{
		if(str.length()>0)
		{
			File file = new File(path);
			try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(str, true));
				bw.write(str);
				bw.newLine();
				bw.flush();
				bw.close();
			}catch(Exception e){
				System.out.println("Error in writing comment file");
			}		
		}
	}

}
