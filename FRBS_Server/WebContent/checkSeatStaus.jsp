<%@page import="model.CustomerVO"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("checkSeatStaus.jsp 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	PreparedStatement pstmt3 = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다

		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL,
				MySQLInfo.USER, MySQLInfo.PASSWORD);

		// 테스트용 실제할때 request.getParameter(""); 변경해서 사용!
		/*
		request.getParameter("id")
		request.getParameter("train_no")
		 */
		
		int trainId = Integer.parseInt(request.getParameter("train_id"));
		int trainNo = Integer.parseInt(request.getParameter("train_no"));
		String seatNo = request.getParameter("seat_no");
		int reserveNo = Integer.parseInt(request.getParameter("reserve_no"));
		/////////////////////////////////////////////////////
		System.out.println("flag::"+request.getParameter("flag"));
		JSONObject jObject1 = new JSONObject();

		if (conn != null) {
			
			// 인증한 예매 티켓정보 seat_check 컬럼 update
			
			String flag = request.getParameter("flag");
			
			String select = "select *  from train_schedule " +
						"where Train_Id =(select Train_Id from reservation where Reserve_No=?)"
						+ " and Departure_date=CURDATE() and curtime() between DATE_SUB(departure_time, INTERVAL 90 minute) and time(Arrival_time)";
			pstmt3 = conn.prepareStatement(select);
			
			pstmt3.setInt(1, reserveNo);
			rs = pstmt3.executeQuery(); // select문 수행
			
			if(!rs.next()){
				flag = "F";
			}
			
			String update = "Update reservation set seat_check=?"
					+ " where reserve_no=?";
			pstmt2 = conn.prepareStatement(update);
			pstmt2.setString(1, flag);
			pstmt2.setInt(2, reserveNo);
			pstmt2.executeUpdate();
			
			// 스캐너 해당 자리 성공, Fail 처리
			String updateSeatStaus = "Update seating_status set Seating_check=?"
					+ " where Train_id=? and Train_no=? and Seat_No=?";
			pstmt = conn.prepareStatement(updateSeatStaus);
			pstmt.setString(1, flag);
			pstmt.setInt(2, trainId);
			pstmt.setInt(3, trainNo);
			pstmt.setString(4, seatNo);
			pstmt.executeUpdate();

			int result = pstmt.executeUpdate(); // checkSeatStaus쿼리수행

			if (result == 1 && flag.equals("Y")) {
				//  seating_status테이블 좌석체크 컬럼 Y or F로 바꿔줌
				jObject1.put("result", "checkSeat_ok");
			} else
				jObject1.put("result", "checkSeat_fail");

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
		if (pstmt2 != null)
			pstmt2.close();
		if (pstmt3 != null)
			pstmt3.close();
		if (conn != null)
			conn.close();
	}
%>
