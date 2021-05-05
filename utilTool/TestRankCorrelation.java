package utilTool;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.linear.RealMatrix;

public class TestRankCorrelation {
	
	public static void main(String[] args)
	{	
		TestRankCorrelation cor = new TestRankCorrelation();		
		
		cor.calculateANDCG();
	}
	
	public void test()
	{
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("149"); list1.add("247"); list1.add("358"); list1.add("37"); list1.add("428");
		list1.add("732"); list1.add("976"); list1.add("1160"); list1.add("977"); list1.add("40");
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("149"); list2.add("247"); list2.add("358"); list2.add("1160"); list2.add("977"); 
		list2.add("579"); list2.add("428"); list2.add("1134"); list2.add("37"); list2.add("67"); 
		ArrayList<String> list3 = new ArrayList<String>();
		list3.add("149"); list3.add("247"); list3.add("358"); list3.add("428"); list3.add("12"); 
		list3.add("976"); list3.add("37"); list3.add("10"); list3.add("40"); list3.add("1160"); 
		int k = 10;
		HashMap<String, Integer> scoreMap = createStoryScoreMap(list1, k);
		double DCG_article = getDCGinTop(list1, k, scoreMap);
		double DCG_comment = getDCGinTop(list2, k, scoreMap);
		double DCG_user= getDCGinTop(list3, k, scoreMap);
		//ANDCG for each list
		double ANDCG_comment = DCG_comment / DCG_article;
		double ANDCG_user = DCG_user / DCG_article;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		System.out.println(df.format(DCG_article));
		System.out.println(df.format(DCG_comment));
		System.out.println(df.format(DCG_user));
		System.out.println(df.format(ANDCG_comment));
		System.out.println(df.format(ANDCG_user));
	}
	
	
	public void calculateANDCG()
	{
		ArrayList<ArrayList<String>> storyList = readExcel("G:\\paper\\resultData\\storyRankList.xls");
		ArrayList<String> listByArticle = storyList.get(0);
		ArrayList<String> listByComment = storyList.get(1);
		ArrayList<String> listByUser = storyList.get(2);
		System.out.println("At first, story size by article: "+listByArticle.size());
		System.out.println("At first, story size by comment: "+listByComment.size());
		System.out.println("At first, story size by user: "+listByUser.size());
		
		//top number for story list
		int[] lengthList = new int[70];
		lengthList[0] = 5;
		for(int i=1; i<70; i++)
			lengthList[i] = 10*i;
//		lengthList[21] = 695;
		
		for(int index=0; index<lengthList.length; index++)
		{
			//get each story list at top k
			int k = lengthList[index];
//			System.out.println("Top "+k+" : ");
			HashMap<String, Integer> scoreMap = createStoryScoreMap(listByUser, k);
//			System.out.println("size of score map: "+scoreMap.size());
			double DCG_article = getDCGinTop(listByArticle, k, scoreMap);
			double DCG_comment = getDCGinTop(listByComment, k, scoreMap);
			double DCG_user= getDCGinTop(listByUser, k, scoreMap);
			//ANDCG for each list
			double ANDCG_comment = DCG_article / DCG_user;
			double ANDCG_user = DCG_comment / DCG_user;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
//			System.out.println(df.format(ANDCG_comment));
			System.out.println(df.format(ANDCG_user));
		}
	}
	
	
	public HashMap<String, Integer> createStoryScoreMap(ArrayList<String> idealList, int k)
	{
		HashMap<String, Integer> idScoreMap = new HashMap<String, Integer>();
		for(int i=0; i<k; i++)
		{
			//story id as the key
			String id = idealList.get(i);
			int score = k - i;
			idScoreMap.put(id, score);
		}
		return idScoreMap;
	}
	
	//DCG = s1 + sum( s(i)/log(i) ) for i>=2
	public double getDCGinTop(ArrayList<String> list, int k, HashMap<String, Integer> scoreMap)
	{
		double DCG = 0;
		//compute for top k stories in the list
		for(int i=0; i<k; i++)
		{
			String id = list.get(i);
			int score = 0;
			//get the score for current id according to the scoreMap
			if(scoreMap.containsKey(id))
				score = scoreMap.get(id);
			//cumulative sum
			if(i == 0)
				DCG = DCG + (double)score;
			else
				DCG = DCG + (double)score / (Math.log(i+1)/Math.log(2));
		}
		
		return DCG;
	}
	
	public void calculateKendell()
	{
		ArrayList<ArrayList<String>> storyList = readExcel("G:\\paper\\resultData\\storyRankList.xls");
		ArrayList<String> listByArticle = storyList.get(0);
		ArrayList<String> listByComment = storyList.get(1);
		ArrayList<String> listByUser = storyList.get(2);
		System.out.println("At first, story size by article: "+listByArticle.size());
		System.out.println("At first, story size by comment: "+listByComment.size());
		System.out.println("At first, story size by user: "+listByUser.size());
		
		//ensure story appear in each list
		for(int a=0; a<listByArticle.size(); a++)
		{
			String storyInArt = listByArticle.get(a);
			int indexInCom = findStoryIndex(listByComment, storyInArt);
			int indexInUser = findStoryIndex(listByUser, storyInArt);
			//remove if story id not appear in comment or user
			if(indexInCom<0 || indexInUser<0)
			{
				listByArticle.remove(a);
				a--;
				if(indexInCom >= 0)
					listByComment.remove(indexInCom);
				if(indexInUser >= 0)
					listByUser.remove(indexInUser);
			}			
		}
		System.out.println("After removing, story size by article: "+listByArticle.size());
		System.out.println("After removing, story size by comment: "+listByComment.size());
		System.out.println("After removing, story size by user: "+listByUser.size());
		
		//create storyID-order map according to the list by article
		HashMap<String, Integer> idMap = createStoryOrderMap(listByArticle);
//		for(HashMap.Entry<String, Integer> entry : idMap.entrySet())
//			System.out.println(entry.getKey()+" - "+entry.getValue());
				
		//covert story list by article to the order array
		double[] orderArrayByArticle = convertIdToOrderArray(idMap, listByArticle);
		double[] orderArrayByComment = convertIdToOrderArray(idMap, listByComment);
		double[] orderArrayByUser = convertIdToOrderArray(idMap, listByUser);
		
		//top number for story list
		int[] lengthList = new int[12];
		lengthList[0] = 5;
		for(int i=1; i<11; i++)
			lengthList[i] = 10*i;	
		lengthList[11] = listByArticle.size();
		for(int index=0; index<lengthList.length; index++)
		{
			//get each story list at top k
			int k = lengthList[index];
			double[] arrayByArticle = new double[k];
			double[] arrayByComment = new double[k];
			double[] arrayByUser = new double[k];						
			System.arraycopy(orderArrayByArticle, 0, arrayByArticle, 0, k);
			System.arraycopy(orderArrayByComment, 0, arrayByComment, 0, k);
			System.arraycopy(orderArrayByUser, 0, arrayByUser, 0, k);
			
			//calculate the Kendalls Tau Coefficient
			System.out.println("Top "+k+" Stories:");
			calKendallsCorrelation(arrayByArticle, arrayByComment);
			calKendallsCorrelation(arrayByArticle, arrayByUser);
			calKendallsCorrelation(arrayByComment, arrayByUser);
			
		}//end of each top k
		
	}
	
	public HashMap<String, Integer> createStoryOrderMap(ArrayList<String> list)
	{
		HashMap<String, Integer> idMap = new HashMap<String, Integer>();
		for(int i=0; i<list.size(); i++)
		{
			//story id as the key
			String id = list.get(i);
			idMap.put(id, i+1);
		}
		return idMap;
	}
	
	public double[] convertIdToOrderArray(HashMap<String, Integer> idMap, ArrayList<String> idList)
	{
		double[] orderArray = new double[idList.size()];
		for(int index=0; index<idList.size(); index++)
		{
			String id = idList.get(index);
			double order = (double)idMap.get(id);
			orderArray[index] = order;
		}
		
		return orderArray;
	}
	
	public int findStoryIndex(ArrayList<String> list, String storyID)
	{
		int res = -1;
		for(int i=0; i<list.size(); i++)
		{
			String curID = list.get(i);
			if(curID.equals(storyID))
			{
				//find the story in the list
				res = i;
				break;
			}				
		}
		
		return res;
	}
	
	public void calKendallsCorrelation(double[] xArray, double[] yArray)
	{
		KendallsCorrelation cor = new KendallsCorrelation();
		
//		double[][] matrix = {{649, 1452}, {1456, 1456}, {1452, 649}};
//		RealMatrix mat = cor.computeCorrelationMatrix(matrix);		
		
		double coefXY = cor.correlation(xArray, yArray);		
		System.out.println(coefXY);
	}
	
	public ArrayList<ArrayList<String>> readExcel(String filepath)
	{
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
		ArrayList<String> listByArticle = new ArrayList<String>();
		ArrayList<String> listByComment = new ArrayList<String>();
		ArrayList<String> listByUser = new ArrayList<String>();
       
        try 
        {         
        	InputStream is = new FileInputStream(filepath);          	
        	Workbook book = Workbook.getWorkbook(is); 

            //index: 0,1,2,3,....)
        	Sheet sheet=book.getSheet(0);           
            int rows = sheet.getRows(); //index begin with 0
//            int rows = 697;
            for(int i=2; i<rows; i++)
            {
                //get the content in column and row, column index begin with 0
            	Cell cell1 = sheet.getCell(0,i);// rank by article
            	Cell cell2 = sheet.getCell(4,i);// by comment
            	Cell cell3 = sheet.getCell(8,i);//by user                        
                String storyByArticle = cell1.getContents();
                String storyByComment = cell2.getContents();
                String storyByUser = cell3.getContents();
                //add story id to each list
                if(storyByArticle!=null && storyByArticle.length()>0)
                	listByArticle.add(storyByArticle);
                if(storyByComment!=null && storyByComment.length()>0)
                	listByComment.add(storyByComment);
                if(storyByUser!=null && storyByUser.length()>0)
                	listByUser.add(storyByUser);
            }
            book.close(); 
        }
        catch(Exception e)  { 
        	System.out.println("Error in ReadExcel!\n"+e);
        } 
        
        resultList.add(listByArticle);
        resultList.add(listByComment);
        resultList.add(listByUser);
        return resultList;
	}

}