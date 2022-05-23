import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ScoreTarget {

	private final int WIDTH, HEIGHT, DELAY;
	private int x, y, currentDelay;
	private int color;
	private boolean shouldDisappear;
	
	public ScoreTarget(int WIDTH, int HEIGHT, int DELAY, boolean isGold) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.DELAY = DELAY;
		this.currentDelay = DELAY;
		
		if(isGold) {
			color = 2;
		} else {
			color = (int)(2*Math.random());
		}
		shouldDisappear = false;
		setRandomLocation();
	}
	
	public boolean isHit(int hitX, int hitY)
	{
		return hitX-10 < this.x && this.x < hitX+10 && hitY-10 < this.y && this.y < hitY+10;
	}
	
	public void getHit()
	{
		this.shouldDisappear = true;
	}
	
	public boolean shouldDisappear() { return shouldDisappear; }
	
	public int getScoreChange() 
	{ 
		int value = 0;
		switch(color)
		{
		case 0:
			value = -5;
			break;
		case 1:
			value = 1;
			break;
		case 2:
			value = 5;
			break;
		}
		return value;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void render(GraphicsContext gc) {
		Color displayColor = Color.WHITE;
		switch(color)
		{
		case 0:
			displayColor = Color.RED;
			break;
		case 1:
			displayColor = Color.GREEN;
			break;
		case 2:
			displayColor = Color.GOLD;
			break;
		}
		gc.setFill(displayColor);
	 	gc.fillOval(x-5, y-5, 10, 10);
	}
	
	public void update() {
		if(--currentDelay == 0) {
			if(color == 2) {
				shouldDisappear = true;
			} else {
				setRandomLocation();
				changeColor();
				currentDelay = DELAY;
			}
		} else {
			int dx = (int)(25*Math.random()-10);
			int dy = (int)(25*Math.random()-10);
			x += dx; y += dy;
			if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT)
			{
				setRandomLocation();
				if(color != 2) {
					changeColor();
				}
			}
		}
	}
	
	private void setRandomLocation()
	{
		this.x = (int)(Math.random() * WIDTH);
		this.y = (int)(Math.random() * HEIGHT);
	}
	
	private void changeColor() { color = (color == 1) ? 0 : 1; }

}
