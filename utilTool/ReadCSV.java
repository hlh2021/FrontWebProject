package utilTool;

import org.apache.commons.io.FileUtils;

import java.util.Properties;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadCSV {
	
	public String getProjectPath()
	{
		String dir = this.getClass().getResource("/").getPath();
		return dir;
	}
	
	public List operateCSV(String story)
	{
		List datalist = new ArrayList<String[]>();
		String storyStr = story.replaceAll(" ", "_");
//		String localpath = "D:/Users/Administrator/workspace/FrontWebProject/WebContent/csv/correlate-"+storyStr+".csv";		
		//get the path of this class="/D:/Users/Administrator/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/FrontWebProject/WEB-INF/classes/"
		String dir = getProjectPath();
		int endIndex = dir.indexOf("WEB-INF");
		String proDir = dir.substring(0, endIndex);
		proDir = proDir.replaceAll("%20", " ");
		String localpath = proDir+"csv/correlate-"+storyStr+".csv";
		System.out.println("csv path: \n"+localpath);		
//		String testURL = "http://images.17173.com/2010/www/roll/201003/0301sohu01.jpg";
		Properties p = readConfig();
		String url = p.getProperty("CSVurl");	
		int firstloc = url.indexOf("?");
		int lastloc = url.indexOf("&");
		String str1 = url.substring(0, firstloc+3);
		String str2 = url.substring(lastloc);		
		String downURL = str1+story+str2;
		System.out.println("CSV file downURL: "+downURL);
		
		File csv_file = new File(localpath);
		if(!csv_file.exists())  //if not exist, download a new csv file
		{
			//downloadFromUrl(downURL, csv_file);
			
		}
		else
		{
			//read this csv file
			datalist = readFile(csv_file);
			System.out.println("csv data size in 2015: "+datalist.size());
		}
				
		return datalist;
	}
	
	
	public Properties readConfig()
	{
		String filepath = "config.properties";//put config under src, the same direction with com after building
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filepath);
		Properties p = new Properties();
		try{
			p.load(inputStream); 
			//System.out.println("Read config file successfully!");
		}catch (IOException e) {
			System.out.println("Read config file error!");
			e.printStackTrace();
		}

		return p;
	}
	
	//download file from url to local path
	public void downloadFromUrl(String url,File f)
	{	
		try{
			URL httpurl = new URL(url); 
			f.createNewFile();			 
	        FileUtils.copyURLToFile(httpurl, f);
	        System.out.println("Success to download "+f);
		}catch(Exception e){
			 System.out.println("Fail to download "+f);
		 }
		
	}
	
	public ArrayList<String[]> readFile(File csv_file)
	{
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		FileReader fileReader = null;  
        BufferedReader bufferedReader = null;  
        
		try{
			fileReader = new FileReader(csv_file);  
            bufferedReader = new BufferedReader(fileReader); 
	        for(int i=0;i<12;i++)
	        	bufferedReader.readLine();//the first 12 line has no use
	        String line = null;
	      	        
	        while((line=bufferedReader.readLine())!=null)
	        { 	        	
	        	if(line.length()>=1)
	        	{
	        		 String item[] = line.split(",");
		        	 String[] strArray = new String[2];
		        	 String comDate = "2015-01-01";
		        	 int isLarge = item[0].compareTo(comDate);
//		        	 System.out.println("is Large "+isLarge + " " +item[0]);
		        	 if(isLarge>=0)
		        	 {
		        		 strArray[0] = item[0];
			        	 strArray[1] = item[1];
			        	 dataList.add(strArray);
		        	 }		        	 		        	
	        	}	        		        		
	        }
//	        System.out.println("in readFile "+dataList.size());	        
		}catch(Exception e){
			System.out.println("Fail to read this csv file!!!\n"+e);
		}finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
                if (fileReader != null) {  
                    fileReader.close();  
                }  
            } catch (IOException e) {  
            	System.out.println("Fail to close reader!\n"+e);
            }  
        }  
		
		return dataList;
	}
	
	

}
