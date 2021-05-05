package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import struct.DensityCountJsonData;
import sun.security.util.Debug;
import utilTool.CommentNetworkProperties;
import utilTool.Util;

@WebServlet("/CommentNetworkPropertiesServlet")
public class CommentNetworkPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CommentNetworkPropertiesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		
		String outletName = request.getParameter("outletName");
		long storyID = Long.parseLong(request.getParameter("storyID"));
		Debug.println("Serve",	"CommentNetworkPropertiesServlet: "+outletName+", "+storyID);		
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			CommentNetworkProperties ct = new CommentNetworkProperties();
			ArrayList<Object> returnObject = ct.getDistributionForOutletStory(outletName, storyID);
			@SuppressWarnings("unchecked")
			List<DensityCountJsonData> sizeList = (List<DensityCountJsonData>)(returnObject.get(0));
			@SuppressWarnings("unchecked")
			List<DensityCountJsonData> depthList = (List<DensityCountJsonData>)(returnObject.get(1));
			@SuppressWarnings("unchecked")
			List<DensityCountJsonData> logDepthList = (List<DensityCountJsonData>)(returnObject.get(2));
			@SuppressWarnings("unchecked")
			List<DensityCountJsonData> widthList = (List<DensityCountJsonData>)(returnObject.get(3));
			@SuppressWarnings("unchecked")
			List<DensityCountJsonData> userList = (List<DensityCountJsonData>)(returnObject.get(4));
			Util util = new Util();			
			String sizeStr = util.ReturnJson(sizeList);
			String depthStr = util.ReturnJson(depthList);
			String logDepthStr = util.ReturnJson(logDepthList);
			String widthStr = util.ReturnJson(widthList);
			String userStr = util.ReturnJson(userList);
			jsonStr = sizeStr + ";"  + depthStr + ";"  + logDepthStr+ ";"  + widthStr + ";" + userStr;
			System.out.println("size"+sizeStr);
			System.out.println("depth"+depthStr);
			System.out.println("logDepth"+logDepthStr);
			System.out.println("width"+widthStr);
			System.out.println("userNumber"+userStr);
			//write json file
//			util.WriteFile("Width"+"_"+outletName+"_"+storyID, widthStr);
//			util.WriteFile("UserNumber"+"_"+outletName+"_"+storyID, userStr);
		}catch(Exception e){
			System.out.println("Eror in CommentNetworkPropertiesServlet");
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
