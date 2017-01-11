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
		// 테스트용 실제할때 request.getParameter(""); 변경해서 사용!
		String customer_id = "pcp8282";
		int trainId = 1;
		int trainNo = 1;
		String seatNo = "A4";		
		*/
		String customer_id = request.getParameter("id");
		int trainId = Integer.parseInt(request.getParameter("train_id"));
		int trainNo  = Integer.parseInt(request.getParameter("train_no"));
		String seatNo = request.getParameter("seat_no"); 
		///////////////////////
		System.out.println(seatNo);
		JSONObject jObject1 = new JSONObject();
		
		if (conn != null) {
			
			//예약 유무 
			String select = "select * from reservation where Train_Id=? and Train_No=? and Seat_No=?";
			pstmt=conn.prepareStatement(select);
			pstmt.setInt(1,trainId);
			pstmt.setInt(2,trainNo);
			pstmt.setString(3,seatNo);
			rs = pstmt.executeQuery();
			
			if(rs.next()){// 예약된 좌석
				jObject1.put("result", "reserve_exist");
			}else{
				String reserve= "Insert into Reservation(Customer_Id,Train_Id,Train_No,Seat_No) values(?,?,?,?)";
				pstmt2=conn.prepareStatement(reserve);
				pstmt2.setString(1,customer_id);
				pstmt2.setInt(2,trainId);
				pstmt2.setInt(3,trainNo);
				pstmt2.setString(4,seatNo);
				int result = pstmt2.executeUpdate(); // 예매쿼리수행
				
				if(result==1){
					jObject1.put("result", "reserve_ok");
				}
				else
					jObject1.put("result", "reserve_fail");
			}
			
		}
		else {//DB연결 실패
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
