import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * PuzzleCollection encapsulates all the puzzles generated
 * @author srj
 *
 */
public class PuzzleCollection {
	
	// how many puzzles are there in this collection
	private int count;
	
	//Collection of puzzles
	//Each puzzle holds the information about a puzzle and the solution
	private ArrayList<Puzzle> puzzleList = new ArrayList<Puzzle>();
	
	/**
	 * Constructor for the PuzzleCollection
	 * @param count
	 */
	
	public PuzzleCollection(int count) {
		this.count = count;
		int max_count = Preferences.MAX_PUZZLE_COUNT;
		
		if (count > max_count) {
			this.count = Preferences.MAX_PUZZLE_COUNT;
			System.out.println("Max puzzle count is " + max_count);
		}
		
	}

	/**
	 * Method for generating a puzzle from an input text file
	 * @param file_name
	 * @param lang
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	
	public void createPuzzles(String file_name, String lang) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
		//set up file to import words from
		File file = new File(file_name);
		Scanner s1 = new Scanner(file, "UTF-8");
		
		//create api for web service calls
		API api = new API();
		int loadingcount = 0;
		
//		//set up language
//		String lang = "";
//		if(inEnglish) {
//			lang = "English";
//		} else {
//			lang = "telugu";
//		}
		
		//get data for puzzles
//		int rows = getRowsFromDatabase();
//		int cols = getColsFromDatabase();
//		
		
		int rows = Preferences.NO_OF_ROWS;
		int cols = Preferences.NO_OF_COLUMNS;
		
		//parse words from text file
		for(int i = 0; i<count; i++) {
			int puzzleID = 0;
			int wordCount = 0;
			String title = "";
			ArrayList<String[]> wordList = new ArrayList<String[]>();
			
			while(s1.hasNextLine()&&wordCount<10) {
				String line = s1.nextLine();
				String[] tokens = new String[4];
				tokens = line.split(",");
				puzzleID = Integer.parseInt(tokens[0]);
				title = tokens[2];
				wordList.add(api.getLogicalChars(tokens[3].toUpperCase()));
				System.out.println("Calling API " + loadingcount);
				loadingcount++;
				wordCount++;
			}
			
			//create puzzle
			Puzzle puzzle = new Puzzle(puzzleID, title, wordList, rows, cols, api.getFillerCharacters(200, lang));
			
			//add puzzle to list for later use
			puzzleList.add(puzzle);
		}
		
		
	}
	
	/**
	 * This method gets the number of rows from the database.
	 * For now, this is NOT being used.
	 * We hardcoded the number of rows in the Preferences.
	 * Once the database support is provided, this method can move to Preferences.
	 * @return
	 */
	public static int getRowsFromDatabase() {
		Connection db_connection = null;
		int rows = 0;
		try {

			String url = "jdbc:mysql://localhost/autosearch";
			String user = "root";
			String password = "";
			db_connection = DriverManager.getConnection(url, user, password);
			System.out.println("Success: Connection established");

			Statement statement_object = db_connection.createStatement();

			String sql_query_str = "SELECT * FROM dimensions";
			ResultSet result_set = statement_object.executeQuery(sql_query_str);

			while (result_set.next()) {
				rows = result_set.getInt("rows");
				System.out.println("rows = "+rows);
				

			} // end while

		} // end try

		catch (Exception ex) {
			ex.printStackTrace();
		} // end catch
		
		return rows;
	}

	/**
	 * This method gets the number of rows from the database.
	 * For now, this is NOT being used.
	 * We hardcoded the number of rows in the Preferences.
	 * Once the database support is provided, this method can move to Preferences.
	 * @return
	 */
	public static int getColsFromDatabase() {
		Connection db_connection = null;
		int columns = 0;
		try {		
			

			String url = "jdbc:mysql://localhost/autosearch";
			String user = "root";
			String password = "";
			db_connection = DriverManager.getConnection(url, user, password);

			Statement statement_object = db_connection.createStatement();

			String sql_query_str = "SELECT * FROM dimensions";
			ResultSet result_set = statement_object.executeQuery(sql_query_str);
			
			while (result_set.next()) {
				columns = result_set.getInt("columns");
				System.out.println("columns = "+columns);
				

			} // end while

		} // end try

		catch (Exception ex) {
			ex.printStackTrace();
		} // end catch
		
		return columns;
	}
	
	/**
	 * getter for returning the puzze list
	 * @return
	 */
	public ArrayList<Puzzle> getPuzzleList(){
		return puzzleList;
	}
	
}
