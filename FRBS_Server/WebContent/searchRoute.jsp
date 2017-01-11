<%@page import="model.TrainScheduleVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%
	System.out.println("searchRoute 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL,
				MySQLInfo.USER, MySQLInfo.PASSWORD);

		// 테스트용 실제할때 request.getParameter(""); 변경해서 사용!
		//String departureStation = "서울";
		//String arrivalStation = "부산";
		//String departureDate = "2016-11-14";
		String departureStation = request.getParameter("departure_station");
		String arrivalStation = request.getParameter("arrival_station");
		String departureDate = request.getParameter("departure_date");
		
		ArrayList<TrainScheduleVO> routeList = new ArrayList<TrainScheduleVO>();

		if (conn != null) {
			String select = "select * from train_schedule"
					+ " where Departure_station=? and Arrival_station=? and Departure_date=? and Departure_time >  timestampadd(HOUR,1,sysdate()) order by Departure_time";
			
			if(request.getParameter("flag") != null){//관리자 페이지 노선조회
				select = "select * from train_schedule where Departure_station=? and Arrival_station=? and Departure_date=?"
						+" and curtime() < time(Arrival_time) order by Departure_time";
			}
			
			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, departureStation);
			pstmt.setString(2, arrivalStation);
			pstmt.setString(3, departureDate);
			rs = pstmt.executeQuery(); // select문 수행

			while (rs.next()) {//노선들을 routeList에 넣는다
				String[] depart = rs.getString("Departure_time").split(":");
				String[] arrival = rs.getString("Arrival_time").split(":");
				routeList.add(new TrainScheduleVO(
						rs.getInt("Train_id"), rs
								.getString("Departure_station"), rs
								.getString("Arrival_station"), 
								depart[0]+":"+depart[1],
								arrival[0]+":"+arrival[1],
								rs.getDate("Departure_date")));
			}

		}
		JSONObject jsonObj = new JSONObject(); 
		JSONArray jArray = new JSONArray();
		//json 담는다
		for (int i = 0; i < routeList.size(); i++) {
			
			jsonObj.put("Train_id", routeList.get(i).getTrainId());
			jsonObj.put("Departure_station", routeList.get(i).getDepartureStation());
			jsonObj.put("Arrival_station", routeList.get(i).getArrivalStation());
			jsonObj.put("Departure_time", routeList.get(i).getDepartureTime());
			jsonObj.put("Arrival_time", routeList.get(i).getArrivalTime());
			jsonObj.put("Departure_date", routeList.get(i).getDepartureDate());
			//out.print(jsonObj.toString());
			jArray.add(jsonObj);
			jsonObj = new JSONObject(); 
		}
		/* 이런형식
			[{"Departure_time":"13:00","Arrival_station":"부산","Train_id":1,"Departure_station":"서울","Departure_date":2016-11-14,"Arrival_time":"16:00"},
			 {"Departure_time":"17:00","Arrival_station":"부산","Train_id":6,"Departure_station":"서울","Departure_date":2016-11-14,"Arrival_time":"20:00"}]
		*/
		out.print(jArray.toJSONString());

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
