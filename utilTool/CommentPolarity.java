package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.DelegatingResultSet;

import struct.CommentPolarityJsonData;

public class CommentPolarity {
	
	public List summaryComment(String story) throws Exception 
	{
		List<CommentPolarityJsonData> numblist = new ArrayList<CommentPolarityJsonData>();
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		**/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;
		
		PreparedStatement ps = null;				

		try {	
			/**
			String datesql = "SELECT distinct comments.Time FROM newsstory,newsline,newsarticle,comments "
					+"where comments.Time>'2015-01-01' "
					+"and newsstory.Name = '"+story+"' "
					+"and newsstory.NewsStoryID = newsline.NewsStoryID "
					+"and newsline.NewsLineID = newsarticle.NewsLineID "
					+"and newsarticle.ArticleID =comments.ArticleID "
					+"order by comments.Time";
			**/
			String datesql = "SELECT distinct comments.Time FROM comments where comments.ArticleID in "
					+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
					+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
					+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+story+"'))) order by comments.Time";
			
			ps=con.prepareStatement(datesql);			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(datesql);  						
			while(rs.next())
			{
				CommentPolarityJsonData comJson = new CommentPolarityJsonData();
				Date date = rs.getDate("Time");
				Format format = new SimpleDateFormat("yyyy-MM-dd");				
				comJson.setDate(format.format(date));
				long totalNumber = getCommentNumber(story, format.format(date));			
				long numb[] = getPolarityNumber(story, format.format(date));
				long posiNum = numb[0];
				long negaNum = numb[1];
				comJson.setTotalNumber(totalNumber);
				comJson.setPosiNumber(posiNum);
				comJson.setNegaNumber(negaNum);
				/***
				float proportion = 0;
				if(negaNum!=0)
					proportion = (float)posiNum/negaNum;
				DecimalFormat df = new DecimalFormat("0.000");
				comJson.setProportion(df.format(proportion));
				**/
				
				numblist.add(comJson);
				
			}
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
				
		return numblist;
	}
	
	public long getCommentNumber(String story, String date)
	{
		long number = 0;
		
		try{
			/***
			Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
			Connection con = ds.getConnection();	
			**/
			Util util = new Util();
			Connection con = util.dbConnection();
			if(con == null)
				return number;
			PreparedStatement ps = null;
			/**
			String sql = "SELECT count(*) as 'number' FROM commentsystem,newsoutlets,newsstory,newsline,newsarticle,comments "
				    +"where comments.Time = '"+date+"' "
					+"and newsstory.Name = '"+story+"' "
					+"and newsstory.NewsStoryID = newsline.NewsStoryID "
					+"and newsline.NewsLineID = newsarticle.NewsLineID "
					+"and newsarticle.ArticleID =comments.ArticleID "
					+"and newsarticle.NewsOutlets_ID =newsoutlets.ID "
					+"and newsoutlets.CommentSystem=commentsystem.SystemType "
					+"and commentsystem.CrawlAble=true";
			**/
			String sql = "SELECT count(*) as 'number' FROM comments where comments.Time='"+date+"' and comments.ArticleID in "
					+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
					+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
					+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+story+"')))";
			
			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
			
			
			while(rs.next()){
				number = rs.getLong("number");
			}
			
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}	
		
		return number;
	}
	
	public long[] getPolarityNumber(String story, String date)
	{
		long number[] = {0, 0};
		try {
			/***
			Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
			Connection con = ds.getConnection();	
			**/
			Util util = new Util();
			Connection con = util.dbConnection();
			if(con == null)
				return number;	
			PreparedStatement ps = null;
			/***
			String sql = "SELECT SUM(comments.PosiCount) as num1, SUM(comments.NegCount) as num2 FROM newsstory,newsline,newsarticle,comments "
				    +"where comments.Time = '"+date+"' "
					+"and newsstory.Name = '"+story+"' "
					+"and newsstory.NewsStoryID = newsline.NewsStoryID "
					+"and newsline.NLID = newsarticle.NewsLine_NLID "
					+"and newsarticle.AID =comments.NewsArticle_AID";
			
			String sql = "SELECT SUM(comments.PosiCount) as num1, SUM(comments.NegCount) as num2 FROM comments where comments.Time='"+date+"' and comments.NewsArticle_AID in "
					+"(SELECT AID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLine_NLID in "
					+"(SELECT NLID FROM newsline where newsline.NewsStory_NSID="
					+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+story+"')))";
					**/
			String sql1 = "SELECT count(*) as num1 FROM comments where comments.PosiCount > comments.NegCount "
					+"and comments.Time='"+date+"' and comments.ArticleID in "
					+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
					+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
					+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+story+"')))";
						
			ps=con.prepareStatement(sql1);
			DelegatingResultSet rs1 =(DelegatingResultSet) ps.executeQuery(sql1);  
						
			while(rs1.next()){
				number[0] = rs1.getLong("num1");
			}
			
			String sql2 = "SELECT count(*) as num2 FROM comments where comments.PosiCount < comments.NegCount "
					+"and comments.Time='"+date+"' and comments.ArticleID in "
					+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
					+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
					+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+story+"')))";
						
			ps=con.prepareStatement(sql2);
			DelegatingResultSet rs2 =(DelegatingResultSet) ps.executeQuery(sql2);  
						
			while(rs2.next()){
				number[1] = rs2.getLong("num2");
			}
			
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return number;
	}

}
