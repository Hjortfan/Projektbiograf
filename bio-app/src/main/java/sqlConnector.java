

import java.sql.*;
/**
 * sqlConnector class.
 * For this class to work, we need the mysql-connector-java-5-1-38-bin.jar file.
 * That file is put in the folder "lib" that is short for library.
 * And then added to the "java build path" trough the projects properties.
 * @author Juicy L
 *
 */
public class sqlConnector {

	private static Connection con;
	private static Statement stat;
	private static ResultSet resSet;
	
	/**
	 * Method that connects to the database
	 * There are 2 ip's, One is for LAN connection, the other for connection over the internet.
	 */
	
	public static void connectionToMysql() {
		String userName = "bob";
		String passWord = "bob";
//		String host = "jdbc:mysql://81.170.228.92:51515";
		String host = "jdbc:mysql://192.168.1.22:51515";

		try {
			con = DriverManager.getConnection(host, userName, passWord);
			System.out.println("Connected to MySQL server...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	 /**
	  * Method that closes down the connection to the server.
	  */
	public static void closeMysql(){
		try {
			con.close();
			System.out.println("Closing connection...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that matches user subbmitted password to the one that exist in the database.
	 * it uses connectionToMysql method to establish an connection.
	 * sends after the password of the user and compares the string it gets back
	 * to the users submitted password.
	 * sends back an boolean that is tru if the password matches.
	 */
	
	public static boolean passwordMatch(String email, String password) {
		connectionToMysql();
		
		boolean pass=false;
		try {
			stat = con.createStatement();
			String sql = "SELECT user_password FROM bio.user"+
					" WHERE user_email='"+email+"'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				if(password.equals(resSet.getString("user_password"))){
					pass=true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeMysql();
		return pass;
	}
	
//   Reference code.	
//	
//	public static void addTag(String tagName, int categoryID) {
//		connectionToMysql();
//		System.out.println("Addning new Tag...");
//		try {
//			
//			stat = con.createStatement();
//			String sql = "INSERT INTO spargrisen.tag "
//					+ "VALUES('" + tagName + "', " + categoryID + ")";
//			stat.executeUpdate(sql);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Tag added...");
//		closeMysql();
//	}
//	/**
//	 * Metod som tar bort tag fr�n databasen.
//	 * @param tagName	 String som ineh�ller tagens namn som ska tas bort.
//	 * @param CategoryID int som ineh�ller CategoryID s� att r�tt tag blir borttagen.
//	 */
//	public static void removeTag(String tagName, int CategoryID) {
//		connectionToMysql();
//		try{
//		System.out.println("Removing Tag...");
//
//			String sql2 = "DELETE FROM spargrisen.tag "
//					+ "WHERE tagName='"+tagName+"'AND tCategoryID="+CategoryID;
//			stat.executeUpdate(sql2);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Tag removed...");
//		closeMysql();
//	}
	
}
