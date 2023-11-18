package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.cj.jdbc.CallableStatement;
import com.mysql.cj.xdevapi.Statement;
public class JDBCUtil {

	public static Connection getConnection() {
		Connection c = null;
		try {
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			String url="jdbc:mysql://localhost:12345/user";
			String username = "root";
            String password = "3119410108";
            c = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public static void  closeConnection(Connection c) {
		try {
			if(c!=null)
				c.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
	
