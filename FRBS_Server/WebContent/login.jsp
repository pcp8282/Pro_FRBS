<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("login 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다
		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL, MySQLInfo.USER, MySQLInfo.PASSWORD); 

		String customer_id = request.getParameter("id");
		String pw = request.getParameter("pw");
		JSONObject jObject1 = new JSONObject();

		if (conn != null) {
			String select = "select * from customer" +
				" where customer_id=? and password=?";

			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, customer_id);
			pstmt.setString(2, pw);
			rs = pstmt.executeQuery(); // select문 수행

			if (rs.next()) {// id,pw 일치 하는 경우
				jObject1.put("result", "login_ok");
			} else
				jObject1.put("result", "login_fail");
			
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
