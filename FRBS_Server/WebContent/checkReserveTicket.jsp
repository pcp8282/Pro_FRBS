<%@page import="model.ReservationVO"%>
<%@page import="model.TrainScheduleVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	try {
		// DB 연결셋팅은 src/config/MySQLInfo에서
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다		
		conn = DriverManager.getConnection(MySQLInfo.URL,// DriverManager 객체로부터 Connection 	객체를 얻어온다
				MySQLInfo.USER, MySQLInfo.PASSWORD);

		String customer_id = request.getParameter("id");

		ArrayList<ReservationVO> reserveList = new ArrayList<ReservationVO>();

		if (conn != null) {
			String select = "SELECT * FROM train_schedule JOIN reservation ON reservation.Train_id = train_schedule.Train_id"
					+" WHERE reservation.Customer_Id =?";

			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, customer_id);
			rs = pstmt.executeQuery(); // select문 수행

			while (rs.next()) {//노선들을 routeList에 넣는다
				String[] depart = rs.getString("Departure_time").split(":");
				String[] arrival = rs.getString("Arrival_time").split(":");
				reserveList.add(new ReservationVO(
						rs.getInt("Train_id"), rs
								.getString("Departure_station"), rs
								.getString("Arrival_station"), 
								depart[0]+":"+depart[1],
								arrival[0]+":"+arrival[1], rs
								.getDate("Departure_date")
								, rs.getInt("Reserve_No")
								, rs.getInt("Train_No")
								, rs.getString("Seat_No")));
			}

		}
		JSONObject jsonObj = new JSONObject();
		JSONArray jArray = new JSONArray();
		//json 담는다
		for (int i = 0; i < reserveList.size(); i++) {

			// json객체에 노선 정보들을 넣음
			jsonObj.put("Train_id", reserveList.get(i).getTrainId());
			jsonObj.put("Departure_station", reserveList.get(i)
					.getDepartureStation());
			jsonObj.put("Arrival_station", reserveList.get(i)
					.getArrivalStation());
			jsonObj.put("Departure_time", reserveList.get(i)
					.getDepartureTime());
			jsonObj.put("Arrival_time", reserveList.get(i)
					.getArrivalTime());
			jsonObj.put("Departure_date", reserveList.get(i)
					.getDepartureDate());
			jsonObj.put("Reserve_No", reserveList.get(i)
					.getReserveNo());
			jsonObj.put("Train_No", reserveList.get(i)
					.getTrainNo());
			jsonObj.put("Seat_No", reserveList.get(i)
					.getSeatNo());
					
			// JSONObject를 JSONArray에 넣음
			jArray.add(jsonObj); 
			jsonObj = new JSONObject(); //JSONObject 초기화
		}
		out.print(jArray.toJSONString());
		/*
		[{"Departure_time":"13:00","Arrival_station":"부산","Train_id":1,"Departure_station":"서울","Departure_date":2016-11-14,"Arrival_time":"16:00"},
		 {"Departure_time":"17:00","Arrival_station":"부산","Train_id":6,"Departure_station":"서울","Departure_date":2016-11-14,"Arrival_time":"20:00"}]
		 */

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
