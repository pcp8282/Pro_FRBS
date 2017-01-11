<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.io.IOException"%>
<%@ page import="java.io.ObjectInputStream"%>
<%@ page import="java.io.ObjectOutputStream"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.net.ServerSocket"%>
<%@ page import="java.net.Socket"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		int portnumber = 10011;
		InetAddress ip = InetAddress.getLocalHost();
		System.out.println("Host Name = [" + ip.getHostName() + "]");
		System.out.println("Host Address = [" + ip.getHostAddress() + "]");

		ServerSocket aSs = new ServerSocket(portnumber);

		System.out.println("Host Address = [" + aSs.getLocalSocketAddress()
				+ "]");
	
		

		for (int i = 1; i <= 100; i++) {
			System.out.println("소캣 진입!");
			
			Socket sock = aSs.accept();
			
			System.out.println("소캣 accept!");
			InetAddress clientHost = sock.getLocalAddress();
			int clientPort = sock.getPort();

			ObjectInputStream ins = new ObjectInputStream(
					sock.getInputStream());
			Object obj = ins.readObject();
			System.out.println("from android: " + obj);

			ObjectOutputStream ons = new ObjectOutputStream(
					sock.getOutputStream());
			
			
			//String value = ; // NFC태깅 성공경우 2 받아진다
			if(request.getParameter("value") != null){
				System.out.println("NFC인증!!");
				ons.writeObject("2"); // on
				//ons.writeObject("1");  off
				ons.flush();
				sock.close();
			}else{
				System.out.println("보통때 NFC인증 안된 상황!");
				ons.writeObject("1"); // on
				//ons.writeObject("1");  off
				ons.flush();
				sock.close();
			}
			/* 
			if("2".equals(value)){
				
				
			}else{
				System.out.println("보통때 NFC인증 안된 상황!");
			} */
			

		}
	%>

</body>

</html>