import static spark.Spark.*;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import spark.*;

/**
 * Main class that listens and responds to request by clients.
 * 
 * @author Juicy L
 *
 */
public class Main {

	private sqlConnector sql;

	/**
	 * Constructor that recives an sql connector object and then has the methods
	 * that will point at the html files.
	 * 
	 * It will later be able to recive information from the client trough these.
	 * I just dont know how yet.
	 * 
	 * @param sql
	 *            sqlConnector. Object of the class that connects to the MySql
	 *            database.
	 */
	public Main(sqlConnector sql) {
		Spark.staticFileLocation("/public");
		this.sql = sql;
		get("/hello", (req, res) -> "Hello World");
		get("/index", (req, res) -> renderContent("index.html"));

		get("/admin/addmovie/:movieID/:movieName/:ageRes", new Route() {
			public Object handle(Request req, Response res) {
				sql.AddMovie(Integer.parseInt((req.params(":movieID"))), (req.params(":movieName")),
						(req.params(":ageRes")));
				return "HELLO IT'S DONE";
			}
		});

		get("/", (req, res) -> renderContent("index.html"));
		get("/admin", (req, res) -> renderContent("admin.html"));

	}

	/**
	 * Main method, creates an sqlConnector object starts the server.
	 * 
	 * for now it also contains a system.out.print method to test the connection
	 * to the database
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		sqlConnector sql = new sqlConnector();
		Main m = new Main(sql);
		// sql.addUser("email@hotmail.com", "hemligt", "user");
		System.out.println(sql.passwordMatch("email@hotmail.com", "hemligt"));
	}

	/**
	 * Method used for sending the client the web page corresponding to the path
	 * the client has requested.
	 * 
	 * @param htmlFile
	 *            String. HTML file for the path the client has requested.
	 * 
	 * @return String(Files.readAllBytes(path), Charset.defaultCharset()); or
	 *         null.
	 */

	private String renderContent(String htmlFile) {
		try {
			URL url = getClass().getResource(htmlFile);
			Path path = Paths.get(url.toURI());
			return new String(Files.readAllBytes(path), Charset.defaultCharset());
		} catch (IOException | URISyntaxException e) {

		}
		return null;
	}

}