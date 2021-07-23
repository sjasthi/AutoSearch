import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Grid {
	private char[][] theGrid;
	private ArrayList<String> solutions = new ArrayList<String>();
	private static ArrayList<String> targetWords = new ArrayList<String>();
	private static ArrayList<String> fillerWords = new ArrayList<String>();
	private static int rows, cols, gridSize;
	
	private static final Random RANDOM = new Random();
	private static final int[][] directions = {
			{1,0}, {0,1}, {1,1}, {1,-1}, {-1,1}, {-1,0}, {0,-1}, {-1,-1} 
	};
	
	public Grid(int rows, int cols, ArrayList<String> wordList, ArrayList<String> fillerList) {
		this.rows = rows;
		this.cols = cols;
		targetWords = wordList;
		fillerWords = fillerList;
		
		gridSize = rows*cols;
		theGrid = new char[rows][cols];
		
		Collections.shuffle(targetWords);
		
		for(String word : targetWords) {
			tryPlaceWord(this, word);			
		}
		
		completeGrid(this, fillerWords);
		//printResults();
	}

	public char[][] getTheGrid() {
		return theGrid;
	}

	public void setTheGrid(char[][] theGrid) {
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

	public static void setRows(int rows) {
		Grid.rows = rows;
	}

	public static int getCols() {
		return cols;
	}

	public static void setCols(int cols) {
		Grid.cols = cols;
	}
	
	public static int tryPlaceWord(Grid grid, String word) {
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
	
	public static int tryLocation(Grid grid, String word, int dir, int pos) {
		int r = pos/cols;
		int c = pos%cols;
		int length = word.length();
		
		if((directions[dir][0] == 1 && (length + c) > cols) 
				|| (directions[dir][0] == -1 && (length - 1) > c)
				|| (directions[dir][1] == 1 && (length + r) > rows)
				|| (directions[dir][1] == -1 && (length - 1) > r))
			return 0;
		
		int i, rr, cc, overlaps = 0;
		
		for(i = 0, rr = r, cc = c; i<length; i++) {
			if(grid.theGrid[rr][cc] != 0 && grid.theGrid[rr][cc] != word.charAt(i))
				return 0;
			
			cc += directions[dir][0];
			rr += directions[dir][1];
		}
		
		for(i = 0, rr = r, cc = c; i<length; i++) {
			if(grid.theGrid[rr][cc] == word.charAt(i))
				overlaps++;
			else grid.theGrid[rr][cc] = word.charAt(i);
			
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
	
	public static void completeGrid(Grid grid, ArrayList<String> words) {
		String temp = "";
		for(int i = 0; i<words.size(); i++) {
			temp += words.get(i).toUpperCase();
		}

		for(int i = 0; i<rows; i++) {
			for(int n = 0; n<cols; n++) {
				if(grid.theGrid[i][n] == 0)
					grid.theGrid[i][n] = temp.charAt(RANDOM.nextInt(temp.length()));
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
