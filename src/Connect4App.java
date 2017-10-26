import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Connect4App extends Application {
	
	private static final int BLOCK_SIZE = 60; //width of columns
	private static final int COLUMNS = 7; // number of columns
	private static final int ROWS = 6;  //number of rows
	private static final int POS_X = 160; // horizontal offset
	private static final int POS_Y = 80;  // vertical offset
	private boolean redMove = true;    //player turn indicator
	private Disc[][] grid = new Disc[COLUMNS][ROWS];// object array of circles
	private int[][] gridNum = new int[COLUMNS][ROWS];//int array for logic
	private Pane discRoot = new Pane(); //pane for disc
	private Pane textRoot = new Pane(); // pane for text
	private Text statusText;
	private Stage stage;
	
	/**
	 * creates board for the game. basically sets up
	 * the panes.
	 * 
	 */
	private Parent createBoard() {
		Pane root = new Pane(); //pane for board
		
		statusText = new Text("select column to place disc....Red goes first!");
		statusText.getFont();
		statusText.setFont(Font.font(20));
		statusText.setFill(Color.WHITE);
		statusText.setTranslateY(550);
		textRoot.getChildren().add(statusText);
		
		root.getChildren().addAll(discRoot, textRoot);
		//root.getChildren().add(textRoot);
		Shape gridShape = makeGrid();
		
		root.getChildren().add(gridShape);
		root.getChildren().addAll(makeColumns());
		
		return root;
	}
	
	
	
	/**
	 * Constructs the rectangular grid with the circular holes.
	 * 
	 */
	private Shape makeGrid(){
		Shape shape = new Rectangle(BLOCK_SIZE*(COLUMNS+1), BLOCK_SIZE*(ROWS+1));
		
		shape.setTranslateX(160);
		shape.setTranslateY(80);
		
		for(int y =0; y < (ROWS*BLOCK_SIZE); y+=(BLOCK_SIZE+5)) {
			for(int x = 0; x < (COLUMNS*BLOCK_SIZE); x+=(BLOCK_SIZE+5)) {
				Circle slot = new Circle(BLOCK_SIZE/2);
				slot.setCenterX(BLOCK_SIZE/2);
				slot.setCenterY(BLOCK_SIZE/2);
				slot.setTranslateX(x + BLOCK_SIZE/4 + POS_X);
				slot.setTranslateY(y + BLOCK_SIZE/4 + POS_Y);
				
				shape = Shape.subtract(shape,  slot);
			}
		}
		DropShadow effect = new DropShadow();
        effect.setColor(Color.WHITE);
        shape.setEffect(effect);
		shape.setFill(Color.DARKBLUE);
		return shape;
	}
	
	
	
	
	/**
	 * Creates an array list of rectangles that highlight the chosen column
	 * 
	 */
	private List<Rectangle> makeColumns() {
		List<Rectangle> columnChooser = new ArrayList<>();
		DropShadow drop = new DropShadow(50, Color.WHITE);
		drop.setInput(new Glow());
		
		for(int x=0; x < COLUMNS; x++) {
			Rectangle chooser = new Rectangle(BLOCK_SIZE, BLOCK_SIZE*(ROWS+1));
			chooser.setTranslateX(x*(BLOCK_SIZE+5) + BLOCK_SIZE/4 + POS_X);
			chooser.setTranslateY(POS_Y);
			chooser.setFill(Color.TRANSPARENT);
			chooser.setOnMouseEntered(event -> chooser.setFill(Color.rgb(50,250,50,0.3)));
			chooser.setOnMouseExited(event -> chooser.setFill(Color.TRANSPARENT));
			
			final int column = x;
			chooser.setOnMouseClicked(event -> putDisk(new Disc(redMove), column));
			
			columnChooser.add(chooser);
		}
		
		return columnChooser;	
	}
	
	
	
	/**
	 * Places a new disc in the board.
	 * @param disc
	 * @param column
	 */
	private void putDisk(Disc disc, int column){
		int row = ROWS-1;
		do{
			if(!getDisc(column, row).isPresent())
				break;
			
			row--;
		}while(row >= 0);
		
		if(row < 0)
			return;
		
		
		if(redMove){
			gridNum[column][row] = 1;
			//System.out.println(gridNum[column][row]);
		}
			else gridNum[column][row] = 2;
		
				
		grid[column][row] = disc;
		discRoot.getChildren().add(disc);
		disc.setTranslateX(column*(BLOCK_SIZE+5) + BLOCK_SIZE/4 + POS_X);
		
		
		TranslateTransition discFall = new TranslateTransition(Duration.seconds(0.5), disc);
		discFall.setToY(row*(BLOCK_SIZE+5) + BLOCK_SIZE/4 + POS_Y);
		
		final int currentRow = row;
		
		redMove = !redMove;
		discFall.setOnFinished(event -> {
			if(gameEnded(column, currentRow)) {
				gameOver();
			}
			
		});
		discFall.play();
	}
	
	
	/**
	 * Checks to see if the last move won the game.
	 * @param column
	 * @param row
	 * @return
	 */
	private boolean gameEnded(int column, int row) {
		int count = 0;
		int playerNum = gridNum[column][row];
		
		/**
		 * Horizontal check:
		 */
		//right:
		for(int x = column; x < COLUMNS; x++){
			if(gridNum[x][row] == playerNum){
				count++;
			}
			else break;
		}
		//left:
		for(int x = column-1; x >= 0; x--){
			if(gridNum[x][row] == playerNum){
				count++;
			}
			else break;
		}
		if(count == 4)
			return true;
		
		
		/**
		 * Vertical Check:
		 */
		count = 0;
		//Up:
		for(int y = row; y < ROWS; y++){
			if(gridNum[column][y] == playerNum){
				count++;
			}
			else break;
		}
		//Down:
		for(int y = row-1; y >= 0; y--){
			if(gridNum[column][y] == playerNum){
				count++;
			}
			else break;
		}
		if(count == 4)
			return true;
		
		
		/**
		 * Horizontal check; top-left to bottom-right
		 */
		count = 0;
		//going right and down:
		for(int x=column,y=row; (x<COLUMNS)&&(y>=0); x++ ,y-- ){
			if(gridNum[x][y] == playerNum){
				count++;
			}
			else break;
		}
		//going left and up:
		for(int x=column-1, y=row+1; (x>=0)&&(y<ROWS); x--, y++){
			if(gridNum[x][y] == playerNum){
				count++;
			}
			else break;
		}
		if(count == 4)
			return true;
		
		
		
		
		/**
		 * Horizontal check; top-right to bottom-left
		 */
		count = 0;
		//going right and up:
		for(int x=column, y=row; (x<COLUMNS)&&(y<ROWS); x++, y++){
			if(gridNum[x][y] == playerNum){
				count++;
			}
			else break;
		}
		//going left and down:
		for(int x=column-1, y=row-1; (x>=0)&&(y>=0); x--, y--){
			if(gridNum[x][y] == playerNum){
				count++;
			}
			else break;
		}
		if(count == 4)
			return true;
		
		
		return false;
	}
	
	
	/**
	 * If game does end this puts a message and closes the app.
	 */
	private void gameOver() {
		String string = null;
		if(!redMove){
			string = "RED WINS!!!";
		}
		else string = "YELLOW WINS!!!";
		
		System.out.println("Game Over");
		Frame frame = new Frame();
		JOptionPane.showMessageDialog(frame,
			    string,
			    "GAME OVER",
			    JOptionPane.PLAIN_MESSAGE);
		Connect4Menu menu = new Connect4Menu();
		try {
			menu.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.exit(0);
	}
	
	
	
	/**
	 * Checks if there is space in the column of discs
	 * @param column
	 * @param row
	 * @return
	 */
	private Optional<Disc> getDisc(int column, int row){
		if(column < 0 || column >= COLUMNS
				|| row < 0 || row >= ROWS){
			return Optional.empty();
		}
		return Optional.ofNullable(grid[column][row]);
	}
	
	
	
	/**
	 * Constructs the discs to be put in the board.
	 * @author Osama
	 *
	 */
	private static class Disc extends Circle {
		private final boolean red;
		public Disc(boolean red){
			super(BLOCK_SIZE/2, red ? Color.RED : Color.YELLOW);
			this.red = red;
			
			setCenterX(BLOCK_SIZE/2);
			setCenterY(BLOCK_SIZE/2);
		}
	}
	

	@Override
	public void start(Stage board) throws Exception {
		stage = board;
		
		
		
		Scene scene = new Scene(createBoard());
		scene.setFill(Color.BLACK);
		
		board.setScene(scene);
		board.setWidth(800);
		board.setHeight(600);
		board.show();
		
	}
	
	

	public static void main(String[] args) {
		launch(args);

	}

}
