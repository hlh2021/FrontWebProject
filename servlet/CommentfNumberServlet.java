package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import struct.CommentJsonData;
import sun.security.util.Debug;
import utilTool.ArticleVolume;
import utilTool.CommentNumber;
import utilTool.Util;

@WebServlet("/CommentfNumberServlet")
public class CommentfNumberServlet extends HttpServlet {
	
private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CommentfNumberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		Debug.println("Serve",	"doGet CommentfNumberServlet");	
//		String story = request.getParameter("story");
//		String fromDate = request.getParameter("fromDate");
//		String toDate = request.getParameter("toDate");
		String storyID = request.getParameter("storyID");
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			CommentNumber ct = new CommentNumber();
//			ArrayList<Object> returnObject = ct.getCommentNumberOverTime(story, fromDate, toDate);
			ArrayList<Object> returnObject = ct.getCommentNumberOverTime(Long.parseLong(storyID));
			@SuppressWarnings("unchecked")
			List<CommentJsonData> ctList = (List<CommentJsonData>)(returnObject.get(0));
			int storyAppearNumber = (int)(returnObject.get(1));
			int storyEndNumber = (int)(returnObject.get(2));
			
			Util util = new Util();
			jsonStr = util.ReturnJson(ctList);
			jsonStr = jsonStr + ";" + storyAppearNumber + ";" + storyEndNumber;
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in Comment NumberServlet");
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
