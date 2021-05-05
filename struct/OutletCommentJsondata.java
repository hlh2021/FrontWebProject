package struct;

public class OutletCommentJsondata {
	String NewsWebsite;
	int day1;  //today
	int day2;  // one day ago
	int day3;
	int day4;
	int day5;   //four day ago
	int sum;
	
	public String getNewsWebsite() {
		return NewsWebsite;
	}
	public void setNewsWebsite(String l) {
		this.NewsWebsite = l;
	}
	
	public int getday1() {
		return day1;
	}
	public void setday1(int l) {
		this.day1 = l;
	}
	
	public int getday2() {
		return day2;
	}
	public void setday2(int l) {
		this.day2 = l;
	}
	
	public int getday3() {
		return day3;
	}
	public void setday3(int l) {
		this.day3 = l;
	}
	
	public int getday4() {
		return day4;
	}
	public void setday4(int l) {
		this.day4 = l;
	}
	
	public int getday5() {
		return day5;
	}
	public void setday5(int l) {
		this.day5 = l;
	}
	public void setsum(int total) {
		// TODO Auto-generated method stub
		this.sum = total;
	}

	public int getsum(int total) {
		// TODO Auto-generated method stub
		return total;
	}
}
