<%@page import="model.CustomerVO"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("reserve.jsp 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다
		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL, MySQLInfo.USER, MySQLInfo.PASSWORD); 

		/*
		request.getParameter("id")
		request.getParameter("train_no")
		
		*/
		
		
		String customer_id = "pcp8282";
		int trainId = 1;
		int trainNo = 1;
		String seatNo = "A4";
		JSONObject jObject1 = new JSONObject();
		
		if (conn != null) {
			String reserve= "Insert into Reservation(Customer_Id,Train_Id,Train_No,Seat_No) values(?,?,?,?)";
			pstmt=conn.prepareStatement(reserve);
			pstmt.setString(1,customer_id);
			pstmt.setInt(2,trainId);
			pstmt.setInt(3,trainNo);
			pstmt.setString(4,seatNo);
			int result = pstmt.executeUpdate(); // 예매쿼리수행
			
			if(result==1){
				// 예매 성공했음으로 seating_status테이블 좌석체크 컬럼 Y로 바꿔줌
				String updateSeatStaus = "Update seating_status set Seating_check='Y'" 
									+" where Train_id=? and Train_no=? and Seat_No=?";
				pstmt2=conn.prepareStatement(updateSeatStaus);
				pstmt2.setInt(1,trainId);
				pstmt2.setInt(2,trainNo);
				pstmt2.setString(3,seatNo);
				pstmt2.executeUpdate();
				jObject1.put("result", "reserve_ok");
			}
			else
				jObject1.put("result", "reserve_fail");
			
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
		if (pstmt2 != null)
			pstmt2.close();
		if (conn != null)
			conn.close();
	}
%>
