package utilTool;

import java.io.File;  
import java.net.URL;  
  
import java.util.List;

import org.apache.commons.io.FileUtils;

public class testCon {
	
	public static void main(String[] args)
	{
		ReadCSV cf = new ReadCSV();
//		cf.operateCSV("Republican Party");
//		cf.operateCSV("Fire");

		CommentPolarity cp = new CommentPolarity();
		List list = null;
		try{
			list = cp.summaryComment("Republican Party");
		}catch(Exception e){
			System.out.println("Eror");
		}
		if(list!=null)
			System.out.println(list.size());

	}
	
	

}
