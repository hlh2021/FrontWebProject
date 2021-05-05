package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import struct.CommentStoryInGoogleNewsJsonData;
import sun.security.util.Debug;
import utilTool.CommentStoryInGoogleNews;
import utilTool.Util;


@WebServlet("/CommentPercentageInGoogleNewsServlet")
public class CommentPercentageInGoogleNewsServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CommentPercentageInGoogleNewsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		
		Debug.println("Serve",	"doGet CommentPercentageInGoogleNewsServlet");		
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			CommentStoryInGoogleNews ct = new CommentStoryInGoogleNews();
			List<ArrayList<CommentStoryInGoogleNewsJsonData>> resList = ct.getCommentDistribution();
			List appearList = resList.get(0);
			List disappearList = resList.get(1);
			
			Util util = new Util();
			String appJsonStr = util.ReturnJson(appearList);
			String disappJsonStr = util.ReturnJson(disappearList);
			jsonStr = appJsonStr + ";" + disappJsonStr;
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in CommentPercentageInGoogleNewsServlet");
		}
		 
		//send the data to JSP  
		response.getWriter().write(jsonStr);
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//System.out.println("dopost\n");
		doGet(request, response);
	}

}