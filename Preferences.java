/**
 * Preferences for encapsulating (1) the puzzle generation (2) the power point
 * generation
 * 
 * @author siva jasthi
 *
 */
public class Preferences {

	// logo file name
	public static String LOGO_FILE_NAME = "logo.png";

	// How many puzzles do you want to generate?
	// For testing purposes, keep this number small.
	// In production, set it to 100
	public static int PUZZLE_COUNT = 5;

	// System limit on the number of puzzles for batch generation
	public static int MAX_PUZZLE_COUNT = 100;

	// Language setting
	public static String LANGUAGE = "Telugu";
	// public static String language = "English";

	// file_name setting;
	// Language and file name setting should match
	// Go with the smaller files during the development
	// public static String FILE_NAME = "telugu_word_list.txt";
	// public static String FILE_NAME = "english_word_list.txt";
	public static String FILE_NAME = "telugu_small_word_list.txt";
	// public static String FILE_NAME = "english_small_word_list.txt";

	// Default number of rows for the puzzle grid
	public static int NO_OF_ROWS = 12;

	// Default number of columns for the puzzle grid
	public static int NO_OF_COLUMNS = 16;

	// Power Point Rendering Preferences
	public static String PPT_FILE_NAME = "Puzzles" + System.currentTimeMillis() + ".ppt";
	public static int ROW_HEIGHT = 30;
	public static int COL_WIDTH = 42;
	public static String FONT_NAME = "NATS";
	public static double FONT_SIZE = 18.0;
	public static boolean SHOW_LABELS = true;
	public static boolean SHOW_BORDERS = true;
	public static int STARTING_X = 40;
	public static int STARTING_Y = 110;

}
