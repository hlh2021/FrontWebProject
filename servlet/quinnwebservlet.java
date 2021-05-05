package servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import utilTool.MenuTreeDao;
import utilTool.SummaryDatagrid;
import utilTool.Util;

/**
 * Servlet implementation class quinnwebservlet
 */
@WebServlet("/quinnwebservlet")
public class quinnwebservlet extends HttpServlet implements javax.servlet.Servlet {
//	private static final long serialVersionUID = 1L;
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
    public quinnwebservlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!        doGet         !!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
//		
//		
		this.doPost(request, response);
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/html;charset=UTF-8");
//		PrintWriter out = response.getWriter();
		List str = null;
		try {
			str = new MenuTreeDao().getParent();
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Util util = new Util();
		String str2 = util.ReturnJson(str);
		
		util.WriteFile("tree", str2);
		
		List summary = null;
		SummaryDatagrid sd = new SummaryDatagrid();
		try {
			summary = sd.Summary();
		} catch (Exception e) {
			System.out.println(e);
		}
		String sumdg = util.ReturnJson(summary);
		//System.out.println(sumdg);
		
		String columnsdata = "";
		
		Date myDate = new Date();
		Calendar c= Calendar.getInstance();
		c.setTime(myDate);
		for(int i=0; i<5; i++)
		{
			int dayi = 0-i;
			c.add(c.DATE, dayi);
			myDate = c.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
			String dateString = formatter.format(myDate);
			 
			
			int num = i+1;
			columnsdata="{field:'day"+num+"',title:'"+dateString+"',width:100},";
		}
		
		columnsdata= columnsdata.substring(0,columnsdata.length()-1);
		
		
//		sumdg = "{ options:{ "
//				+ "fitColumns:true,"
//				+ "columns:[["+columnsdata
//				+ "]],"
//				+ "data:"+sumdg
//				+ "}}";
		
		util.WriteFile("summary", sumdg);
		
		
//		Gson gson = new Gson();
//		String str2 = gson.toJson(str);
//		System.out.println("========================\t1\t===============================\n");
//		System.out.println(str2);
//		System.out.println("=========================\t1\t==============================\n");
		
		
        //E:\JAVA workspace\FrontWebProject\WebContent 
//        
//        String filePath = "E:\\JAVA workspace\\FrontWebProject\\WebContent\\json\\tree.json";
//        FileWriter fw = new FileWriter(filePath);
//        PrintWriter out = new PrintWriter(fw);
//        out.write(str2);
//        out.println();
//        fw.close();
//        out.close();
        
		
        request.getRequestDispatcher("MainPage.jsp").forward(request, response);  
		
//		doGet(request, response);
	}
	


}
