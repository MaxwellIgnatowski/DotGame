import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DotGame extends Application {
	final String appName = "Dot Game";
	final int FPS = 10; // frames per second
	final int WIDTH = 600;
	final int HEIGHT = 500;
	final int[] numberOfTargets = {1, 4, 5, 7, 9, 12, 15};
	
	ArrayList<ScoreTarget> targets = new ArrayList<ScoreTarget>();
	int player_x, player_y;
	int level, score, gameState;
	double time;
	String formattedTime;
	
	DecimalFormat df;
	Font explanationFont, scoreFont;
	GraphicsContext gc;
	
	public static AudioClip smack, deadfly;
	
	/**
	 * Set up initial data structures/values
	 */
	void initialize()
	{
		gameState = 0;
		level = 1;
		addScoreTargetsForLevel();
		player_x = WIDTH - 100; player_y = HEIGHT - 100;
		
		df = new DecimalFormat("#.#");
		explanationFont = Font.font("Serif", 30);
		scoreFont = Font.font("Serif", 15);
	}
	
	void setHandlers(Scene scene)
	{
		scene.setOnMousePressed(
				e -> {
						if(gameState == 0) {
							gameState = 1;
						} else {
							int x = (int)e.getX();
							int y = (int)e.getY();
							for(int i = 0; i < targets.size(); i++) {
								ScoreTarget target = targets.get(i);
								if(target.isHit(x, y))
								{
									score += target.getScoreChange();
									target.getHit();
								}
							}
						}
					}
				);
		
		scene.setOnMouseMoved(
				e -> {
						int x = (int)e.getX();
						int y = (int)e.getY();
						player_x = x;
						player_y = y;
						render(gc);
					}
				);
	}

	/**
	 *  Update variables for one time step
	 */
	public void update()
	{
		if(gameState == 1) {
			time+=0.1;
			formattedTime = df.format(time);
			if((int)(100*Math.random()) >= 99)
			{
				targets.add(new ScoreTarget(WIDTH, HEIGHT, getLevelDelay(), true));
			}
			
			if(targets.size() == 0) {
				if(level >= 7) {
					gameState = 2;
				} else {
					level++;
					addScoreTargetsForLevel();
				}
			} else {
				for(int i = 0; i < targets.size(); i++) {
					targets.get(i).update();
					if(targets.get(i).shouldDisappear()) {
						targets.remove(i);
					}
				}
			}
		}
	}

	/**
	 *  Draw the game world
	 */
	void render(GraphicsContext gc) {
		
		// fill background
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		
		switch(gameState) {
		case 0:
			gc.setFont(explanationFont);
			gc.setFill(Color.WHITE);
			gc.fillText("Click on the green dots to get points!", 20, 80);
			gc.fillText("Don't click on the red dots, you'll lose points!", 20, 160);
			gc.fillText("Gold dots are rare and worth extra points!", 20, 240);
			gc.fillText("Click window to start game.", 20, 320);
			return;
		case 1:
			// show level, score, and time
			gc.setFont(scoreFont);
			gc.setFill(Color.WHITE);
			gc.fillText("Level: " + level, 10, 20);
			gc.fillText("Score: " + score, 10, 40);
			gc.fillText("Time: " + formattedTime, 10, 60);
			
			// draw targets
			for(int i = 0; i < targets.size(); i++) {
				targets.get(i).render(gc);
			}
			
			// draw player tool
		    gc.setStroke(Color.WHITE);
		    gc.strokeRect(player_x-10, player_y-10, 20, 20);
			return;
		case 2:
			gc.setFont(explanationFont);
			gc.setFill(Color.WHITE);
			gc.fillText("You completed the game in " + formattedTime + " seconds,", 20, 80);
			gc.fillText("and got a score of " + score + "!", 20, 160);
			if(score >= 55) {
				gc.fillText("Great job!", 20, 240);
			} else if(score >= 20) {
				gc.fillText("Good job!", 20, 240);
			} else if(score > 0) {
				gc.fillText("Okay job!", 20, 240);
			} else {
				gc.fillText("Better luck next time.", 20, 240);
			}
			return;
		}
		
	}

	/*
	 * Begin boiler-plate code...
	 * [Animation and events with initialization]
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage theStage) {
		theStage.setTitle(appName);

		Group root = new Group();
		Scene theScene = new Scene(root);
		theStage.setScene(theScene);

		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);

		gc = canvas.getGraphicsContext2D();

		// Initial setup
		initialize();
		setHandlers(theScene);
		
		// Setup and start animation loop (Timeline)
		KeyFrame kf = new KeyFrame(Duration.millis(1000 / FPS),
				e -> {
					// update position
					update();
					// draw frame
					render(gc);
				}
			);
		Timeline mainLoop = new Timeline(kf);
		mainLoop.setCycleCount(Animation.INDEFINITE);
		mainLoop.play();

		theStage.show();
	}
	/*
	 * ... End boiler-plate code
	 */
	
	private void addScoreTargetsForLevel() {
		for(int i = 0; i < numberOfTargets[level-1]; i++) {
			targets.add(new ScoreTarget(WIDTH, HEIGHT, getLevelDelay(), false));
		}
	}
	
	private int getLevelDelay()
	{
		return 50 - (4 * (level-1));
	}
}
