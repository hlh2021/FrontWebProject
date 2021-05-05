package servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Debug;
import utilTool.CommentLastedTimeRange;
import utilTool.Util;

@WebServlet("/CommentLastedTimeRangeServlet")
public class CommentLastedTimeRangeServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CommentLastedTimeRangeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		String timeRangeIn = request.getParameter("timeRangeIn");
		Debug.println("Serve",	"doGet CommentLastedTimeRangeServlet timeRangeIn:"+timeRangeIn);		
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			CommentLastedTimeRange ct = new CommentLastedTimeRange();
			List ctList = ct.getLastedTimeList(Integer.parseInt(timeRangeIn));
			
			Util util = new Util();
			jsonStr = util.ReturnJson(ctList);
			System.out.println(new Date());
			System.out.println(jsonStr);
			//write json file
			util.WriteFile("commentDuration", jsonStr);
		}catch(Exception e){
			System.out.println("Eror in CommentLastedTimeRangeServlet");
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
