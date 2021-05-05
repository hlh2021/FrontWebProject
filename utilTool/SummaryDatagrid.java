package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.DelegatingResultSet;

import struct.OutletCommentJsondata;

public class SummaryDatagrid {
	
	public List Summary() throws Exception{
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		**/
		ReadCSV cf = new ReadCSV();
    	String DATABASE_URL = cf.readConfig().getProperty("DBurl");
    	String USER_NAME =	cf.readConfig().getProperty("DBusername");
    	String PASSWORD =  cf.readConfig().getProperty("DBpwd");
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
    	Connection con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD); 
		PreparedStatement ps = null;

		List list=null;
		String lt=null;
		try {
			String sql ="SELECT * FROM newsoutlets where newsoutlets.NewsOutletID in (select NewsOutletID from newsarticle where newsarticle.HasComments=true)";

			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
//			rs= ps.executeQuery();
			
			
			list=new ArrayList();
			while(rs.next()){
				String namestr = rs.getString("Name");
				long outletID = rs.getLong("NewsOutletID");

				if(namestr!=null)
				{
					OutletCommentJsondata t=new OutletCommentJsondata();
					t.setNewsWebsite(rs.getString("Name"));

					
					int Commentsnumber = GetNumber(rs.getString("Name"),outletID,0);
					if(Commentsnumber>0)
						t.setday1(Commentsnumber);
					else
						t.setday1(0);
					
					Commentsnumber = GetNumber(rs.getString("Name"),outletID,1);
					if(Commentsnumber>0)
						t.setday2(Commentsnumber);
					else
						t.setday2(0);
					
					Commentsnumber = GetNumber(rs.getString("Name"),outletID,2);
					if(Commentsnumber>0)
						t.setday3(Commentsnumber);
					else
						t.setday3(0);
					
					Commentsnumber = GetNumber(rs.getString("Name"),outletID,3);
					if(Commentsnumber>0)
						t.setday4(Commentsnumber);
					else
						t.setday4(0);
					
					Commentsnumber = GetNumber(rs.getString("Name"),outletID,4);
					if(Commentsnumber>0)
						t.setday5(Commentsnumber);
					else
						t.setday5(0);
					
					Commentsnumber = GetNumber(rs.getString("Name"),outletID,5);
					if(Commentsnumber>0)
						t.setsum(Commentsnumber);
					else
						t.setsum(0);

					list.add(t);
				}				
			}
			
			OutletCommentJsondata t=new OutletCommentJsondata();
			String name = "total";
			long outletID = 0;
			t.setNewsWebsite(name);

			
			int Commentsnumber = GetNumber(name,outletID,0);
			if(Commentsnumber>0)
				t.setday1(Commentsnumber);
			else
				t.setday1(0);
			
			Commentsnumber = GetNumber(name,outletID,1);
			if(Commentsnumber>0)
				t.setday2(Commentsnumber);
			else
				t.setday2(0);
			
			Commentsnumber = GetNumber(name,outletID,2);
			if(Commentsnumber>0)
				t.setday3(Commentsnumber);
			else
				t.setday3(0);
			
			Commentsnumber = GetNumber(name,outletID,3);
			if(Commentsnumber>0)
				t.setday4(Commentsnumber);
			else
				t.setday4(0);
			
			Commentsnumber = GetNumber(name,outletID,4);
			if(Commentsnumber>0)
				t.setday5(Commentsnumber);
			else
				t.setday5(0);
			
			Commentsnumber = GetNumber(name,outletID,5);
			if(Commentsnumber>0)
				t.setsum(Commentsnumber);
			else
				t.setsum(0);

			list.add(t);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

		}
		con.close();
		return list;
		
	}

	private int GetNumber(String name, long outletID, int i) {
		// TODO Auto-generated method stub
		int num = 0;
				
		try {
			/***
			Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
			Connection con = ds.getConnection();	
			**/
			ReadCSV cf = new ReadCSV();
	    	String DATABASE_URL = cf.readConfig().getProperty("DBurl");
	    	String USER_NAME =	cf.readConfig().getProperty("DBusername");
	    	String PASSWORD =  cf.readConfig().getProperty("DBpwd");
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	Connection con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD); 
			PreparedStatement ps = null;
			
			String sql;
			if(name.equals("total"))
			{
				if(i>4)
				{
					/***
					sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments,commentsystem  "
							+ "where newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+ "and newsoutlets.CommentSystem=commentsystem.SystemType and commentsystem.CrawlAble=true "						
							+ "and newsarticle.AID =comments.NewsArticle_AID;";
					***/
					sql = "SELECT count(*) as 'number' FROM comments where comments.ArticleID in (SELECT ArticleID FROM newsarticle where newsarticle.HasComments=true)";
				}
				else if(i==4)
				{
					/**
					sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments,commentsystem "
						+ "where newsarticle.NewsOutlets_ID =newsoutlets.ID "
						+ "and newsoutlets.CommentSystem=commentsystem.SystemType and commentsystem.CrawlAble=true "
						+ "and newsarticle.AID =comments.NewsArticle_AID "
						+ "and comments.Time <= DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=DATE_SUB(CURDATE(), INTERVAL "+i+" DAY)"
							+"and comments.ArticleID in (SELECT ArticleID FROM newsarticle where newsarticle.HasComments=true)";
				}
				else
				{
					/***
					sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments "
						+ "where newsarticle.NewsOutlets_ID =newsoutlets.ID "
						+ "and newsarticle.AID =comments.NewsArticle_AID "
						+ "and comments.Time = DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time=DATE_SUB(CURDATE(), INTERVAL "+i+" DAY) "
							+"and comments.ArticleID in (SELECT ArticleID FROM newsarticle where newsarticle.HasComments=true)";
				}

				ps=con.prepareStatement(sql);
				
				//ResultSet
				DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
				
				
				while(rs.next()){
					num = rs.getInt("number");
					System.out.println("At "+i+" , total in ALL : "+num);
				}
			}
			else if((!name.equals("total")) && i>4)  //get total comment number
			{
				/***
				sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments "
						+ "where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
						+ "and newsarticle.AID =comments.NewsArticle_AID;";
				***/
				sql = "SELECT count(*) as 'number' FROM comments where comments.ArticleID in "
						+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID+" and newsarticle.HasComments=1)";
				
				ps=con.prepareStatement(sql);
				
				//ResultSet
				DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
				
				
				while(rs.next()){
					num = rs.getInt("number");
					System.out.println("total in this outlet: "+num);
				}
				
			}
			else if((!name.equals("total")) && i==4)    //get comment number on i day ago and earlier
			{
				/***
				sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments "
						+ "where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
						+ "and newsarticle.AID =comments.NewsArticle_AID "
						+ "and comments.Time <= DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";	
				**/
				sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=DATE_SUB(CURDATE(), INTERVAL "+i+" DAY) and comments.ArticleID in "
						+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID+" and newsarticle.HasComments=1)";
				ps=con.prepareStatement(sql);
				
				//ResultSet
				DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
				
				
				while(rs.next()){
					num = rs.getInt("number");
					System.out.println(i+" day ago and earlier, comments in this outlet:"+num);
				}
			}
			else    //get comment number on i day ago
			{
				/**
				sql="SELECT count(*) as 'number' FROM newsarticle,newsoutlets,comments "
						+ "where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
						+ "and newsarticle.AID =comments.NewsArticle_AID "
						+ "and comments.Time = DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";	
				**/
				sql = "SELECT count(*) as 'number' FROM comments where comments.Time=DATE_SUB(CURDATE(), INTERVAL "+i+" DAY) and comments.ArticleID in "
						+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID+" and newsarticle.HasComments=1)";
				ps=con.prepareStatement(sql);
				
				//ResultSet
				DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
				
				
				while(rs.next()){
					num = rs.getInt("number");
					System.out.println(i+" day ago, comments in this outlet:"+num);
				}
			}
			
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}	
		
		return num;
	}

	public List UpDateDatagrid(String choseStory) throws Exception {
		// TODO Auto-generated method stub
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		**/
		ReadCSV cf = new ReadCSV();
    	String DATABASE_URL = cf.readConfig().getProperty("DBurl");
    	String USER_NAME =	cf.readConfig().getProperty("DBusername");
    	String PASSWORD =  cf.readConfig().getProperty("DBpwd");
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
    	Connection con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD); 
		PreparedStatement ps = null;

		List list=null;
		String lt=null;
		try {
			String sql ="SELECT * FROM newsoutlets where newsoutlets.NewsOutletID in (select NewsOutletID from newsarticle where newsarticle.HasComments=true)";

			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
//			rs= ps.executeQuery();
						
			list=new ArrayList();
			while(rs.next()){
				OutletCommentJsondata t=new OutletCommentJsondata();
				
				String namestr = rs.getString("Name");
				long outletID = rs.getLong("NewsOutletID");

				if(namestr!=null)
				{
					t.setNewsWebsite(rs.getString("Name"));
					
					int Commentsnumber = StoryNumber(choseStory,rs.getString("Name"),outletID,0);
					if(Commentsnumber>0)
						t.setday1(Commentsnumber);
					else
						t.setday1(0);
					
					Commentsnumber = StoryNumber(choseStory,rs.getString("Name"),outletID,1);
					if(Commentsnumber>0)
						t.setday2(Commentsnumber);
					else
						t.setday2(0);
					
					Commentsnumber = StoryNumber(choseStory,rs.getString("Name"),outletID,2);
					if(Commentsnumber>0)
						t.setday3(Commentsnumber);
					else
						t.setday3(0);
					
					Commentsnumber =StoryNumber(choseStory,rs.getString("Name"),outletID,3);
					if(Commentsnumber>0)
						t.setday4(Commentsnumber);
					else
						t.setday4(0);
					
					Commentsnumber = StoryNumber(choseStory,rs.getString("Name"),outletID,4);
					if(Commentsnumber>0)
						t.setday5(Commentsnumber);
					else
						t.setday5(0);
					
					Commentsnumber = StoryNumber(choseStory,rs.getString("Name"),outletID,5);
					if(Commentsnumber>0)
						t.setsum(Commentsnumber);
					else
						t.setsum(0);

					list.add(t);
				}							
			}
			
			//add the end row total
			OutletCommentJsondata t=new OutletCommentJsondata();
			String name = "total";
			long outletID = 0;
			t.setNewsWebsite(name);
			
			int Commentsnumber = StoryNumber(choseStory,name,outletID,0);
			if(Commentsnumber>0)
				t.setday1(Commentsnumber);
			else
				t.setday1(0);
			
			Commentsnumber = StoryNumber(choseStory,name,outletID,1);
			if(Commentsnumber>0)
				t.setday2(Commentsnumber);
			else
				t.setday2(0);
			
			Commentsnumber = StoryNumber(choseStory,name,outletID,2);
			if(Commentsnumber>0)
				t.setday3(Commentsnumber);
			else
				t.setday3(0);
			
			Commentsnumber = StoryNumber(choseStory,name,outletID,3);
			if(Commentsnumber>0)
				t.setday4(Commentsnumber);
			else
				t.setday4(0);
			
			Commentsnumber = StoryNumber(choseStory,name,outletID,4);
			if(Commentsnumber>0)
				t.setday5(Commentsnumber);
			else
				t.setday5(0);
			
			Commentsnumber = StoryNumber(choseStory,name,outletID,5);
			if(Commentsnumber>0)
				t.setsum(Commentsnumber);
			else
				t.setsum(0);

			list.add(t);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

		}
		con.close();
		return list;
		
	}

	private int StoryNumber(String choseStory, String name, long outletID, int i) {

		int num = 0;		
		
		try {
			/***
			Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
			Connection con = ds.getConnection();		
			***/
			ReadCSV cf = new ReadCSV();
	    	String DATABASE_URL = cf.readConfig().getProperty("DBurl");
	    	String USER_NAME =	cf.readConfig().getProperty("DBusername");
	    	String PASSWORD =  cf.readConfig().getProperty("DBpwd");
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	Connection con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD); 
			PreparedStatement ps = null;
			String sql="";
			
			if(name.equals("total"))
			{
				if(i>4)
				{
					/**
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,commentsystem,comments "
						    +"where newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsoutlets.CommentSystem=commentsystem.SystemType and commentsystem.CrawlAble=true "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID;";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
				else if(i==4)
				{
					/***
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,commentsystem,comments "
						    +"where newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsoutlets.CommentSystem=commentsystem.SystemType and commentsystem.CrawlAble=true "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID "
							+"and comments.Time <= DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=DATE_SUB(CURDATE(), INTERVAL "
							+i+" DAY) and comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
				else
				{
					/***
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,comments "
						    +"where newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID "
							+"and comments.Time = DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time=DATE_SUB(CURDATE(), INTERVAL "
							+i+" DAY) and comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
			}
			else
			{
				if(i>4)
				{
					/**
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,comments "
						    +"where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID
							+" and newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
				else if(i==4)
				{
					/**
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,comments "
						    +"where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID "
							+"and comments.Time <= DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time<=DATE_SUB(CURDATE(), INTERVAL "
							+i+" DAY) and comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID
							+" and newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
				else
				{
					/**
					sql= "SELECT count(*) as 'number' FROM newsstory,newsline,newsarticle,newsoutlets,comments "
						    +"where newsoutlets.name = '"+name+"' and newsarticle.NewsOutlets_ID =newsoutlets.ID "
							+"and newsstory.Name = '"+choseStory+"' "
							+"and newsstory.NSID = newsline.NewsStory_NSID and newsline.NLID = newsarticle.NewsLine_NLID "
							+"and newsarticle.AID =comments.NewsArticle_AID "
							+"and comments.Time = DATE_SUB(CURDATE(), INTERVAL "+i+" DAY);";
					**/
					sql = "SELECT count(*) as 'number' FROM comments where comments.Time=DATE_SUB(CURDATE(), INTERVAL "
							+i+" DAY) and comments.ArticleID in "
							+"(SELECT ArticleID FROM newsarticle where newsarticle.NewsOutletID="+outletID
							+" and newsarticle.HasComments=1 and newsarticle.NewsLineID in "
							+"(SELECT NewsLineID FROM newsline where newsline.NewsStoryID in "
							+"(SELECT NewsStoryID FROM newsstory where newsstory.Name='"+choseStory+"')))";
				}
			}
			
			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
			
			
			while(rs.next()){
				num = rs.getInt("number");
			}
			
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}	

		return num;

	}

}
