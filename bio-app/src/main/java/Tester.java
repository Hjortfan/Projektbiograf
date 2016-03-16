
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
		sql.printBooking(4, "ossian@bob.se", seats);
	}

}
