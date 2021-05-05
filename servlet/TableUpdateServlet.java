package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Debug;
import utilTool.CommentPolarity;
import utilTool.ReadCSV;
import utilTool.SummaryDatagrid;
import utilTool.Util;

/**
 * Servlet implementation class TableUpdateServlet
 */
@WebServlet("/TableUpdateServlet")
public class TableUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TableUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		
		String ChoseStory = request.getParameter("ChoseStory");
		//System.out.println(ChoseStory+"\n");
		Debug.println("Serve",	"doGet Story: "+ChoseStory);
		
		response.setContentType("text/html;charset=UTF-8");
				
		List summary = null;
		SummaryDatagrid sd = new SummaryDatagrid();
		try {
			summary = sd.UpDateDatagrid(ChoseStory);
		} catch (Exception e) {
			System.out.println(e);
		}
		Util util = new Util();
		String sumdg = util.ReturnJson(summary);
//		System.out.println("sumdg data:\n"+sumdg);
//		util.WriteFile("summary", sumdg);
		
		ReadCSV csvR = new ReadCSV();
		List csvList = csvR.operateCSV(ChoseStory);
		String csvStr = util.ReturnJson(csvList);
//		System.out.println("csv data:\n"+csvStr);
		
		CommentPolarity cp = new CommentPolarity();
		List comlist = null;
		try{
			comlist = cp.summaryComment(ChoseStory);
		}catch(Exception e){
			System.out.println("Eror");
		}
		String comStr = util.ReturnJson(comlist);
//		System.out.println("comment data:\n"+comStr);
		
		//the string transfer to jsp
		String totalStr = sumdg+";"+csvStr+";"+comStr;
		
		//response.setHeader("Refresh","1;url=FrontWebpage.jsp");  
		 //request.getRequestDispatcher("FrontWebpage.jsp").forward(request, response);  
		//send the data to JSP  
		response.getWriter().write(totalStr);
		 
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
