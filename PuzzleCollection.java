import java.io.File;
import java.io.FileNotFoundException;
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
 * 
 * @author srj
 *
 */
public class PuzzleCollection {

	// how many puzzles are there in this collection
	private int count;

	// Collection of puzzles
	// Each puzzle holds the information about a puzzle and the solution
	private ArrayList<Puzzle> puzzleList = new ArrayList<Puzzle>();

	/**
	 * Constructor for the PuzzleCollection The count should be smaller than the
	 * MAX_PUZZLE_COUNT If it is not, it defaults to MAX_PUZZLE_COUNT
	 * 
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
	 * 
	 * @param file_name
	 * @param lang
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */

	public void createPuzzles(String file_name, String lang)
			throws FileNotFoundException, UnsupportedEncodingException, SQLException {
		// set up file to import words from
		File file = new File(file_name);
		Scanner file_scanner = new Scanner(file, "UTF-8");

		// for generating the puzzle
		int rows = Preferences.NO_OF_ROWS;
		int cols = Preferences.NO_OF_COLUMNS;

		// create api for web service calls
		API api = new API();

		// to keep track of the line number being read from the file
		int line_count = 1;

		// parse words from text file
		// We generate a puzzle for each set of 10 words
		for (int i = 0; i < count; i++) {
			int puzzle_ID = 0;
			int word_count = 0;
			String title = "";
			ArrayList<String[]> word_list = new ArrayList<String[]>();

			// Each puzzle has 10 words; So, we hard code the limit here
			while (file_scanner.hasNextLine() && word_count < 10) {
				String line = file_scanner.nextLine();
				String[] tokens = new String[4];
				tokens = line.split(",");
				puzzle_ID = Integer.parseInt(tokens[0]);
				title = tokens[2];
				word_list.add(api.getLogicalChars(tokens[3].toUpperCase()));
				System.out.println("Calling API " + line_count);
				line_count++;
				word_count++;
			}

			// create puzzle: All the word placement logic happens here
			Puzzle puzzle = new Puzzle(puzzle_ID, title, word_list, rows, cols, api.getFillerCharacters(200, lang));

			// add puzzle to list for later use
			puzzleList.add(puzzle);
		}

	}

	/**
	 * NOT USED; We are relying on the Preference now. This method gets the number
	 * of rows from the database. For now, this is NOT being used. We hardcoded the
	 * number of rows in the Preferences. Once the database support is provided,
	 * this method can move to Preferences.
	 * 
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
				System.out.println("rows = " + rows);

			} // end while

		} // end try

		catch (Exception ex) {
			ex.printStackTrace();
		} // end catch

		return rows;
	}

	/**
	 * NOT USED; We are relying on the Preference now. This method gets the number
	 * of rows from the database. For now, this is NOT being used. We hardcoded the
	 * number of rows in the Preferences. Once the database support is provided,
	 * this method can move to Preferences.
	 * 
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
				System.out.println("columns = " + columns);

			} // end while

		} // end try

		catch (Exception ex) {
			ex.printStackTrace();
		} // end catch

		return columns;
	}

	/**
	 * getter for returning the puzze list
	 * 
	 * @return
	 */
	public ArrayList<Puzzle> getPuzzleList() {
		return puzzleList;
	}

}
