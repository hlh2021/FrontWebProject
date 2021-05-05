<%@ page language="java" contentType="text/html; charset=GB2312"
    pageEncoding="GB2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<html>
<head>

<title>使用tomcat自带的数据库连接池</title>
</head>
<body>
 <%
 try{
  Connection conn;
  Statement stmt;
  ResultSet rs;
  //从数据源中获得数据库连接
  Context ctx = new InitialContext();
  DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/mydb");
  conn = ds.getConnection();
  
  //创建一个SQL声明
  stmt = conn.createStatement();
  //查询记录
  rs = stmt.executeQuery("SELECT * FROM mydb.newsstory;");
  //输出查询结果
  out.println("<table border=1 width=600>");
  while (rs.next()){
   int col1 = rs.getInt(1);
   String col2 = rs.getString(2);
   String col3 = rs.getString(3);
   int col4 = rs.getInt(4);
   int col5 = rs.getInt(5);
   int col6 = rs.getInt(6);
   
   //转换字符编码
   //col1 = new String(col1.getBytes("ISO-8859-1"),"GB2312");
   //col2 = new String(col2.getBytes("ISO-8859-1"),"GB2312");
   //col3 = new String(col3.getBytes("ISO-8859-1"),"GB2312");
   
   //打印显示的数据
   out.println("<tr><td>"+col1+"</td><td>"+col2+"</td><td>"+col3+"</td><td>"+col4+"</td><td>"+col5+"</td><td>"+col6+"</td></tr>");
  }
  out.println("</table>");
  
  //关闭结果集、SQL声明和数据库连接
  rs.close();
  stmt.close();
  conn.close();
 }catch(Exception e){
  out.println(e.getMessage());
  e.printStackTrace();
 }
 %>
</body>
</html>