import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A class representing a single Puzzle
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
	private static final int[][] directions = {
			{1,0}, {0,1}, {1,1}, {1,-1}, {-1,1}, {-1,0}, {0,-1}, {-1,-1} 
	};
	
	/**
	 * Constructor for the Puzzle
	 * @param puzzleID
	 * @param title
	 * @param words
	 * @param rows
	 * @param cols
	 * @param fillerList
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public Puzzle(int puzzleID, String title, ArrayList<String[]> words, int rows, int cols, String[] fillerList) throws UnsupportedEncodingException, SQLException {
		this.puzzleID = puzzleID;
		this.title = title;
		this.targetWords = words;
		
		Puzzle.rows = rows;
		Puzzle.cols = cols;
		fillerCharacters = fillerList;
		
		gridSize = rows*cols;
		theGrid = new String[rows][cols];
		
		Collections.shuffle(targetWords);
		
		for(String[] word : targetWords) {
			tryPlaceWord(this, word);			
		}
		
		completeGrid(fillerCharacters);
		//printResults();
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

	public ArrayList<String[]> getWords() {
		return targetWords;
	}

	public void setWords(ArrayList<String[]> words) {
		this.targetWords = words;
	}

	
	public static int tryPlaceWord(Puzzle grid, String[] word) {
		int randDir = RANDOM.nextInt(directions.length);
		int randPos = RANDOM.nextInt(gridSize);
		
		for(int dir = 0; dir<directions.length; dir++) {
			dir = (dir + randDir) % directions.length;
			
			for(int pos = 0; pos<gridSize; pos++) {
				pos = (pos + randPos ) % gridSize;
				
				int lettersPlaced = tryLocation(grid, word, dir, pos);
				
				if(lettersPlaced > 0) {
					return lettersPlaced;
				}
			}
		}
		return 0;
	}
	
	public static int tryLocation(Puzzle grid, String[] word, int dir, int pos) {
		int r = pos/cols;
		int c = pos%cols;
		int length = word.length;
		
		if((directions[dir][0] == 1 && (length + c) > cols) 
				|| (directions[dir][0] == -1 && (length - 1) > c)
				|| (directions[dir][1] == 1 && (length + r) > rows)
				|| (directions[dir][1] == -1 && (length - 1) > r))
			return 0;
		
		int i, rr, cc, overlaps = 0;
		
		for(i = 0, rr = r, cc = c; i<length; i++) {
			if(grid.theGrid[rr][cc] != null && grid.theGrid[rr][cc] != word[i])
				return 0;
			
			cc += directions[dir][0];
			rr += directions[dir][1];
		}
		
		for(i = 0, rr = r, cc = c; i<length; i++) {
			if(grid.theGrid[rr][cc] == word[i])
				overlaps++;
			else grid.theGrid[rr][cc] = word[i];
			
			if(i < length - 1) {
				cc += directions[dir][0];
				rr += directions[dir][1];
			}
		}
		
		int lettersPlaced = length - overlaps;
		
		if(lettersPlaced > 0) {
			grid.solutions.add(String.format("%d,%d %d,%d", c, r, cc, rr));
		}
		
		return lettersPlaced;
	}
	
	public void completeGrid(String[] characters) {
		int count = 0;
		for(int i = 0; i<rows; i++) {
			for(int n = 0; n<cols; n++) {
				if(this.theGrid[i][n] == null) {
					this.theGrid[i][n] = characters[count];
					count++;
				}
					
			}
		}
	}
	
	
	public void printResults() {
		if(theGrid == null) {
			System.out.println("No grid exists");
			return;
		}
		
		int size = solutions.size();
		
		for(int r = 0; r<rows; r++) {
			
			for(int c = 0; c<cols; c++) {
				System.out.printf(" %c ", theGrid[r][c]);
			}
			
			System.out.printf("\n");
		}
		
		System.out.println("\n");
		
		for(int i = 0; i<size-1; i+=2) {
			System.out.printf("%s %s%n", solutions.get(i), solutions.get(i + 1));
		}
		
		if(size % 2 == 1) {
			System.out.println(solutions.get(size - 1));
		}
		
	}
	

	
}
