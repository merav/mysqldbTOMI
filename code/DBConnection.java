package dbproj;
import java.sql.*;

public class DBConnection {

	private static Connection conn;			// DB connection


	/**
	 * 
	 * @return the connection (null on error)
	 */
	private static void openConnection()
	{

		// loading the driver
		try
		{
			Class.forName("oracle.jdbc.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Unable to load the Oracle JDBC driver..");
			java.lang.System.exit(0); 
		}
		System.out.println("Driver loaded successfully");


		// creating the connection
		System.out.print("Trying to connect.. ");
		try
		{
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:XE","DBPROJ","DBPROJ");
		}
		catch (SQLException e)
		{
			System.out.println("Unable to connect - " + e.toString());
			java.lang.System.exit(0); 
		}
		System.out.println("Connected!");

	}
	
	public static Connection getConnection()
	{
		try {
			if (conn==null || conn.isClosed())
			{
				openConnection();
			}
		} catch (SQLException e) {
			System.out.println("Unable to connect - " + e.toString());
			java.lang.System.exit(0); 
		}

		return conn;
			
	}


	/**
	 * close the connection
	 */
	public static void closeConnection()
	{
		// closing the connection
		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			System.out.println("Unable to close the connection - " + e.toString());
			java.lang.System.exit(0); 
		}

	}


}
