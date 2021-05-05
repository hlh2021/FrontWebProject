package struct;

import java.util.Date;


public class CommentPolarityJsonData {
	
	String date;  
	long totalNumber; 
	long posiNumber;
	long negaNumber;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(long totalNumber) {
		this.totalNumber = totalNumber;
	}
	public long getPosiNumber() {
		return posiNumber;
	}
	public void setPosiNumber(long posiNumber) {
		this.posiNumber = posiNumber;
	}
	public long getNegaNumber() {
		return negaNumber;
	}
	public void setNegaNumber(long negaNumber) {
		this.negaNumber = negaNumber;
	}
	

}
