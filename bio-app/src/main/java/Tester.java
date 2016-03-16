
public class Tester {

	public static void main(String[] args) {
		sqlConnector sql = new sqlConnector();
		int[] seats = {2,3,4};
		
//		funkar
//		sql.addUser("email@hotmail.com", "hemligt", "user");
//        System.out.println(sql.passwordMatch("email@hotmail.com", "hemligt"));
		//sql.changeUser("email@hotmail.com","password","admin");
	
		//sql.booking(2,"ossian@bob.se", seats);
		//System.out.println(sql.getScreeningTimeAndDate(2));
		
//		sql.booking("terminator", "22.30", "2016-03-16", "ossian@bob.se", seats);
//		System.out.println(sql.getScreeningId("terminator", "2016-03-16", "22.30"));
//		System.out.println(sql.reservationID("ossian@bob.se", 2));
//		System.out.println(sql.getMovieName(4));
//		sql.getScreeningTimeAndDate(4);
		sql.printBooking(4, seats);
	}

}
