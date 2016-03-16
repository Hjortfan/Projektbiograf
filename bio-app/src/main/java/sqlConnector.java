

import java.sql.*;

import javax.swing.JOptionPane;
/**
 * sqlConnector class.
 * For this class to work, we need the mysql-connector-java-5-1-38-bin.jar file.
 * available at http://dev.mysql.com/downloads/connector/j/
 * That file is put in the folder "lib" that is short for library.
 * And then added to the "java build path" trough the projects properties.
 * 
 * @author Juicy L
 *
 */
public class sqlConnector {

	private static Connection con;
	private static Statement stat;
	private static ResultSet resSet;
	
	/**
	 * Method that connects to the database.
	 * There are 2 ip's, One is for LAN connection, the other for connection over the internet.
	 */
	
	public static void connectionToMysql() {
		String userName = "bob";
		String passWord = "bob";
	//	String host = "jdbc:mysql://81.170.228.92:51515";
		String host = "jdbc:mysql://192.168.1.22:51515";

		try {
			con = DriverManager.getConnection(host, userName, passWord);
			System.out.println("Connected to MySQL server...");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Connection faild...");
		}
	}
	 /**
	  * Method that closes down the connection to the server.
	  * Used by other methods when they are done.
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
	 * Method that matches user submitted password to the one that exist in the database.
	 * it uses connectionToMysql method to establish an connection.
	 * sends after the password of the user and compares the string it gets back
	 * to the users submitted password.
	 * sends back an boolean that is true if the password matches.
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
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return pass;
	}
	
	
	public static void addUser(String email, String password, String type) {
	connectionToMysql();
	System.out.println("Addning new user...");
	try {
		
		stat = con.createStatement();
		String sql = "INSERT INTO bio.user "
				+ "VALUES('" + email + "', '" + password +"', '" + type + "')";
		stat.executeUpdate(sql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	System.out.println("user added...");
	closeMysql();
}
	public void changeUser(String email, String newPassword, String newType) {
		connectionToMysql();
		System.out.println("Changing user...");
		try {
			
			stat = con.createStatement();
			String sql = "UPDATE bio.user "
					+ "SET user_password='"
					+ newPassword +"', user_type='" + newType + "' "+
					"WHERE user_email = '"+email+"'";
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("user added...");
		closeMysql();
	}
	public void booking(int movie, String email, int[] seats) {
		
		connectionToMysql();
		System.out.println("making reservation...");
		try {
			
			stat = con.createStatement();
			String sql = "INSERT INTO bio.reservation "
					+ "VALUES ( null, '"+ email +"'," + movie + ", 'true')";
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("user added...");
		closeMysql();
		
		int res = reservationID(email, movie);
		
		for (int i = 0; i < seats.length; i++) {
			bookingSeats(res, seats[i], movie);
		}
		printBooking(movie, email, seats);
	}
	
	public void printBooking(int movie, String email, int[] seats) {
		connectionToMysql();
		int i = -1;
		boolean pass=false;
		try {
			stat = con.createStatement();
			String sql = "SELECT reservation_id FROM bio.reservation"+
					" WHERE reservation_user_email='"+email+
					"' AND reservation_screening_id="+movie+"";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				i = resSet.getInt("reservation_screening_id");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		
		String movieName = getMovieName(i);
		String dateAndTime  = getScreeningTimeAndDate(i);
		StringBuilder sb = new StringBuilder();
		
		for (int j = 0; j < seats.length; j++) {
			sb.append(getSeat(seats[j]));
		}
		
		String print = "Movie: "+ movieName
				+ "Date And Time:\n"+ dateAndTime+ 
				"Seats booked: \n"+
				sb.toString();
		JOptionPane.showMessageDialog(null, print);
	}
	private String getSeat(int i){
		connectionToMysql();
		String row = null, number = null;
		try {
			stat = con.createStatement();
			String sql = "SELECT seat_row, seat_nr FROM bio.seat"+
					" WHERE seat_id="+i+"";
			resSet = stat.executeQuery(sql);
			
			while (resSet.next()) {
				row = resSet.getString("seat_row");
				number = resSet.getString("seat_nr");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return "row: "+row + " \nNumber: "+ number;
	}
		
	
	
	public String getScreeningTimeAndDate(int id) {
		connectionToMysql();
		String time = null, date = null;
		try {
			stat = con.createStatement();
			String sql = "SELECT screening_date, screening_time FROM bio.screening"+
					" WHERE screening_id="+id+"";
			resSet = stat.executeQuery(sql);
			
			while (resSet.next()) {
				date = resSet.getString("screening_date");
				time = resSet.getString("screening_time");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return time + " \n"+ date;
	}
	private String getMovieName(int movieId){
		connectionToMysql();
		String movieName = "";
		try {
			stat = con.createStatement();
			String sql = "SELECT movie_name FROM bio.movie"+
					" WHERE movie_id='"+movieId+"'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				movieName= resSet.getString("movie_name");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		
		return movieName;
		
	}
	
	public void bookingSeats(int reservation_id, int seat_id, int screening_id){
		connectionToMysql();
		System.out.println("reserving seats...");
		try {
			
			stat = con.createStatement();
			
				String sql = " INSERT INTO bio.reserved_seats "
						+ "VALUES ( "+reservation_id+" , "+
						seat_id+" , "+screening_id+")";
			
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("seat reserved...");
		closeMysql();
		
	}
	
	private int reservationID(String email, int movie) {
		connectionToMysql();
		int i = -1;
		boolean pass=false;
		try {
			stat = con.createStatement();
			String sql = "SELECT reservation_id FROM bio.reservation"+
					" WHERE reservation_user_email='"+email+
					"' AND reservation_screening_id="+movie+"";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				i = resSet.getInt("reservation_id");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		System.out.println("got reservations id");
		return i;
		
	}
	
//	public static boolean seatsReserved(String movie, String date, String time) {
//		connectionToMysql();
//		
//		boolean pass=false;
//		try {
//			stat = con.createStatement();
//			String sql = "SELECT screening_id FROM bio.screening"+
//					" WHERE user_email='"+email+"'";
//			resSet = stat.executeQuery(sql);
//			while (resSet.next()) {
//				if(password.equals(resSet.getString("user_password"))){
//					pass=true;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		closeMysql();
//		return pass;
//	}
	
	
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
