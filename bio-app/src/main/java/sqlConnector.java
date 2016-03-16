

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
	public void booking(String movie, String time, String date, String email, int[] seats) {
		
		int screening_id = getScreeningId(movie, time, date);
		connectionToMysql();
		System.out.println("making reservation...");
		try {
			stat = con.createStatement();
			String sql = "INSERT INTO bio.reservation "
					+ "VALUES ( null, '"+ email +"'," + screening_id + ", 'true')";
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("user added...");
		closeMysql();
		
		int reservation_id = reservationID(email, screening_id);
		
		for (int i = 0; i < seats.length; i++) {
			bookingSeats(reservation_id, seats[i], screening_id);
		}
		printBooking(reservation_id, seats);
	}
	
	public int getScreeningId(String movie, String date, String time){
		connectionToMysql();
		int i = -1;
		try {
			stat = con.createStatement();
			String sql = "SELECT screening_id "+
					"FROM bio.movie, bio.screening "+
					"WHERE movie_name='"+movie+"' AND screening_date='"+date+"' AND screening_time='"+time+"'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				i = resSet.getInt("screening_id");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		
		return i;
	}
	
	public void printBooking(int reservation_id, int[] seats) {
		connectionToMysql();
		String movieName = "", date = "", time = "";
		try {
			stat = con.createStatement();
			String sql = "SELECT movie_name, screening_date, screening_time FROM bio.movie, bio.screening "+
					"WHERE movie_id=(SELECT screening_movie_id FROM bio.screening "+
						"WHERE screening_id=(SELECT reservation_screening_id FROM bio.reservation "+
							"WHERE reservation_id=4)) "+ 
								"AND screening_id=(SELECT reservation_screening_id FROM bio.reservation "+
									"WHERE reservation_id="+reservation_id+")";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				movieName = resSet.getString("movie_name");
				date = resSet.getString("screening_date");
				time = resSet.getString("screening_time");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		StringBuilder sb = new StringBuilder();
		sb.append("Movie: "+movieName+"\nDate: "+date+"\nTime: "+time+"\nSeats: ");
		for (int j = 0; j < seats.length; j++) {
			sb.append(getSeat(seats[j]));
		}
		
		JOptionPane.showMessageDialog(null, sb.toString());
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
		return "\nRow: "+row+ " Number: "+ number;
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
	
	public int reservationID(String email, int screening_id) {
		connectionToMysql();
		int i = -1;
		boolean pass=false;
		try {
			stat = con.createStatement();
			String sql = "SELECT reservation_id FROM bio.reservation"+
					" WHERE reservation_user_email='"+email+
					"' AND reservation_screening_id="+screening_id+"";
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
}
