package utilTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.Properties;
import com.google.gson.Gson;

public class Util {
	
	public Connection dbConnection()
	{
		Connection con = null;
//		String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
		String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		ReadCSV cf = new ReadCSV();
		Properties p = cf.readConfig();
    	String DATABASE_URL = p.getProperty("DBurl");
    	String DATABASE_NAME = p.getProperty("DBname");
    	String USER_NAME =	p.getProperty("DBusername");
    	String PASSWORD =  p.getProperty("DBpwd");  
    	try{
//    		Class.forName(DRIVER_CLASS).newInstance();
//    		con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD);
    		Class.forName(DRIVER_CLASS); 		
    		con = DriverManager.getConnection(DATABASE_URL+";databaseName="+DATABASE_NAME, USER_NAME, PASSWORD);
    		System.out.println("Connect DataBase!");
    	}catch(Exception e)
    	{
    		System.out.println("Fail to connect DataBase!");
    	}
    	return con;
	}
	
	public boolean dbClose(Connection con) 
	{
		boolean closeResult = false; 
		try{
			con.close();
			closeResult = true;
		} catch(Exception e){
			System.out.println(e);
		}
		if (closeResult)
			return true;
		else
			return false;
	}
	
	public Connection mysqlConnection()
	{
		Connection con = null;
		String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
    	String DATABASE_URL = "jdbc:mysql://localhost:3306/news1702";
//		String DATABASE_URL = "jdbc:mysql://129.32.94.230:3306/newsdb"; //lab computer 301
//		String DATABASE_URL = "jdbc:mysql://10.109.79.158:3306/newsdb"; //laptop
//		String DATABASE_URL = "jdbc:mysql://155.247.182.200:3306/newsdb"; //eduard's server, failed
    	String USER_NAME =	"root";
    	String PASSWORD =  "root";
		//connect to mydb on Guo's server
//		String DATABASE_URL = "jdbc:mysql://129.32.100.98:3306/mydb"; //failed
//    	String USER_NAME =	"dragroot";
//    	String PASSWORD =  "zeiKeiw9";
    	try{
    		Class.forName(DRIVER_CLASS).newInstance();
    		con = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD);
    		System.out.println("Connect DataBase!");
    	}catch(Exception e)
    	{
    		System.out.println("Fail to connect mysql DataBase!\n"+e);
    	}
    	return con;
	}
	
	public String ReturnJson(List str)
	{
//		String JsonString = null;
		
		Gson gson = new Gson();
		String JsonString = gson.toJson(str);
//		System.out.println("========================\t1\t===============================\n");
//		System.out.println(str2);
//		System.out.println("=========================\t1\t==============================\n");
		
		return JsonString;
		
	}
	
	public void WriteFile(String filename,String str)
	{
		System.out.println(filename+" json file path: ");
//        String filePath = "D:\\Users\\Administrator\\workspace\\FrontWebProject\\WebContent\\json\\"+filename+".json";
		//D:/Users/Administrator/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/FrontWebProject/WEB-INF/classes/
		ReadCSV read = new ReadCSV();
		String dir = read.getProjectPath();
		int endIndex = dir.indexOf("WEB-INF");
		String proDir = dir.substring(0, endIndex);
		proDir = proDir.replaceAll("%20", " ");
		String filePath = proDir+"json/"+filename+".json";
		System.out.println(filePath);
        FileWriter fw = null;
		try {
			fw = new FileWriter(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("fw error\t"+fw);
			e.printStackTrace();
		}
		
        PrintWriter out = new PrintWriter(fw);
        out.write(str);
//        System.out.println("Str here is:\t"+str);
        out.println();
        try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out.close();
		
	}
	
	public void writeFileAppending(String path, String str)
	{
		if(str.length()>0)
		{
			File file = new File(path);
			try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
				bw.write(str);
				bw.newLine();
				bw.flush();
				bw.close();
			}catch(Exception e){
				System.out.println("Error in writing file "+path);
			}		
		}
	}
	
	
	public String removeTag(String htmlStr)
	{
	    String textStr = "";  
	    Pattern p_script;  
	    Matcher m_script;  
	    Pattern p_style;  
	    Matcher m_style;  
	    Pattern p_html;  
	    Matcher m_html; 
	    
		//regex for script { or <script[^>]*?>[\\s\\S]*?<\\/script>  
        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";   
        //regex for style { or <style[^>]*?>[\\s\\S]*?<\\/style>  
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; 
        // regex for html tag
        String regEx_html = "<[^>]+>";
        
        //remove all special character
        String regEx_special = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        
        //only keep alphabet and number
        String  regEx_nonAlp_nonNumb  =  "[^a-zA-Z0-9]";
        
        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
        m_script = p_script.matcher(htmlStr);  
        htmlStr = m_script.replaceAll(""); // remove script tag
        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
        m_style = p_style.matcher(htmlStr);  
        htmlStr = m_style.replaceAll(""); // remove style tag
        
        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
        m_html = p_html.matcher(htmlStr);  
        htmlStr = m_html.replaceAll(""); // remove html tag
        textStr = htmlStr; 
//        System.out.println(textStr.length()+", "+textStr);
        return textStr;
	}
	
	
	public ArrayList<String> readAIDsFromTxt(String filepath)
	{
		ArrayList<String> idList = new ArrayList<String>();
		try {
            File f = new File(filepath);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            while ((readLine = b.readLine()) != null) 
            {
                if(readLine.startsWith("ID"))
                	continue;
                //extract ID value
                int endIdx = readLine.indexOf(";");
                String idVal = readLine.substring(0, endIdx);
                idList.add(idVal);
            }
            
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return idList;
	}
	
	
	public static void main (String[] args)
	{
		Util util = new Util();
		Connection con = util.mysqlConnection();
		
	}
	

	

}
