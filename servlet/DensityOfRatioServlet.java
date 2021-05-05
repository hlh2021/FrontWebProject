package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Debug;
import utilTool.DensityOfRatio;
import utilTool.Util;

@WebServlet("/DensityOfRatioServlet")
public class DensityOfRatioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public DensityOfRatioServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
			
		Debug.println("Serve",	"doGet DensityOfRatioServlet");	
		String firstTime = request.getParameter("firstTime");
		String secondTime = request.getParameter("secondTime");
		String storyID = request.getParameter("storyID");		
		System.out.println("storyID: "+ storyID + ", firstTime: " + firstTime + ", secondTime: "+secondTime);		
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			DensityOfRatio ct = new DensityOfRatio();
			List ctList = ct.getDensityList(storyID, firstTime, secondTime);
			
			Util util = new Util();
			jsonStr = util.ReturnJson(ctList);
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in DensityOfRatioServlet");
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
