package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Debug;
import utilTool.ContinuedTime;
import utilTool.Util;

@WebServlet("/ContinuedHoursServlet")
public class ContinuedHoursServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ContinuedHoursServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		Debug.println("Serve",	"doGet ContinuedHoursServlet");	
		String sinceDate = request.getParameter("sinceDate");
		int fromHour = Integer.parseInt(request.getParameter("fromHour"));
		int toHour = Integer.parseInt(request.getParameter("toHour"));
		int bucketHour = Integer.parseInt(request.getParameter("bucketHour"));
		System.out.println("sinceDate:"+sinceDate+", fromHour"+fromHour+", toHour"+toHour+", bucketHour"+bucketHour);
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			ContinuedTime ct = new ContinuedTime();
			List ctList = ct.getContinuedHourList(sinceDate, fromHour, toHour, bucketHour);
			
			Util util = new Util();
			jsonStr = util.ReturnJson(ctList);
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in ContinuedHoursServlet");
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
