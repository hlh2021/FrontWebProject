package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.UserOutletJsonData;

public class UserVolumeForAllOutlets {
	
	public List<UserOutletJsonData> getUserOutletList() throws Exception
	{
		List<UserOutletJsonData> numblist = new ArrayList<UserOutletJsonData>();	
		
		/****
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist;  
		
		System.out.println("count users for each outlet.....");
		
		String countSql = "SELECT a.newsoutletid, COUNT(distinct AuthorName) countUsers FROM comments c, newsarticle a "
				+ "WHERE c.articleid = a.articleid GROUP BY a.newsoutletid ORDER BY countUsers desc";
		
//		String countSql = "SELECT a.newsoutletid, sum(s.UserNumber) countUsers FROM articlestatistics s, newsarticle a "
//				+ "WHERE s.articleid = a.articleid GROUP BY a.newsoutletid ORDER BY countUsers desc";
        
        PreparedStatement storyStatement = con.prepareStatement(countSql);     
        ResultSet rs = storyStatement.executeQuery();
        while(rs.next())
        {
        	UserOutletJsonData json = new UserOutletJsonData();
        	long id = rs.getLong(1);
        	String name = getNameByOutletID(id, con);
    		long number = rs.getLong(2);
    		System.out.println("outlet id: "+id+", name: "+name+", user number: "+number);
    		
    		String anonymousName = "null";
    		if(name.equals("washingtonpost"))
    			anonymousName = "anonymous sooner";
    		long[] comment = getCommentNumberByOutletID(id, anonymousName, con);
//    		long[] comment = getCommentNumberByOutletID(id, con);
    		long totalComment = comment[0];
    		long fromAnonyComment = comment[1];
    		
    		json.setOutletID(id);
    		json.setOutletName(name);
    		json.setUserNumber(number);
    		json.setTotalComment(totalComment);
    		json.setCommentFromAnonymous(fromAnonyComment);
    		
    		numblist.add(json);
        }
        
        return numblist;
	}
	
	private String getNameByOutletID(long id, Connection con) throws SQLException
	{
		String name = "";
		
		String sql = "SELECT Name FROM newsoutlets where NewsOutletID=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next())
			name = rs.getString(1);
		
		return name;
	}
	
	private long[] getCommentNumberByOutletID(long id, String anonymousName, Connection con) throws SQLException
//	private long[] getCommentNumberByOutletID(long id, Connection con) throws SQLException
	{
		long[] count = new long[2];
		System.out.println("count comments for outlet "+ id);
		/***
		String sql = "SELECT A.totalNumber, B.nullNumber from "
				+ "(SELECT COUNT(c.CommentID) as totalNumber FROM comments c, newsarticle a "
				+ "WHERE c.articleid = a.articleid and a.NewsOutletID = ?) A, "
				+ "(SELECT COUNT(c.CommentID) nullNumber FROM comments c, newsarticle a "
				+ "WHERE c.articleid = a.articleid and c.AuthorName = ? and a.NewsOutletID = ?) B";
//				+ "WHERE c.articleid = a.articleid and c.AuthorName = 'null' and a.NewsOutletID = ?) B";
//				+ "WHERE c.articleid = a.articleid and c.AuthorName in ('null', 'anonymous sooner') and a.NewsOutletID = ?) B";
		**/
		//count the total comments for this outlet
		String sqlTotal = "SELECT sum(s.RevisitCommentCount) from articlestatistics s, newsarticle a "
				+"where s.articleID=a.articleID and a.NewsOutletID = ?";		
		PreparedStatement statementTotal = con.prepareStatement(sqlTotal);
		statementTotal.setLong(1, id);
		ResultSet rsTotal = statementTotal.executeQuery();
		while(rsTotal.next())
			count[0] = rsTotal.getLong(1);
		rsTotal.close();
		statementTotal.close();
		/**
		//count the comments from anonymous user for this outlet
		String sqlAnony = "SELECT COUNT(c.CommentID) nullNumber FROM comments c, newsarticle a "
				+"WHERE c.articleid = a.articleid and c.AuthorName = ? and a.NewsOutletID = ?";		
		PreparedStatement statementAnony = con.prepareStatement(sqlAnony);
		statementAnony.setString(1, anonymousName);
		statementAnony.setLong(2, id);
		ResultSet rsAnony = statementAnony.executeQuery();
		while(rsAnony.next())
			count[1] = rsAnony.getLong(1);
		rsAnony.close();
		statementAnony.close();
		***/
		//know the number of comments from anonymous user for Daily Mail, execute in DB
		if(id==142)
			count[1] = 12363;
		return count;
	}

}
