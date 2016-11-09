package config;


public interface MySQLInfo {
	
	static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	static final String URL = "jdbc:mysql://localhost:3306/frbs?autoReconnect=true&useSSL=false";
	
	
	// 각자 db 계정 Id,pw 맞게 수정
	static final String USER = "root";
	static final String PASSWORD = "8282";
}

