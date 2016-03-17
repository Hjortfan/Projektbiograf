
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * sqlConnector class. For this class to work, we need the
 * mysql-connector-java-5-1-38-bin.jar file. available at
 * http://dev.mysql.com/downloads/connector/j/ That file is put in the folder
 * "lib" that is short for library. And then added to the "java build path"
 * trough the projects properties.
 * 
 * @author Juicy L
 *
 */
public class sqlConnector {

	private static Connection con;
	private static Statement stat;
	private static ResultSet resSet;

	/**
	 * Method that connects to the database. There are 2 ip's, One is for LAN
	 * connection, the other for connection over the internet.
	 */

	public static void connectionToMysql() {
		String userName = "bob";
		String passWord = "bob";
		// String host = "jdbc:mysql://81.170.228.92:51515";
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
	 * Method that closes down the connection to the server. Used by other
	 * methods when they are done.
	 */
	public static void closeMysql() {
		try {
			con.close();
			System.out.println("Closing connection...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that matches user submitted password to the one that exist in the
	 * database. it uses connectionToMysql method to establish an connection.
	 * sends after the password of the user and compares the string it gets back
	 * to the users submitted password. sends back an boolean that is true if
	 * the password matches.
	 */

	public boolean passwordMatch(String email, String password) {
		connectionToMysql();

		boolean pass = false;
		try {
			stat = con.createStatement();
			String sql = "SELECT user_password FROM bio.user" + " WHERE user_email='" + email + "'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				if (password.equals(resSet.getString("user_password"))) {
					pass = true;
				}
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return pass;
	}

	public void addUser(String email, String password, String type) {
		connectionToMysql();
		System.out.println("Addning new user...");
		try {

			stat = con.createStatement();
			String sql = "INSERT INTO bio.user " + "VALUES('" + email + "', '" + password + "', '" + type + "')";
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
			String sql = "UPDATE bio.user " + "SET user_password='" + newPassword + "', user_type='" + newType + "' "
					+ "WHERE user_email = '" + email + "'";
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("user added...");
		closeMysql();
	}

	public String booking(int screening_id, String email, int[] seats) {
		connectionToMysql();
		System.out.println("making reservation...");
		try {
			stat = con.createStatement();
			String sql = "INSERT INTO bio.reservation " + "VALUES ( null, '" + email + "'," + screening_id
					+ ", 'true')";
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
		return printBooking(reservation_id);
	}

	public int getScreeningId(String movie, String date, String time) {
		connectionToMysql();
		int i = -1;
		try {
			stat = con.createStatement();
			String sql = "SELECT screening_id " + "FROM bio.movie, bio.screening " + "WHERE movie_name='" + movie
					+ "' AND screening_date='" + date + "' AND screening_time='" + time + "' AND movie_id=screening_id";
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

	public String printBooking(int reservation_id) {
		connectionToMysql();
		String movieName = "", date = "", time = "";
		try {
			stat = con.createStatement();
			String sql = "SELECT movie_name, screening_date, screening_time FROM bio.movie, bio.screening "
					+ "WHERE movie_id=(SELECT screening_movie_id FROM bio.screening "
					+ "WHERE screening_id=(SELECT reservation_screening_id FROM bio.reservation "
					+ "WHERE reservation_id=" + reservation_id + ")) "
					+ "AND screening_id=(SELECT reservation_screening_id FROM bio.reservation "
					+ "WHERE reservation_id=" + reservation_id + ")";
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
		ArrayList<Integer> seats = bookedSeats(reservation_id);
		StringBuilder sb = new StringBuilder();
		sb.append("Movie: " + movieName + "\nDate: " + date + "\nTime: " + time + "\nSeats: ");
		for (int j = 0; j < seats.size(); j++) {
			sb.append(getSeat(seats.get(j)));
		}
		return sb.toString();
	}

	private String getSeat(int i) {
		connectionToMysql();
		String row = null, number = null;
		try {
			stat = con.createStatement();
			String sql = "SELECT seat_row, seat_nr FROM bio.seat" + " WHERE seat_id=" + i + "";
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
		return "\nRow: " + row + " Number: " + number;
	}

	public void bookingSeats(int reservation_id, int seat_id, int screening_id) {
		connectionToMysql();
		System.out.println("reserving seats...");
		try {

			stat = con.createStatement();

			String sql = " INSERT INTO bio.reserved_seats " + "VALUES ( " + reservation_id + " , " + seat_id + " , "
					+ screening_id + ")";

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
		boolean pass = false;
		try {
			stat = con.createStatement();
			String sql = "SELECT reservation_id FROM bio.reservation" + " WHERE reservation_user_email='" + email
					+ "' AND reservation_screening_id=" + screening_id + "";
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

	public String remainingSeats(int screening_id) {
		ArrayList<Integer> list = new ArrayList<>();
		connectionToMysql();
		try {
			stat = con.createStatement();
			String sql = "SELECT seat_id FROM bio.seat " + "WHERE seat_id NOT IN " + "(SELECT reserved_seats_seat_id "
					+ "FROM bio.reserved_seats WHERE reserved_screening_id=" + screening_id + ")";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				list.add(resSet.getInt("seat_id"));
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		StringBuilder sb = new StringBuilder();
		sb.append("Seats left: ");
		for (int i = 0; i < list.size(); i++) {
			sb.append(getSeat(list.get(i)));
		}
		return sb.toString();
	}
	
	public ArrayList<Integer> bookedSeats(int reservation_id) {
		ArrayList<Integer> list = new ArrayList<>();
		connectionToMysql();
		try {
			stat = con.createStatement();
			String sql = "SELECT reserved_seats_seat_id FROM bio.reserved_seats "+
					"WHERE reserved_seats_reserveation_id="+reservation_id+"";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				list.add(resSet.getInt("reserved_seats_seat_id"));
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return list;
	}
	
	public String showAllReservations(String email){
		ArrayList<Integer> list = new ArrayList<>();
		connectionToMysql();
		try {
			stat = con.createStatement();
			String sql = "SELECT reservation_id FROM bio.reservation "+
					"WHERE reservation_user_email='"+email+"'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				list.add(resSet.getInt("reservation_id"));
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		StringBuilder sb = new StringBuilder();
		sb.append("Your Booking "+email+"\n\n");
		for (int i = 0; i < list.size(); i++) {
			sb.append(printBooking(list.get(i))+"\n\n");
		}
		return sb.toString();
	}

	public void deletReservation(int screeningId, String email) {
		connectionToMysql();
		System.out.println("deleting reservation");
		try {

			stat = con.createStatement();

			String sql = " DELETE FROM bio.reservation WHERE reservation_screening_id="+screeningId+" And reservation_user_email='"+email+"'";

			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("reservation deleted");
		closeMysql();
		
	}

	public void AddMovie(int i, String movie_name, String ageRestriction) {
		connectionToMysql();
		System.out.println("Adding movie to database...");
		try {
			stat = con.createStatement();

			String sql = "INSERT INTO bio.movie "+
					"VALUE ("+i+",'"+ movie_name+"','"+ ageRestriction+"' )";

			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("movie added...");
		closeMysql();
	}

	public void addScreening(int screening_id, String date, String time, int movie_id, int auditorium) {
		
		connectionToMysql();
		System.out.println("Adding screening to database...");
		try {

			stat = con.createStatement();

			String sql = "INSERT INTO bio.screening "+
					"VALUE ("+screening_id+",'"+date+"','"+time+"',"+movie_id+","+auditorium+" )";

			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("screening added...");
		closeMysql();

	}
	
	public int getMovieId(String movie) {
		connectionToMysql();
		int i = -1;
		try {
			stat = con.createStatement();
			String sql = "SELECT movie_id " + "FROM bio.movie " + "WHERE movie_name='" + movie+"'";
			resSet = stat.executeQuery(sql);
			while (resSet.next()) {
				i = resSet.getInt("movie_id");
			}
		} catch (SQLException e) {
			System.out.println("problem");
			e.printStackTrace();
		}
		closeMysql();
		return i;
	}
	

}
