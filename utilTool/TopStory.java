package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TopStory {
	
	public ArrayList<String[]> getTopStoryByComments(int topNumber) throws Exception
	{
		ArrayList<String[]> storyList = new ArrayList<String[]>();
		/***
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		***/
		Util util = new Util();
		Connection con = util.dbConnection();
		if(con == null)
			return storyList;
		
//		String sql = "SELECT s.NewsStoryID, s.Name, count(c.CommentID) as c FROM comments c, newsarticle a, newsline l, newsstory s "
//				+ "where c.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=s.NewsStoryID "
//				+ "group by s.NewsStoryID, s.Name order by c desc";
		String sql = "SELECT s.NewsStoryID, s.Name, sum(z.EndCrawlCommentCount) as c FROM newsarticle a, articlestatistics z, newsline l, newsstory s "
				+ "where z.ArticleID=a.ArticleID and a.NewsLineID=l.NewsLineID and l.NewsStoryID=s.NewsStoryID "
				+ "group by s.NewsStoryID, s.Name order by c desc";
		PreparedStatement statement = null;
		try{
			statement = con.prepareStatement(sql);
			System.out.println("sort stories by comments in getTopStoryByComments .....");
			ResultSet rs = statement.executeQuery();			
			
			int i = 1;
			while(rs.next())
			{
				if(i > topNumber)
					break;
				
				//add the top story into the list
				long id = rs.getLong(1);
				String name = rs.getString(2);
				String[] record = new String[2];
				record[0] = String.valueOf(id);
				record[1] = name;	
				storyList.add(record);
				
				i++;
			}
			
			if(rs != null)
				rs.close();
			if(statement != null)
				statement.close();
			
		}catch(SQLException se){
            try{
                if(statement!=null)
                    statement.close();
             }catch(SQLException se2){
            	 System.err.println("getTopStoryByComments: ERROR in closing statement!!!");           
             }// nothing we can do
        }   
		
		return storyList;
	}
	

}
