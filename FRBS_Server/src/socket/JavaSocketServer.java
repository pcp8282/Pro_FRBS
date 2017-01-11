package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import config.SocketInfo;

public class JavaSocketServer {
	Map<String, ObjectOutputStream> clients;

	// 생성자
	JavaSocketServer() {
		clients = Collections.synchronizedMap( //
				new HashMap<String, ObjectOutputStream>());
	}

	// 비즈니스 로직을 처리하는 메서드
	public void start() {
		ServerSocket serverSocket = null;
		int count = 0;
		Socket socket = null;

		try {
			// 10011 포트에 바인딩된 서버 소켓 생성
			serverSocket = new ServerSocket(10011);
			System.out.println("서버가 시작되었습니다.");
			System.out.println("ip::" + serverSocket.getLocalSocketAddress());
			System.out.println(serverSocket.getInetAddress());
			System.out.println("inet;;;;;;;");

			while (true) {
				// 클라이언트 접속 대기 accept()
				socket = serverSocket.accept();
				System.out.println("[" + socket.getInetAddress() // + ":" +
																	// socket.getPort()
						+ "]" + "에서 접속하였습니다.");

				ServerReceiver threadServerReceiver = new ServerReceiver(socket);
				threadServerReceiver.start();
				//thread.interrupt();
			}// while

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	} // start()

	void sendToAll(String msg) {
		// 대화방에 접속한 유저의 대화명 리스트 추출
		java.util.Iterator<String> it = clients.keySet().iterator();

		while (it.hasNext()) {
			try {
				String name = it.next();
				ObjectOutputStream out = clients.get(name);
				System.out.println("sendToAll - msg::"+msg);
				out.writeObject(msg);
				out.flush();
			} catch (IOException e) {
			}
		} // while
	} // sendToAll

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		new JavaSocketServer().start();
	}

	class ServerReceiver extends Thread {
		Socket socket;
		ObjectInputStream in;
		ObjectOutputStream out;

		ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				// 클라이언트 소켓에서 데이터를 수신받기 위한 InputStream 생성
				in = new ObjectInputStream(socket.getInputStream());

				// 클라이언트 소켓에서 데이터를 전송하기 위한 OutputStream 생성
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		public void run() {
		//	String name = "aa";
			String name = "2";
			try {
				// 서버에서는 최초에 클라이언트가 보낸 대화명을 받아야 한다.
				System.out.println("run 맨처음!!@!@!");
				
				
				name = (String) in.readObject();
				if(name.equals(null) || name.equals("aa"))
					System.out.println("aaaaa fuckjsdfklsdfjsdl!!!");
				
				System.out.println("name:::"+name);
				out.flush();
				
				
				System.out.println("flush 후 sendToAll 전 name::"+name);
				
				///////////// 1) 
				sendToAll(name);// 대화명을 받아, 전에 클라이언트에게 대화방 참여 메시지를 보낸다. //////////////////
				
				//================================
				// 대화명, 클라이언로 메시지를 보낼 수 있는 OutputStream 객체를 대화방 Map에 저장한다.
				clients.put(name, out);
				//socket.close();
				
				// 클라이언트가 전송한 메시지를 받아, 전에 클라이언트에게 메시지를 보낸다.
				while (in != null) {
					String str = (String) in.readObject();
					
					System.out.println("while문 안");
					sendToAll(str);
					//sendToAll((String) in.readObject());
				}// while
				
				
				
			} catch (IOException e) {
				// ignore
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// finally절이 실행된다는 것은 클라이언트가 빠져나간 것을 의미한다.
				//sendToAll("#" + name + "님이 나가셨습니다.");
				sendToAll(name);

				// 대화방에서 객체 삭제
				clients.remove(name);
				System.out.println("[" + socket.getInetAddress() //
						+ ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
				System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
				
				
			} // try
		} // run
		
	} // ReceiverThread
} // class

