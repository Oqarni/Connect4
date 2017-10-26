import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

public class Connect4Menu extends Application{
	
	private Stage stage;
	private GameMenu gameMenu; //object gameMenu of type GameMenu defined below
	

	/**
	 * start function that sets up the Stage, scene and panes for the menu
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		Pane root = new Pane();
		root.setPrefSize(800,  600);
		
		//Load background Image
		InputStream is = Files.newInputStream(Paths.get("C:\\Users\\osama\\IdeaProjects\\Connect4\\src\\woodcon1.jpg"));
		Image img = new Image(is);
		is.close();
		
		ImageView imgView = new ImageView(img);
		imgView.setFitHeight(600);
		imgView.setFitWidth(800);
		
		//new instance of GameMenu
		gameMenu = new GameMenu();
		
		root.getChildren().addAll(imgView, gameMenu);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect4");
		primaryStage.setHeight(600);
		primaryStage.setWidth(800);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}
	
	
	
	/**
	 * Class GameMenu:
	 * creates two menus and animates them to replace each other
	 * ass well as have extra buttons.
	 * @author Osama
	 *
	 */
	private class GameMenu extends Parent {
		public GameMenu() {
			VBox menu0 = new VBox(10);
			VBox menu1 = new VBox(10);
			
			menu0.setTranslateX(100);
			menu0.setTranslateY(100);
			
			menu1.setTranslateX(100);
			menu1.setTranslateY(100);
			
			final int offset = 500;
			
			menu1.setTranslateX(offset);
			
			MenuButton btnNewGame = new MenuButton("NEW GAME");
			btnNewGame.setOnMouseClicked(event -> {
				getChildren().add(menu1);
				
				TranslateTransition translate0 = new TranslateTransition(Duration.seconds(0.25), menu0);
				translate0.setToX(menu0.getTranslateX() - offset);
				
				TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), menu1);
				translate1.setToX(menu0.getTranslateX());
				
				translate0.play();
				translate1.play();
				
				translate0.setOnFinished(evnt -> {
					getChildren().remove(menu0);
				});
				
			});
			
			
			MenuButton btnSingle = new MenuButton("SINGLE PLAYER");
			btnSingle.setOnMouseClicked(event -> {
				
			});
			
			MenuButton btnMulti = new MenuButton("MULTI-PLAYER");
			btnMulti.setOnMouseClicked(event -> {
				Connect4App newGame = new Connect4App();
				try {
					newGame.start(stage);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			MenuButton btnBack = new MenuButton("BACK");
			btnBack.setOnMouseClicked(event -> {
				getChildren().add(menu0);
				
				TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.25), menu1);
				translate1.setToX(menu1.getTranslateX() + offset);
				
				TranslateTransition translate0 = new TranslateTransition(Duration.seconds(0.5), menu0);
				translate0.setToX(menu1.getTranslateX());
				
				translate1.play();
				translate0.play();
				
				translate1.setOnFinished(evnt -> {
					getChildren().remove(menu1);
				});
				
			});
			
			
			MenuButton btnQuit = new MenuButton("QUIT");
			btnQuit.setOnMouseClicked(event -> {
				System.exit(0);
			});
			
			
			menu0.getChildren().addAll(btnNewGame, btnQuit);
			menu1.getChildren().addAll(btnSingle, btnMulti, btnBack);
			
			Rectangle bg = new Rectangle(800, 600);
			bg.setFill(Color.GRAY);
			bg.setOpacity(0.4);
			
			this.getChildren().addAll(bg, menu0);
			
		}
	}
	
	
	
	
	/**
	 * contructs a text object with a rectangular background
	 * that moves and glows.
	 * @author Osama
	 *
	 */
	private static class MenuButton extends StackPane {
		private Text menuText;
		
		public MenuButton(String name){
			menuText = new Text(name);
			menuText.setFont(menuText.getFont().font(20));
			menuText.setFill(Color.WHITE);
			
			Rectangle br = new Rectangle(250, 30);
			br.setOpacity(0.7);
			br.setFill(Color.BLACK);
			br.setEffect(new GaussianBlur(3.5));
			
			this.setAlignment(Pos.TOP_LEFT);
			this.getChildren().addAll(br, menuText);
			
			this.setOnMouseEntered(event -> {
				br.setTranslateX(-10);
				menuText.setTranslateX(-10);
				br.setFill(Color.WHITE);
				menuText.setFill(Color.BLACK);
			});
			
			this.setOnMouseExited(event -> {
				br.setTranslateX(0);
				menuText.setTranslateX(0);
				br.setFill(Color.BLACK);
				menuText.setFill(Color.WHITE);
			});
			
			DropShadow drop = new DropShadow(50, Color.WHITE);
			drop.setInput(new Glow());
			
			this.setOnMousePressed(event -> setEffect(drop));
			this.setOnMouseReleased(event -> setEffect(null));
			
		}
	}

	public static void main(String[] args) {
		launch(args);

	}

	

}
