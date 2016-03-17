import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import java.sql.*;

public class TestClass {
	sqlConnector sql = new sqlConnector();
	String mail = "newUser@test.test", pass= "testerspass";
	
	@Test
	public void addScreening(){
		sql.addScreening(6, "2016-03-18","22.45" , 6, 3);
		
		assertEquals(6, sql.getScreeningId("Gabriels Resa", "2016-03-18", "22.45"));
		
	}
	
	
	@Test
	public void AddMovie(){
		sql.AddMovie(6, "Gabriels Resa", "VuxenFilm");
		assertEquals(6, sql.getMovieId("Gabriels Resa"));
	}
	
	@Test
	public void deleteReservation(){
		sql.deletReservation(sql.getScreeningId("kill bill", "2016-03-17", "15.30"),"ossian@bob.se");
		
		String str = "Your Booking ossian@bob.se\n\n"
				+ "Movie: the hangover\n"
				+ "Date: 2016-03-16\n"
				+ "Time: 19.15\n"
				+ "Seats: \n"
				+ "Row: 1 Number: 5\n"
				+ "Row: 1 Number: 6\n"
				+ "Row: 1 Number: 7\n\n"
				+ "Movie: terminator\n"
				+ "Date: 2016-03-16\n"
				+ "Time: 22.30\n"
				+ "Seats: \n"
				+ "Row: 1 Number: 2\n"
				+ "Row: 1 Number: 3\n"
				+ "Row: 1 Number: 4\n\n";
		
				assertEquals(str, sql.showAllReservations("ossian@bob.se"));
	}
	
	@Test
	public void showAllResarvationes(){
		String str = "Your Booking ossian@bob.se\n\n"
				+ "Movie: the hangover\n"
				+ "Date: 2016-03-16\n"
				+ "Time: 19.15\n"
				+ "Seats: \n"
				+ "Row: 1 Number: 5\n"
				+ "Row: 1 Number: 6\n"
				+ "Row: 1 Number: 7\n\n"
				+ "Movie: terminator\n"
				+ "Date: 2016-03-16\n"
				+ "Time: 22.30\n"
				+ "Seats: \n"
				+ "Row: 1 Number: 2\n"
				+ "Row: 1 Number: 3\n"
				+ "Row: 1 Number: 4\n\n"
				+ "Movie: kill bill\n"
				+ "Date: 2016-03-17\n"
				+ "Time: 15.30\n"
				+ "Seats: \n"
				+ "Row: 1 Number: 2\n"
				+ "Row: 1 Number: 3\n"
				+ "Row: 1 Number: 4\n"
				+ "Row: 1 Number: 5\n"
				+ "Row: 1 Number: 6\n\n";
		
		assertEquals(str, sql.showAllReservations("ossian@bob.se"));
	}
	
	
	@Test
	public void testDoseUserGetRegisterd() {
		
		sql.addUser("newUser@test.test","testerspass", "user");
		boolean boo = sql.passwordMatch("newUser@test.test", "testerspass");
		assertEquals(true, boo);
	}
	@Test
	public void chekIfAdmin(){
		assertEquals(true, sql.chekIfAdmin("ossian@bob.se", "ossybossy"));
	}
	
	@Test
	public void ChangeUserInfo(){
		sql.addUser("testson@lkahsjf.se","daytona", "user");
		sql.changeUser("testson@lkahsjf.se","heffe","admin");
		
		boolean boo = sql.passwordMatch("testson@lkahsjf.se", "heffe");
		assertEquals(true, boo);
	}
	
	@Test
	public void BookMovie(){
		String str= "Movie: kill bill\n"
				+"Date: 2016-03-17\n"
				+"Time: 15.30\n"
				+"Seats: \n" 
				+"Row: 1 Number: 2\n"
				+"Row: 1 Number: 3\n"
				+"Row: 1 Number: 4\n"
				+"Row: 1 Number: 5\n"
				+"Row: 1 Number: 6";
		int[] seats = {2,3,4,5,6};
		int i = sql.getScreeningId("kill bill", "2016-03-17", "15.30"), b= -1 ;
		
		assertEquals(str ,sql.booking(i, "ossian@bob.se", seats));
		
	}
	
	@Test
	public void SeeRemainingSeats(){
		String str = "Seats left: \n"+ 
			"Row: 1 Number: 1\n"+
			"Row: 1 Number: 5\n"+
			"Row: 1 Number: 6\n"+
			"Row: 1 Number: 7\n"+
			"Row: 1 Number: 8\n"+
			"Row: 1 Number: 9\n"+
			"Row: 1 Number: 10\n"+
			"Row: 2 Number: 1\n"+
			"Row: 2 Number: 2\n"+
			"Row: 2 Number: 3\n"+
			"Row: 2 Number: 4\n"+
			"Row: 2 Number: 5\n"+
			"Row: 2 Number: 6\n"+
			"Row: 2 Number: 7\n"+
			"Row: 2 Number: 8\n"+
			"Row: 2 Number: 9\n"+
			"Row: 2 Number: 10";
		assertEquals(str, sql.remainingSeats(2));
	}
//			delete FROM bio.user
//			WHERE user_email='newUser@test.test';
//			delete FROM bio.user
//			WHERE user_email='testson@lkahsjf.se';
//			delete FROM bio.reservation
//			WHERE reservation_screening_id=3;
//			delete FROM bio.movie
//			WHERE movie_id=6;
//			delete FROM bio.screening
//			WHERE screening_id=6;
}
