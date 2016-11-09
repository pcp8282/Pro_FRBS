<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("arduinoController.jsp 호출!!");
	
	JSONObject jObject1 = new JSONObject();
	jObject1.put("result", "1");
	out.print(jObject1.toJSONString());
	
%>
