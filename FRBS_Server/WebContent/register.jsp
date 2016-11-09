<%@page import="model.CustomerVO"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("register 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다
		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL, MySQLInfo.USER, MySQLInfo.PASSWORD); 

		CustomerVO cvo = new CustomerVO(request.getParameter("id"), 
						request.getParameter("pw"),
						request.getParameter("phoneNumber"), 
						request.getParameter("email"));
		
		JSONObject jObject1 = new JSONObject();
		
		if (conn != null) {
			String register="Insert into Customer values(?,?,?,?)";
			pstmt=conn.prepareStatement(register);
			pstmt.setString(1,cvo.getCustomer_id());
			pstmt.setString(2,cvo.getPassword());
			pstmt.setString(3,cvo.getPhone_number());
			pstmt.setString(4,cvo.getEmail());
			int result = pstmt.executeUpdate(); // 회원가입 쿼리수행
			
			if(result==1)
				jObject1.put("result", "register_ok");
			else
				jObject1.put("result", "register_fail");
			
		}else {//DB연결 실패
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
