package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDB {
	
	private Connection conn=null;
	private String driver = "com.mysql.jdbc.Driver";
	private final String DB_url = "jdbc:mysql://localhost:3306/lykdb?user=root&password=i59pwy&useUnicode=true&characterEncoding=utf-8";
	//private final String DB_url = "jdbc:mysql://192.168.1.243:3306/music?user=mysql&password=xjtu&useUnicode=true&characterEncoding=utf-8";
	
	public Connection StartDBConnection() throws ClassNotFoundException{
		String url = DB_url;
		try{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			Class.forName(driver);
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			System.out.println("Mysql is connected");
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Mysql can't be connected!");
		}
		return conn;
	}
	
	public Connection getConnection(){
		return conn;
	}
	
	public void closeConnection(Connection conn) throws SQLException{
		if(conn!=null){
			conn.close();
			conn=null;
		}
	}

}
