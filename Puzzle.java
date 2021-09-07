import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A class representing a single Puzzle
 * 
 * @author srj
 *
 */
public class Puzzle {
	private int puzzleID;
	private String title;
	private ArrayList<String[]> targetWords = new ArrayList<String[]>();
	private String[][] theGrid;
	private ArrayList<String> solutions = new ArrayList<String>();
	private static String[] fillerCharacters = new String[200];
	private static int rows, cols, gridSize;
	private static final Random RANDOM = new Random();
	// these eigth directions represent the way words are placed
	private static final int[][] directions = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, 0 },
			{ 0, -1 }, { -1, -1 } };

	/**
	 * Constructor for the Puzzle
	 * 
	 * @param puzzle_id
	 * @param title
	 * @param target_words
	 * @param rows
	 * @param cols
	 * @param filler_chars
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public Puzzle(int puzzle_id, String title, ArrayList<String[]> target_words, int rows, int cols,
			String[] filler_chars) throws UnsupportedEncodingException, SQLException {
		this.puzzleID = puzzle_id;
		this.title = title;
		this.targetWords = target_words;

		Puzzle.rows = rows;
		Puzzle.cols = cols;
		Puzzle.fillerCharacters = filler_chars;
		Puzzle.gridSize = rows * cols;

		theGrid = new String[rows][cols];

		Collections.shuffle(targetWords);

		for (String[] word : targetWords) {
			placeWordsInGrid(this, word);
		}

		placeFillersInGrid(fillerCharacters);
		// printResults();
	}

	public String[][] getTheGrid() {
		return theGrid;
	}

	public void setTheGrid(String[][] theGrid) {
		this.theGrid = theGrid;
	}

	public ArrayList<String> getSolutions() {
		return solutions;
	}

	public void setSolutions(ArrayList<String> solutions) {
		this.solutions = solutions;
	}

	public static int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public static int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getPuzzleID() {
		return puzzleID;
	}

	public void setPuzzleID(int puzzleID) {
		this.puzzleID = puzzleID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String[]> getTargetWords() {
		return targetWords;
	}

	public void setTargetWords(ArrayList<String[]> words) {
		this.targetWords = words;
	}

	/**
	 * This method places the given words in the given puzzle object (which is
	 * encapsulating the grid)
	 * 
	 * @param grid
	 * @param target_words
	 * @return
	 */

	public static int placeWordsInGrid(Puzzle grid, String[] target_words) {
		int rand_dir = RANDOM.nextInt(directions.length);
		int rand_pos = RANDOM.nextInt(gridSize);

		for (int dir = 0; dir < directions.length; dir++) {
			dir = (dir + rand_dir) % directions.length;

			for (int pos = 0; pos < gridSize; pos++) {
				pos = (pos + rand_pos) % gridSize;

				int letters_placed = tryLocation(grid, target_words, dir, pos);

				if (letters_placed > 0) {
					return letters_placed;
				}
			}
		}
		return 0;
	}

	/**
	 * This method tries to place the solution words in the grid.
	 * 
	 * @param the_puzzle
	 * @param target_words
	 * @param dir
	 * @param pos
	 * @return
	 */

	public static int tryLocation(Puzzle the_puzzle, String[] target_words, int dir, int pos) {
		int r = pos / cols;
		int c = pos % cols;
		int length = target_words.length;

		if ((directions[dir][0] == 1 && (length + c) > cols) || (directions[dir][0] == -1 && (length - 1) > c)
				|| (directions[dir][1] == 1 && (length + r) > rows) || (directions[dir][1] == -1 && (length - 1) > r))
			return 0;

		int i, rr, cc, overlaps = 0;

		for (i = 0, rr = r, cc = c; i < length; i++) {
			if ((the_puzzle.theGrid[rr][cc] != null) && (the_puzzle.theGrid[rr][cc] != target_words[i]))
				return 0;

			cc += directions[dir][0];
			rr += directions[dir][1];
		}

		for (i = 0, rr = r, cc = c; i < length; i++) {
			if (the_puzzle.theGrid[rr][cc] == target_words[i])
				overlaps++;
			else
				the_puzzle.theGrid[rr][cc] = target_words[i];

			if (i < length - 1) {
				cc += directions[dir][0];
				rr += directions[dir][1];
			}
		}

		int letters_placed = length - overlaps;

		if (letters_placed > 0) {
			the_puzzle.solutions.add(String.format("%d,%d %d,%d", c, r, cc, rr));
		}

		return letters_placed;
	}

	/**
	 * This method places the filler characters in the grid.
	 * 
	 * @param filler_characters
	 */
	public void placeFillersInGrid(String[] filler_characters) {
		int count = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (this.theGrid[i][j] == null) {
					this.theGrid[i][j] = filler_characters[count];
					count++;
				}
			}
		}
	}

	/**
	 * This method prints the puzzle. This serves as a debugging aid
	 */
	public void printResults() {
		if (theGrid == null) {
			System.out.println("No grid exists");
			return;
		}

		int size = solutions.size();

		for (int r = 0; r < rows; r++) {

			for (int c = 0; c < cols; c++) {
				System.out.printf(" %c ", theGrid[r][c]);
			}

			System.out.printf("\n");
		}

		System.out.println("\n");

		for (int i = 0; i < size - 1; i += 2) {
			System.out.printf("%s %s%n", solutions.get(i), solutions.get(i + 1));
		}

		if (size % 2 == 1) {
			System.out.println(solutions.get(size - 1));
		}

	}

}
