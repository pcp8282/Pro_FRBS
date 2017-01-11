<%@page import="config.SocketInfo"%>
<%@page import="config.MySQLInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="org.json.simple.*"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.ObjectInputStream"%>
<%@ page import="java.io.ObjectOutputStream"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.net.ServerSocket"%>
<%@ page import="java.net.Socket"%>

<%
class ConnectThread extends Thread {
    String hostname;

    public ConnectThread(String addr) {
        hostname = addr;
    }

    public void run() {

        try {

            int port = 10011;

            Socket sock = new Socket(hostname, port);
            ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());

           // String temp=beginListenForData();  /// 서버로 아두이노로부터 받은 데이터를 보낸다
            outstream.writeObject("2");
            //System.out.println("서버->소켓 22222");
            outstream.flush();
            sock.close();

        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}//ConnectThread

	System.out.println("NFCRToServerProcess.jsp 호출!!");
	Connection conn = null; // null 로 초기화 한다.
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	PreparedStatement pstmt3 = null;
	ResultSet rs = null;
	try {
		// DB 연결셋팅은 src/config/MySQLInfo 확인합니다
		
		Class.forName(MySQLInfo.DRIVER_NAME); // 데이터베이스와 연동하기 위해DriverManager 에 등록한다.
		// DriverManager 객체로부터 Connection 	객체를 얻어온다.		
		conn = DriverManager.getConnection(MySQLInfo.URL, MySQLInfo.USER, MySQLInfo.PASSWORD); 
				
		int reserveNo = Integer.parseInt(request.getParameter("reserve_no"));
				
		JSONObject jObject1 = new JSONObject();

		if (conn != null) {
			
			String doorAlready = "select Door_Check from reservation where Reserve_No=?";
			pstmt3 = conn.prepareStatement(doorAlready);
			pstmt3.setInt(1, reserveNo);
			rs = pstmt3.executeQuery(); // select문 수행
			rs.next();
			if("Y".equals(rs.getString(1))){// 이미 출입문 인증한경우 더 이상 시간 그런거 확인할 필요도 없음
				jObject1.put("result", "doorNFC_already");
			}else{
				String flag = "F"; // 리더기 세팅과 승차권 train_id 같은지 체크 변수
				String update = "Update reservation set door_check=?"
						+ " where reserve_no=?";
				pstmt2 = conn.prepareStatement(update);
				pstmt2.setString(1, flag);
				pstmt2.setInt(2, reserveNo);
				
				if("ok".equals(request.getParameter("result"))){// 인증 유효시간인지 체크
					String select = "select *  from train_schedule " +
							"where Train_Id =(select Train_Id from reservation where Reserve_No=?)"
							+ " and Departure_date=CURDATE() and curtime() between DATE_SUB(departure_time, INTERVAL 90 minute) and DATE_SUB(departure_time, INTERVAL -10 minute)";

					pstmt = conn.prepareStatement(select);
					pstmt.setInt(1, reserveNo);
					rs = pstmt.executeQuery(); // select문 수행

					/////////////////////////////////
					if (rs.next()) {// 예약테이블에 해당 정보 일치(유효한 티켓 이증)
						pstmt2.setString(1, "Y");
						pstmt2.executeUpdate();
						
						jObject1.put("result", "doorNFC_pass");
						System.out.println("출입문 인증!!");
						  ConnectThread thread = new ConnectThread(SocketInfo.SOCKET_IP);
		                thread.start(); //소켓서버에 2전송 */
		                //thread.sleep(1000);
					
					} else{
						pstmt2.executeUpdate();
						jObject1.put("result", "doorNFC_fail");
						System.out.println("출입문 인증실패!!");
						
					}
				}else{// 스캐너랑 태깅 train_id다른경우
					pstmt2.executeUpdate();
				}
			}
			
		} else {//DB연결 실패
			jObject1.put("result", "connect_fail");
		}
		System.out.println(jObject1.toJSONString());
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
