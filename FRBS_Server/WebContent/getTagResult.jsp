<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다
		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL, MySQLInfo.USER, MySQLInfo.PASSWORD); 

		int reserveNo = Integer.parseInt(request.getParameter("reserve_no"));
		
		JSONObject jObject1 = new JSONObject();

		if (conn != null) {
			String select = "select door_check, seat_check from reservation where reserve_no=?";

			pstmt = conn.prepareStatement(select);
			pstmt.setInt(1, reserveNo);			
			rs = pstmt.executeQuery(); // select문 수행	
			rs.next();
		 
			String door = rs.getString(1);
			String seat = rs.getString(2);
			
			if(null == door)
				door = "N";
			if(null == seat)
				seat = "N";
		 	jObject1.put("result", door+"`"+seat);
		} else {//DB연결 실패
			jObject1.put("result", "connect_fail");
		}

		out.print(jObject1.toJSONString());

	} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
		e.printStackTrace();
	} finally {
		if (rs != null)
			rs.close();
		if (pstmt != null)
			pstmt.close();
		if (conn != null)
			conn.close();
	}
%>
