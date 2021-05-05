package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Debug;
import utilTool.ArticleVolume;
import utilTool.Util;

@WebServlet("/ArticleVolumeServlet")
public class ArticleVolumeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ArticleVolumeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		
		String topNumber = request.getParameter("topNumber");
		Debug.println("Serve",	"doGet topNumber: "+topNumber);		
		response.setContentType("text/html;charset=UTF-8");	
		//the string transfer to jsp
		String jsonStr = "";
		
		try{
			ArticleVolume ct = new ArticleVolume();
			List ctList = ct.getArticleVolumeList(Integer.parseInt(topNumber));
			
			Util util = new Util();
			jsonStr = util.ReturnJson(ctList);
			System.out.println(jsonStr);
		}catch(Exception e){
			System.out.println("Eror in ArticleVolumeServlet");
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
