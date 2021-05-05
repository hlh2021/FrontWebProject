package utilTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class CommentWordLength {
	
	public static void main(String[] args)
	{
		HashMap<Integer, Long> lenMap = new HashMap<Integer, Long>();  
		for(int i=1;i<=16;i++)
			lenMap.put(i, (long)0); //initial length, frequency
		HashMap<Integer, Long> uniqMap = new HashMap<Integer, Long>();  
		for(int i=1;i<=16;i++)
			uniqMap.put(i, (long)0); //initial length, frequency
		long totalFreq = 0;
		long vocabSize = 0;
		long totalLen = 0;
		long totalLenUniq = 0;
		
		String filepath = "G:\\paper\\comment crawler paper\\resultData\\AllComment_freq.csv";
		FileReader fileReader = null;  
        BufferedReader bufferedReader = null; 
        String line = "";
        try{       	
        	File file = new File(filepath);
        	fileReader = new FileReader(file);  
            bufferedReader = new BufferedReader(fileReader); 
            // read from the second line
            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            while(line != null)
            {            	
            	String[] wordArray = line.split(",");
            	int length = Integer.parseInt(wordArray[1]);
            	long freq = Long.parseLong(wordArray[3]);
            	totalFreq = totalFreq + freq;
                totalLen = 	totalLen + length*freq;
                totalLenUniq = totalLenUniq + length;
                vocabSize++;
                
                if(length >=15)
                	System.out.println("word: "+wordArray[0]+", length: "+length);
                
                if(freq < 5000)
                	break;
                
                //add to lenMap, uniqMap
                if(lenMap.containsKey(length))
    			{
					long occurrence = lenMap.get(length);
					lenMap.put(length, occurrence+freq);
					long lenUniq = uniqMap.get(length);
					uniqMap.put(length, lenUniq+1);
    			}
    			else
    				System.out.println("word row: "+vocabSize+", length: "+length);
            	
            	//go to next line
            	line = bufferedReader.readLine();
            }//end of each document  
            
            System.out.println("Average word length: " + (double)totalLen/(double)totalFreq);
            System.out.println("Average unique word length: " + (double)totalLenUniq/(double)vocabSize);
            
            //traverse map
            System.out.println("traverse lenMap");
            for (HashMap.Entry <Integer, Long> entry : lenMap.entrySet())
            	System.out.println(entry.getValue());
            System.out.println("traverse uniqMap");
            for (HashMap.Entry <Integer, Long> entry : uniqMap.entrySet())
            	System.out.println(entry.getValue());
            
            System.out.println("vocabSize: "+vocabSize);
            
        	
        }catch(Exception e){
			System.out.println("Fail to read input file!\n"+e);
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
	}


}
