import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.TableCell.BorderEdge;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Neil Haggerty
 *
 */
public class Driver {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	
	
	public static void main(String[] args) throws IOException, SQLException {
		String font = "Arial";
		int fontSize = 10;
		int colWidth = 25;
		int rowHeight = 25;
		int X = 165;
		int Y = 150;
		boolean showLables = true;
		boolean showBorders = false;
		
		createPowerPoint(rowHeight, colWidth, font, fontSize, showLables, showBorders, X, Y);
		
		API test = new API();
		
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add("Hello");
		temp.add("my");
		temp.add("name");
		temp.add("is");
		temp.add("Neil");
		
		
		
		
		System.out.println(test.getLength(toCharArray(temp)));
		
		
		
		
		
		
	}
	
	public static char[] toCharArray(ArrayList<String> strings) {
		int size = 0;
		for(int i = 0; i<strings.size(); i++) {
			for(int n = 0; n<strings.get(i).length(); n++) {
				size++;
			}
		}
		
		char[] theArray = new char[size];
		
		size = 0;
		for(int i = 0; i<strings.size(); i++) {
			for(int n = 0; n<strings.get(i).length(); n++) {
				theArray[size] = strings.get(i).charAt(n);
				size++;
			}
		}
		
		return theArray;
		
	}
	
	public static void setBorders(HSLFTableCell cell) {
		cell.setBorderColor(BorderEdge.bottom, Color.black);
		cell.setBorderColor(BorderEdge.top, Color.black);
		cell.setBorderColor(BorderEdge.right, Color.black);
		cell.setBorderColor(BorderEdge.left, Color.black);
	}
	
	public static void createTitle(HSLFSlide slide) {
		HSLFTextBox title = slide.createTextBox();
		HSLFTextParagraph p = title.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setBold(true);
		r.setFontColor(Color.black);
		r.setText("Puzzle");
		r.setFontFamily("Arial");
		r.setFontSize(35.);
		title.setAnchor(new Rectangle(270,30,200,60));
		
	}
	
	public static void createSlideNum(HSLFSlide slide, int slide_num) {
		HSLFTextBox slide_number = slide.createTextBox();
		HSLFTextParagraph p = slide_number.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		slide_number.setFillColor(Color.green);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setText("" + slide_num + "");
		r.setFontFamily("Arial");
		if(slide_num>99) {
			r.setFontSize(20.);
		} else {
			r.setFontSize(30.);
		}
		
		
		slide_number.setAnchor(new Rectangle(220,30,50,50));
	}
	
	public static void createLine(HSLFSlide slide, int x, int y, int x2, int y2) {
		HSLFLine line = new HSLFLine();
		line.setAnchor(new Rectangle(x,y,x2,y2));
		line.setLineColor(Color.black);
		slide.addShape(line);	
	}
	
	public static void createPic(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		byte[] picture = IOUtils.toByteArray(new FileInputStream(new File("logo.png")));
		HSLFPictureData pd = ppt.addPicture(picture, HSLFPictureData.PictureType.PNG);
		HSLFPictureShape pic_shape = slide.createPicture(pd);  
		pic_shape.setAnchor(new Rectangle(0, 0, 174, 65));
	}
	

	
	
	public static void drawSolutions(HSLFTable t, ArrayList<String> coords) {
		for(int n = 0; n<coords.size(); n++) {
			String[] current = coords.get(n).split(" ");
			String[] start = current[0].split(",");
			String[] end = current[1].split(",");
			
			int startX = Integer.parseInt(start[1]);
			int startY = Integer.parseInt(start[0]);
			int endX = Integer.parseInt(end[1]);
			int endY = Integer.parseInt(end[0]);
			
			int wordLength;
			if((startX - endX)==0) {
				if((startY - endY)>0) {
					wordLength = startY - endY;
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX), (startY - i));
						cell.setFillColor(Color.green);
						
					}
					
				} else {
					wordLength = endY - startY;
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX), (startY + i));
						cell.setFillColor(Color.green);
						
					}
				}
			} else if((startX - endX)>0) {
				wordLength = startX - endX;
				if((endY - startY)==0) {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX - i), (startY));
						cell.setFillColor(Color.green);
						
					}
				} else if((endY - startY)>0) {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX - i), (startY + i));
						cell.setFillColor(Color.green);
						
					}
				} else {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX - i), (startY - i));
						cell.setFillColor(Color.green);
						
					}
				}
			} else {
				wordLength = endX - startX;
				if((endY - startY)==0) {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX + i), (startY));
						cell.setFillColor(Color.green);
						
					}
				} else if((endY - startY)>0) {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX + i), (startY + i));
						cell.setFillColor(Color.green);
						
					}
				} else {
					for(int i = 0; i<wordLength+1; i++) {
						HSLFTableCell cell = t.getCell((startX + i), (startY - i));
						cell.setFillColor(Color.green);
						
					}
				}
			}
			
			
			
		}
	}
	
	public static void createPowerPoint(int rowHeight, int colWidth, String font, int fontSize, boolean showLables, boolean showBorders, int X, int Y) throws IOException, SQLException {
		//create file
		File f = new File("Puzzle.ppt");
		
		ArrayList<String> tempWordList = new ArrayList<String>();
		
		tempWordList.add("CONSOLE");
		tempWordList.add("SPEAKER");
		tempWordList.add("COFFEE");
		tempWordList.add("DRAWER");
		tempWordList.add("DRINK");
		tempWordList.add("COLD");
		
		API api = new API();
		
		ArrayList<String> logicalCharacters = api.parseLogicalChars(tempWordList);
		
		//Data about slides
		int puzzleCount = 10;
		
		int puzzle_slide = 1;
		int solution_slide = puzzleCount + 1;
		
		int rows = getRowsFromDatabase();
		int cols = getColsFromDatabase();
		
		//keeps track of the puzzles, used to create solution slides
		ArrayList<Grid> puzzles = new ArrayList<Grid>();
		
		//create powerpoints
		HSLFSlideShow ppt = new HSLFSlideShow();
		
		String[] top_label = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };

		String[] side_label = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24", "25", "26" };
		
		//creating puzzle slides
		for(int count = 0; count<puzzleCount; count++) {
			
			
			
			
			//create puzzle
			Grid theGrid = new Grid(rows, cols, getWordsFromTextFile("words.txt"), logicalCharacters);
			
			//add puzzle to list for later use
			puzzles.add(theGrid);
							
			//create 2 slides (no solution and solution)
			HSLFSlide slide1 = ppt.createSlide();
					
			//create template for slide1
			createTitle(slide1); //title
			createSlideNum(slide1, puzzle_slide); //number textbox
			createPic(ppt, slide1); //logo
					
			//create text box lines
			createLine(slide1, 220, 30, 50, 0); //top line
			createLine(slide1, 270, 30, 0, 50); //right line
			createLine(slide1, 220, 80, 50, 0); //bottom line
			createLine(slide1, 220, 30, 0, 50); //left line
			
					
			//create a table of 12 rows and 16 columns
			HSLFTable table1 = slide1.createTable(rows, cols);
			
			char grid[][] = theGrid.getTheGrid();
			
			if(showLables) {
				HSLFTable top_row = slide1.createTable(1, cols);
				HSLFTable side_row = slide1.createTable(rows, 1);
				
				for(int i = 0; i<rows; i++) {
					//side column labels for slide 1
					HSLFTableCell side_cell = side_row.getCell(i, 0);
					side_cell.setText(side_label[i]);
					setBorders(side_cell);
					HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rts1.setFontFamily("Arial");
					rts1.setFontSize(10.);
					side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					side_cell.setHorizontalCentered(true);
				}
				
				
				for(int i = 0; i<cols; i++) {
					HSLFTableCell top_cell = top_row.getCell(0, i);
					top_cell.setText(top_label[i]);
					setBorders(top_cell);
					HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rt2s1.setFontFamily("Arial");
					rt2s1.setFontSize(10.);
					top_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					top_cell.setHorizontalCentered(true);
				}
				
				side_row.setColumnWidth(0, 30);
				top_row.setRowHeight(0, 30);
				
				for (int i = 0; i < cols; i++) {
					top_row.setColumnWidth(i, colWidth);
					
				}
				
				for (int i = 0; i < rows; i++) {
					side_row.setRowHeight(i, rowHeight);
					
				}
				
				top_row.moveTo(X, Y-30);
				side_row.moveTo(X-30, Y);
				
			}
			
			
			
			for(int i = 0; i<cols; i++) {
				for(int n = 0; n<rows; n++) {
					//writes values from puzzle into tables
					String char_string = String.valueOf(grid[n][i]);
					HSLFTableCell cell1 = table1.getCell(n, i);
					cell1.setText(char_string);
					
					//formats each cell on slide 1
					if(showBorders)
						setBorders(cell1);
				    HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
				    rt1.setFontFamily("Arial");
				    rt1.setFontSize(10.);
				    cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
				    cell1.setHorizontalCentered(true);
				    
				    
				}
			}
			
			
			
			for (int i = 0; i < cols; i++) {
				table1.setColumnWidth(i, colWidth);
				
			}
			
			
			for (int i = 0; i < rows; i++) {
				table1.setRowHeight(i, rowHeight);
				
			}
			
			table1.moveTo(X, Y);
			
			
			//increment slide numbers 
			puzzle_slide = puzzle_slide + 1; 
		}
		
		//creating solution slides
		for(int count = 0; count<puzzleCount; count++) {
			HSLFSlide slide2 = ppt.createSlide();
			createSlideNum(slide2, solution_slide); 
			createPic(ppt, slide2);
			
			//create text box lines 
			createLine(slide2, 220, 30, 50, 0); //top line
			createLine(slide2, 270, 30, 0, 50); //right line
			createLine(slide2, 220, 80, 50, 0); //bottom line
			createLine(slide2, 220, 30, 0, 50); //left line
			
			HSLFTable table2 = slide2.createTable(rows, cols);
			
			//draw solutions on slide 2
			char grid[][] = puzzles.get(count).getTheGrid();
			drawSolutions(table2, puzzles.get(count).getSolutions());
			
			if(showLables) {
				HSLFTable top_row = slide2.createTable(1, cols);
				HSLFTable side_row = slide2.createTable(rows, 1);
				
				for(int i = 0; i<rows; i++) {
					//side column labels for slide 1
					HSLFTableCell side_cell = side_row.getCell(i, 0);
					side_cell.setText(side_label[i]);
					setBorders(side_cell);
					HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rts1.setFontFamily("Arial");
					rts1.setFontSize(10.);
					side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					side_cell.setHorizontalCentered(true);
				}
				
				
				for(int i = 0; i<cols; i++) {
					HSLFTableCell top_cell = top_row.getCell(0, i);
					top_cell.setText(top_label[i]);
					setBorders(top_cell);
					HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rt2s1.setFontFamily("Arial");
					rt2s1.setFontSize(10.);
					top_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					top_cell.setHorizontalCentered(true);
				}
				
				side_row.setColumnWidth(0, 30);
				top_row.setRowHeight(0, 30);
				
				for (int i = 0; i < cols; i++) {
					top_row.setColumnWidth(i, colWidth);
					
				}
				
				for (int i = 0; i < rows; i++) {
					side_row.setRowHeight(i, rowHeight);
					
				}
				
				top_row.moveTo(X, Y-30);
				side_row.moveTo(X-30, Y);
				
			}
			
			for(int i = 0; i<16; i++) {
				for(int n = 0; n<12; n++) {
					//writes values from puzzle into tables
					String char_string = String.valueOf(grid[n][i]);
					HSLFTableCell cell2 = table2.getCell(n, i);
					cell2.setText(char_string);
				    
				    //formats each cell on slide 2
					if(showBorders)
						setBorders(cell2);
			        HSLFTextRun rt2 = cell2.getTextParagraphs().get(0).getTextRuns().get(0);
			        rt2.setFontFamily("Arial");
			        rt2.setFontSize(10.);
			        cell2.setVerticalAlignment(VerticalAlignment.MIDDLE);
			        cell2.setHorizontalCentered(true);
				}
			}
			
			
			for (int i = 0; i < cols; i++) {
				table2.setColumnWidth(i, colWidth);
				
			}
			
			for (int i = 0; i < rows; i++) {
				table2.setRowHeight(i, rowHeight);
				
			}
			
			table2.moveTo(X, Y);
			
			//increment slide numbers  
			solution_slide = solution_slide + 1;
		}
		
		FileOutputStream out = new FileOutputStream(f);
		ppt.write(out);
		out.close();
		
				
		System.out.println("Puzzle is created to Puzzle.ppt.");
				
		Desktop.getDesktop().browse(f.toURI());
		ppt.close();
	}
	
	public static ArrayList<String> getWordsFromTextFile(String file_name){
		ArrayList<String> theList = new ArrayList<String>();
		
		Random rand = new Random();
		
		File file = new File(file_name);
		try {
			Scanner s1 = new Scanner(file);
			String word = "";
			for(int i = 0; i<10; i++) {
				int line = rand.nextInt(20);
				for(int n = 0; n<line; n++) {
					word = s1.nextLine();
				}
				//System.out.println(word);
				if(theList.contains(word)) {
					i--;
				} else {
					theList.add(word.toUpperCase());
				}
				
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return theList;
	}
	
	public static ArrayList<String> getWordsFromDatabase() {
		ArrayList<String> tempWordList = new ArrayList<String>();
		tempWordList.add("CAT");
		tempWordList.add("DOG");
		tempWordList.add("TRUCK");
		tempWordList.add("TREE");
		tempWordList.add("ROCK");
		tempWordList.add("DIME");
		tempWordList.add("QUARTER");
		tempWordList.add("NICKLE");
		tempWordList.add("BRICK");
		tempWordList.add("STICK");
		
		return tempWordList;
	}
	
	/*
	public static ArrayList<String> getFillerCharacters() {
		ArrayList<String> tempWordList = new ArrayList<String>();
		
		tempWordList.add("CONSOLE");
		tempWordList.add("SPEAKER");
		tempWordList.add("COFFEE");
		tempWordList.add("DRAWER");
		tempWordList.add("DRINK");
		tempWordList.add("COLD");
		
		return tempWordList;
	}
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
}
