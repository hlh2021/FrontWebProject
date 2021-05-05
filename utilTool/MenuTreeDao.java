package utilTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.*;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.DelegatingResultSet;

import struct.TreeJsonData;
import struct.TreeMenu;


public class MenuTreeDao {
	
    
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";      
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/newsdb";   
    private static final String USER_NAME = "root";    
    private static final String PASSWORD = "root";  
    
	private static Statement stmt;
	
	private static Connection conn; 
	
	/** 
     * connect to DB, not come here
     */     
    private Connection getConnection(){ 
    	
    	ReadCSV cf = new ReadCSV();
    	String DATABASE_URL = cf.readConfig().getProperty("DBurl");
    	String USER_NAME =	cf.readConfig().getProperty("DBusername");
    	String PASSWORD =  cf.readConfig().getProperty("DBpwd");
    	
    	System.out.println(DATABASE_URL+" "+USER_NAME+" "+PASSWORD);
        
        try {  
        	Class.forName(DRIVER_CLASS).newInstance();
            conn = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD);  
            System.out.println("******* Connect to database successfully! ******");  
        } catch (Exception e) {  
            System.out.println("DB Connection error! ");  
            System.out.println(e.getMessage());  
        }  
        return conn;  
    } 
    
	
	
	public List getParent() throws NamingException, SQLException{
		
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		PreparedStatement ps = null;

		
//        try {  
//            stmt = getConnection().createStatement();  
//        } catch (Exception e) {   
//            System.out.println(e.getMessage());  
//            return null;  
//        } 
//		java.sql.ResultSet rs =null;
		List list=null;
		String lt=null;
		try {
			String sql ="SELECT * FROM newscategory;";
			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
//			rs= ps.executeQuery();
			
			
			list=new ArrayList();
			while(rs.next()){
				TreeJsonData t=new TreeJsonData();
				t.setId(rs.getLong("NewsCategoryID"));
				t.setText(rs.getString("Name"));
				t.setMonitored(rs.getBoolean("Monitored"));

				
				if(rs.getBoolean("Monitored"))
				{
					t.setstate("opened");
					t.setIcon("icon-ok");
				}
				else
				{
					t.setstate("closed");
				}
				
				
				t.setChildren(getMinType(rs.getLong("NewsCategoryID")));
				list.add(t);
				
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{

		}
		return list;		
	}
	
	
	private boolean StoryHasComments(long long1) {
		// TODO Auto-generated method stub

		int num = 0;		
		Context ctx;
		try {
			ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
			Connection con = ds.getConnection();		
			PreparedStatement ps = null;

			String sql ="SELECT COUNT(*) as 'number' FROM newsstory s, newsline l, newsarticle a"
					    +" where s.NewsStoryID = l.NewsStoryID and l.NewsLineID = a.NewsLineID"
					    +" and s.NewsStoryID = "+long1+" and a.HasComments=1;";
					//		String sql ="SELECT Name FROM newsoutlets;";

			ps=con.prepareStatement(sql);
			
			//ResultSet
			DelegatingResultSet rs =(DelegatingResultSet) ps.executeQuery(sql);  
			
			
			while(rs.next()){
				num = rs.getInt("number");
			}
			
			con.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		if(num > 0)
			return true;
		else
			return false;
	}


	public List getMinType(long l) throws NamingException, SQLException{
		
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/newsdb");
		Connection con = ds.getConnection();
		
		
		PreparedStatement ps = null;
		DelegatingResultSet rs =null;
		List sonlist=null;
		String sonstr=null;
		try {
			String sql ="select * from newsstory where NewsCategoryID="+l+"";
			ps=con.prepareStatement(sql);
			rs= (DelegatingResultSet) ps.executeQuery();
			sonlist=new ArrayList();
			
			while(rs.next()){
				TreeMenu t=new TreeMenu();
				t.setId(rs.getLong("NewsStoryID"));
				t.setText(rs.getString("Name"));
				t.setMonitored(true);
				

				if(StoryHasComments(rs.getLong("NewsStoryID")))
				{
					System.out.println(rs.getLong("NewsStoryID")+"\t"+rs.getString("Name"));
					t.setIcon("icon-ok");
				}
				
				sonlist.add(t);
			}
			/*Gson gson=new Gson();
			sonstr=gson.toJson(sonlist);*/
			//System.out.println(sonstr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(con!=null)
					con.close();
				if(ps!=null)
					ps.close();	
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sonlist;
		
	}
}