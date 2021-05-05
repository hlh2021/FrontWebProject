package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import struct.UserCommentJsonData;

public class UserCommentContribution {
	
	public List<UserCommentJsonData> getUserContributionList(String outletName) throws Exception
	{
		List<UserCommentJsonData> numblist = new ArrayList<UserCommentJsonData>();	
		
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return numblist; 
		
		//specify the anonymous user name
		String anonymousName = "null";
//		if(outletName.equals("washingtonpost"))
//			anonymousName = "anonymous sooner";
		
		// count the total comments number from all non-anonymous users
		long totalComment = getTotalCommentNumber(outletName, anonymousName, con);       
        if(totalComment == 0)
        	return numblist;   
		
        // count the comments number from each non-anonymous users
        ArrayList<Long> eachCommentList = getEachCommentNumber(outletName, anonymousName, con); 
        
        // set the user comment distribution list
        System.out.println("set the user comment distribution list .... ");
        int userTotalNumber = eachCommentList.size();
        long commentNumber = 0;
        int userNumberBase = (int)(userTotalNumber * 0.1);  // the number for 10 percent in the total users number
        for(int i=0; i<eachCommentList.size(); i++)
        {
        	commentNumber = commentNumber + eachCommentList.get(i); //the cumulative comments number for current users
        	//only consider the contribution of 10%, 20%, ... , 100% users
        	if( (i+1) % userNumberBase == 0 )
        	{
        		int userPercentage = (i+1) / userNumberBase * 10;
        		double commentPercentage = (double)commentNumber / (double)totalComment * 100;
        		if(userPercentage == 100)
        			commentPercentage = 100; // to ensure all users contribute to all comments
        		
        		UserCommentJsonData json = new UserCommentJsonData();
            	json.setUserPercentage(userPercentage);
        		json.setCommentPercentage(commentPercentage);
        		numblist.add(json);
        	}
        	
        }       
        
        return numblist;
	}
	
	
	// count the total comments number from all non-anonymous users
	private long getTotalCommentNumber(String outletName, String anonymousName, Connection con)
	{
		long number = 0;
		
		String totalSql = "SELECT COUNT(c.CommentID) nullNumber FROM comments c, newsarticle a, newsoutlets o "
				+ "WHERE c.ArticleID = a.ArticleID and a.NewsOutletID = o.NewsOutletID and o.Name = ? and c.AuthorName != ?";
		
		PreparedStatement statementTotal = null;
		try{
			statementTotal = con.prepareStatement(totalSql);
			statementTotal.setString(1, outletName);
			statementTotal.setString(2, anonymousName);
	        System.out.println("count the total comments number .... ");
	        ResultSet rt = statementTotal.executeQuery();
	        while(rt.next())
	        {
	        	number = rt.getLong(1);
	        }
		}catch(SQLException se){
            try{
                if(statementTotal!=null)
                	statementTotal.close();
             }catch(SQLException se2){
            	 System.err.println("getTotalCommentNumber: ERROR in closing statement!!!");           
             }// nothing we can do
        }        		
        
		return number;
	}
		
	// count the comments number from each non-anonymous users
	private ArrayList<Long> getEachCommentNumber(String outletName, String anonymousName, Connection con)
	{
		ArrayList<Long> eachCommentList = new ArrayList<Long>();
		
		String eachSql = "select AuthorName, count(c.CommentID) as commentNumber from comments c, newsarticle a, newsoutlets o "
				+ "where c.ArticleID = a.ArticleID and a.NewsOutletID = o.NewsOutletID and o.Name = ? and c.AuthorName != ? "
				+ "group by c.AuthorName order by commentNumber desc";				
		
		PreparedStatement statement = null;
        try{
            statement = con.prepareStatement(eachSql);
            statement.setString(1, outletName);
            statement.setString(2, anonymousName);
            System.out.println("count the comments number for each user .... ");
            System.out.println("query: "+ statement.toString());
            ResultSet rs = statement.executeQuery();
            System.out.println("end time: "+new Date());
            while(rs.next())
            {
                long userComment = rs.getLong(2);
                eachCommentList.add(userComment);
            }
        }catch(SQLException se){
            try{
                if(statement!=null)
                    statement.close();
             }catch(SQLException se2){
            	 System.err.println("getEachCommentNumber: ERROR in closing statement!!!");           
             }// nothing we can do
        }        
        System.out.println("return size: " + eachCommentList.size());
		
        return eachCommentList;
	}

}
