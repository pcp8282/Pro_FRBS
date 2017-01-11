<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.TrainScheduleVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("getCheckSeat.jsp 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL,
				MySQLInfo.USER, MySQLInfo.PASSWORD);

		
		int trainId = Integer.parseInt(request.getParameter("train_id"));
		int trainNo  = Integer.parseInt(request.getParameter("train_no"));
		
		HashMap<String, String> checkSeats = new HashMap<String, String>();
		//ArrayList<String,String> checkSeatList = new ArrayList<String>();

		if (conn != null) {
			String select = "select Seat_No, Seating_check from seating_status where Train_id=? and Train_no=?";

			pstmt = conn.prepareStatement(select);
			pstmt.setInt(1, trainId);
			pstmt.setInt(2, trainNo);
			rs = pstmt.executeQuery(); // select문 수행

			while (rs.next()) {//노선들을 routeList에 넣는다
				if(rs.getString(2).equals("Y") || rs.getString(2).equals("F"))
					checkSeats.put(rs.getString(1), rs.getString(2));// seatNo, 체크상태
				//checkSeatList.add(rs.getString(1));

		}
		JSONObject jsonObj = new JSONObject(); 
		JSONArray jArray = new JSONArray();
		//json 담는다
		Set s = checkSeats.entrySet();
		Iterator it = s.iterator();
		
		while(it.hasNext()){
			Map.Entry<String,String> e = (Map.Entry<String, String>)it.next();
			jsonObj.put("check_seat", e.getKey()+"`"+e.getValue());
			jArray.add(jsonObj);
			jsonObj = new JSONObject(); 
			
		}
		/* for (int i = 0; i < checkSeats.size(); i++) {
			
			jsonObj.put(, );
			jArray.add(jsonObj);
			jsonObj = new JSONObject(); 
		} */
		
		out.print(jArray.toJSONString());
		}
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
