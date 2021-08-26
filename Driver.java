import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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

/**
 * This driver is responsible for 
 * (a) creating an empty puzzle collection
 * (b) creating the puzzles and populating the puzzle collection
 * (c) generating the PPT based on the puzzle collection
 */
public class Driver {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */

	public static void main(String[] args) throws IOException, SQLException {

		// Create the puzzle collection
		PuzzleCollection puzzle_collection = new PuzzleCollection(Preferences.PUZZLE_COUNT);

		// Create the puzzles based on the language
		puzzle_collection.createPuzzles(Preferences.FILE_NAME, Preferences.LANGUAGE);

		// Create the power point from the puzzle collection
		createPowerPoint(puzzle_collection.getPuzzleList());

	}

	/**
	 * Create the Power Point from the puzzle collection
	 * @param puzzles
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void createPowerPoint(ArrayList<Puzzle> puzzles) throws IOException, SQLException {

		// Get the preferences for creating the power point
		int row_height = Preferences.ROW_HEIGHT;
		int col_width = Preferences.COL_WIDTH;
		String font_name = Preferences.FONT_NAME;
		double font_size = Preferences.FONT_SIZE;
		boolean show_labels = Preferences.SHOW_LABELS;
		boolean show_borders = Preferences.SHOW_BORDERS;
		int X = Preferences.STARTING_X;
		int Y = Preferences.STARTING_Y;
		String ppt_file_name = Preferences.PPT_FILE_NAME;

		// create ppt file
		File f = new File(ppt_file_name);

		// create slide numbers
		int puzzle_slide = 1;
		int solution_slide = puzzle_slide;

		// create slideshow
		HSLFSlideShow ppt = new HSLFSlideShow();

		// create labels
		String[] top_label = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		String[] side_label = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24", "25", "26" };

		// creating puzzle slides
		for (int count = 0; count < puzzles.size(); count++) {
			Puzzle thePuzzle = puzzles.get(count);

			// create slide
			HSLFSlide slide1 = ppt.createSlide();

			// create template for slide1
			createTitle(slide1, thePuzzle.getTitle()); // title
			createSlideNum(slide1, puzzle_slide); // number textbox
			createPic(ppt, slide1); // logo

			// create text box lines
//			createLine(slide1, 220, 30, 50, 0); //top line
//			createLine(slide1, 270, 30, 0, 50); //right line
//			createLine(slide1, 220, 80, 50, 0); //bottom line
//			createLine(slide1, 220, 30, 0, 50); //left line
//					
			// create a table for puzzle
			HSLFTable table1 = slide1.createTable(thePuzzle.getRows(), thePuzzle.getCols());

			// get the 2d char array
			String grid[][] = thePuzzle.getTheGrid();

			// show labels if specified
			if (show_labels) {
				HSLFTable top_row = slide1.createTable(1, thePuzzle.getCols());
				HSLFTable side_row = slide1.createTable(thePuzzle.getRows(), 1);

				for (int i = 0; i < thePuzzle.getRows(); i++) {
					// side column labels for slide 1
					HSLFTableCell side_cell = side_row.getCell(i, 0);
					side_cell.setText(side_label[i]);
					setBorders(side_cell);
					HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rts1.setFontFamily(font_name);
					rts1.setFontSize(font_size);
					side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					side_cell.setHorizontalCentered(true);
				}

				for (int i = 0; i < Puzzle.getCols(); i++) {
					HSLFTableCell top_cell = top_row.getCell(0, i);
					top_cell.setText(top_label[i]);
					setBorders(top_cell);
					HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rt2s1.setFontFamily(font_name);
					rt2s1.setFontSize(font_size);
					top_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					top_cell.setHorizontalCentered(true);
				}

				side_row.setColumnWidth(0, 30);
				top_row.setRowHeight(0, 30);

				for (int i = 0; i < thePuzzle.getCols(); i++) {
					top_row.setColumnWidth(i, col_width);

				}

				for (int i = 0; i < thePuzzle.getRows(); i++) {
					side_row.setRowHeight(i, row_height);

				}

				top_row.moveTo(X, Y - 30);
				side_row.moveTo(X - 30, Y);

			}

			// writes appropriate values into table
			for (int i = 0; i < thePuzzle.getCols(); i++) {
				for (int n = 0; n < thePuzzle.getRows(); n++) {
					// writes values from puzzle into tables
					String char_string = String.valueOf(grid[n][i]);
					HSLFTableCell cell1 = table1.getCell(n, i);
					cell1.setText(char_string);

					// formats each cell on slide 1
					if (show_borders)
						setBorders(cell1);
					HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
					rt1.setFontFamily(font_name);
					rt1.setFontSize(font_size);
					cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
					cell1.setHorizontalCentered(true);

				}
			}

			// set column width
			for (int i = 0; i < thePuzzle.getCols(); i++) {
				table1.setColumnWidth(i, col_width);

			}

			// set row height
			for (int i = 0; i < thePuzzle.getRows(); i++) {
				table1.setRowHeight(i, row_height);

			}

			// move table
			table1.moveTo(X, Y);

			// increment slide numbers
			puzzle_slide = puzzle_slide + 1;
		}

		// creating solution slides
		for (int count = 0; count < puzzles.size(); count++) {

			// get puzzle from puzzle list
			Puzzle thePuzzle = puzzles.get(count);

			// create and format slide
			HSLFSlide slide2 = ppt.createSlide();
			createSlideNum(slide2, solution_slide);
			createTitle(slide2, thePuzzle.getTitle());
			createPic(ppt, slide2);

			// create text box lines
//			createLine(slide2, 220, 30, 50, 0); //top line
//			createLine(slide2, 270, 30, 0, 50); //right line
//			createLine(slide2, 220, 80, 50, 0); //bottom line
//			createLine(slide2, 220, 30, 0, 50); //left line

			// create table
			HSLFTable table2 = slide2.createTable(thePuzzle.getRows(), thePuzzle.getCols());

			// draw solutions
			String grid[][] = puzzles.get(count).getTheGrid();
			drawSolutions(table2, puzzles.get(count).getSolutions());

			// show labels if specified
			if (show_labels) {
				HSLFTable top_row = slide2.createTable(1, thePuzzle.getCols());
				HSLFTable side_row = slide2.createTable(thePuzzle.getRows(), 1);

				for (int i = 0; i < thePuzzle.getRows(); i++) {
					// side column labels for slide 1
					HSLFTableCell side_cell = side_row.getCell(i, 0);
					side_cell.setText(side_label[i]);
					setBorders(side_cell);
					HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rts1.setFontFamily(font_name);
					rts1.setFontSize(font_size);
					side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					side_cell.setHorizontalCentered(true);
				}

				for (int i = 0; i < thePuzzle.getCols(); i++) {
					HSLFTableCell top_cell = top_row.getCell(0, i);
					top_cell.setText(top_label[i]);
					setBorders(top_cell);
					HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
					rt2s1.setFontFamily(font_name);
					rt2s1.setFontSize(font_size);
					top_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
					top_cell.setHorizontalCentered(true);
				}

				side_row.setColumnWidth(0, 30);
				top_row.setRowHeight(0, 30);

				for (int i = 0; i < thePuzzle.getCols(); i++) {
					top_row.setColumnWidth(i, col_width);

				}

				for (int i = 0; i < thePuzzle.getRows(); i++) {
					side_row.setRowHeight(i, row_height);

				}

				top_row.moveTo(X, Y - 30);
				side_row.moveTo(X - 30, Y);

			}

			// write appropriate values into table
			for (int i = 0; i < 16; i++) {
				for (int n = 0; n < 12; n++) {
					// writes values from puzzle into tables
					String char_string = String.valueOf(grid[n][i]);
					HSLFTableCell cell2 = table2.getCell(n, i);
					cell2.setText(char_string);

					// formats each cell
					if (show_borders)
						setBorders(cell2);
					HSLFTextRun rt2 = cell2.getTextParagraphs().get(0).getTextRuns().get(0);
					rt2.setFontFamily(font_name);
					rt2.setFontSize(font_size);
					cell2.setVerticalAlignment(VerticalAlignment.MIDDLE);
					cell2.setHorizontalCentered(true);
				}
			}

			// set column width
			for (int i = 0; i < thePuzzle.getCols(); i++) {
				table2.setColumnWidth(i, col_width);

			}

			// set row height
			for (int i = 0; i < thePuzzle.getRows(); i++) {
				table2.setRowHeight(i, row_height);

			}

			// move table
			table2.moveTo(X, Y);

			// increment slide numbers
			solution_slide = solution_slide + 1;
		}

		// write data to slideshow
		FileOutputStream out = new FileOutputStream(f);
		ppt.write(out);
		out.close();

		System.out.println("Puzzle is created in " + Preferences.PPT_FILE_NAME);

		Desktop.getDesktop().browse(f.toURI());
		ppt.close();
	}

	/**
	 * Helper method to set the borders
	 * @param cell
	 */
	public static void setBorders(HSLFTableCell cell) {
		cell.setBorderColor(BorderEdge.bottom, Color.black);
		cell.setBorderColor(BorderEdge.top, Color.black);
		cell.setBorderColor(BorderEdge.right, Color.black);
		cell.setBorderColor(BorderEdge.left, Color.black);
	}

	/**
	 * Helper method to create the Title
	 * @param slide
	 * @param puzzleName
	 */
	public static void createTitle(HSLFSlide slide, String puzzleName) {
		HSLFTextBox title = slide.createTextBox();
		HSLFTextParagraph p = title.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setBold(true);
		r.setFontColor(Color.black);
		r.setText(puzzleName.toUpperCase());
		r.setFontFamily(Preferences.FONT_NAME);
		r.setFontSize(Preferences.FONT_SIZE * 2);
		title.setAnchor(new Rectangle(240, 10, 400, 200));
	}

	/**
	 * Helper method to set the slide number
	 * @param slide
	 * @param slide_num
	 */
	public static void createSlideNum(HSLFSlide slide, int slide_num) {
		HSLFTextBox slide_number = slide.createTextBox();
		HSLFTextParagraph p = slide_number.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		slide_number.setFillColor(Color.green);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setText("" + slide_num + "");
		r.setFontFamily(Preferences.FONT_NAME);
		r.setFontSize(30.);
//		if (slide_num > 9) {
//			r.setFontSize(20.);
//		} else {
//			r.setFontSize(36.);
//		}

		slide_number.setAnchor(new Rectangle(220, 10, 50, 30));
	}

	
	/** 
	 * Helper method to create the lines in PPT
	 * @param slide
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 */
	public static void createLine(HSLFSlide slide, int x, int y, int x2, int y2) {
		HSLFLine line = new HSLFLine();
		line.setAnchor(new Rectangle(x, y, x2, y2));
		line.setLineColor(Color.black);
		slide.addShape(line);
	}
	
	/**
	 * Helper method to keep the logo
	 * @param ppt
	 * @param slide
	 * @throws IOException
	 */

	public static void createPic(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		File logo_file = new File(Preferences.LOGO_FILE_NAME);
		FileInputStream logo_stream = new FileInputStream(logo_file);
		byte[] picture = IOUtils.toByteArray(logo_stream);
		HSLFPictureData picture_data = ppt.addPicture(picture, HSLFPictureData.PictureType.PNG);
		HSLFPictureShape pic_shape = slide.createPicture(picture_data);
		pic_shape.setAnchor(new Rectangle(0, 0, 174, 65));
	}

	/**
	 * Method for showing the solutions in the solution slides
	 * 
	 * @param table
	 * @param coords
	 */

	public static void drawSolutions(HSLFTable table, ArrayList<String> coords) {
		for (int n = 0; n < coords.size(); n++) {
			String[] current = coords.get(n).split(" ");
			String[] start = current[0].split(",");
			String[] end = current[1].split(",");

			int start_x = Integer.parseInt(start[1]);
			int start_y = Integer.parseInt(start[0]);
			int end_x = Integer.parseInt(end[1]);
			int end_y = Integer.parseInt(end[0]);

			int word_length;

			// this nested if sequence determines what direction the word is going and fills
			// in the appropriate cells
			if ((start_x - end_x) == 0) {
				if ((start_y - end_y) > 0) {
					word_length = start_y - end_y;
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x), (start_y - i));
						cell.setFillColor(Color.green);
					}

				} else {
					word_length = end_y - start_y;
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x), (start_y + i));
						cell.setFillColor(Color.green);

					}
				}
			} else if ((start_x - end_x) > 0) {
				word_length = start_x - end_x;
				if ((end_y - start_y) == 0) {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x - i), (start_y));
						cell.setFillColor(Color.green);

					}
				} else if ((end_y - start_y) > 0) {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x - i), (start_y + i));
						cell.setFillColor(Color.green);

					}
				} else {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x - i), (start_y - i));
						cell.setFillColor(Color.green);

					}
				}
			} else {
				word_length = end_x - start_x;
				if ((end_y - start_y) == 0) {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x + i), (start_y));
						cell.setFillColor(Color.green);

					}
				} else if ((end_y - start_y) > 0) {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x + i), (start_y + i));
						cell.setFillColor(Color.green);

					}
				} else {
					for (int i = 0; i < word_length + 1; i++) {
						HSLFTableCell cell = table.getCell((start_x + i), (start_y - i));
						cell.setFillColor(Color.green);

					}
				}
			}

		}
	}

}
