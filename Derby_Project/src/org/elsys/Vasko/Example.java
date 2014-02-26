package org.elsys.Vasko;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class Example {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		Connection conn = null;
		System.out.println("Connecting to database...");
		try {
			conn = DriverManager.getConnection("jdbc:derby:MyDatabase");
			System.out.println(">Successfully connected to database...");
		} catch (SQLException e) {
			System.out.println(">Database not found...");
			conn = CreateDatabase();
		}
		
		ShowSelect(conn);
		UpdateData(conn);

		ShowSelect(conn);
		PreparedStatement(conn);
		
		ShowSelect(conn);
		Transaction(conn);
		
		ShowSelect(conn);
		
		System.out.println("Done! Closing connection...");
		conn.close();
	}
	
	private static Connection CreateDatabase() throws SQLException
	{
		System.out.println("Creating database...");
		Connection conn = DriverManager.getConnection("jdbc:derby:MyDatabase;create=true");
		System.out.println(">Database created...");
		Statement st = conn.createStatement();
		st.execute("CREATE TABLE Example (number INT, name VARCHAR(16), address VARCHAR(16))");
		System.out.println(">Table created...");
		st = conn.createStatement();
		st.execute("INSERT INTO Example (number, name, address) VALUES (1, 'Vasko', 'Sofia'), (2, 'Hans', 'Duesseldorf')");
		System.out.println(">Records added...");
		
		
		System.out.println("Database ready...");
		return conn;
	}
	
	private static void ShowSelect(Connection conn) throws SQLException
	{
		System.out.println("Displaying Table data...");
		Statement stmnt = conn.createStatement();
		ResultSet result = stmnt.executeQuery("SELECT number, name, address FROM Example");
		while(result.next()) {
			String name = result.getString("name");
			String address = result.getString("address");
			int number = result.getInt(1);
			System.out.println("Name: " + name + " Address: " + address + " Number: " + number);
		}
	}
	
	private static void UpdateData(Connection conn) throws SQLException
	{
		System.out.println("Updating ResultSet data...");
		Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
											ResultSet.CONCUR_UPDATABLE);
		
		ResultSet result = stm.executeQuery("SELECT number FROM Example");
		while(result.next()) {
			int number = result.getInt(1);
			result.updateInt("number", number+1);
			result.updateRow();
		}
	}
	
	private static void PreparedStatement(Connection conn) throws SQLException
	{
		System.out.println("Updating actual database...");
		PreparedStatement update = conn.prepareStatement("UPDATE Example SET number = number*? WHERE name LIKE ?");
		update.setInt(1, 2);
		update.setString(2, "Hans");
		update.executeUpdate();
	}
	
	private static void Transaction(Connection conn) throws SQLException
	{
		System.out.println("Reverting changes via Transaction...");
		conn.setAutoCommit(false);
		Statement st = conn.createStatement();
		st.executeUpdate("UPDATE Example SET number = 1 WHERE name LIKE 'Vasko'");
		st.executeUpdate("UPDATE Example SET number = 2 WHERE name LIKE 'Hans'");
		conn.setAutoCommit(true);
	}
}