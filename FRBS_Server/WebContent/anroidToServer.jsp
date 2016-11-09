<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("anroidToServer.jsp 호출!!");
	
	request.getParameter("data");

	JSONObject jObject1 = new JSONObject();
	jObject1.put("result", "3");
	out.print(jObject1.toJSONString());
	
%>
