package rbadia.voidspace.main;

import java.awt.Graphics2D;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

public class Level4State extends Level3State {

	private static final long serialVersionUID = -2094575762243216079L;
	protected MegaMan powerUp;

	// Constructors
	public Level4State(int level, MainFrame frame, GameStatus status, 
			LevelLogic gameLogic, InputHandler inputHandler,
			GraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}

	//Override to add drawPowerUp method
	@Override
	public void updateScreen(){
		Graphics2D g2d = getGraphics2D();
		GameStatus status = this.getGameStatus();

		// save original font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}
		clearScreen();
		drawStars(50);
		drawFloor();
		drawPlatforms();
		drawMegaMan();
		drawAsteroid();
		drawBullets();
		drawBigBullets();
		checkBullletAsteroidCollisions();
		checkBigBulletAsteroidCollisions();
		checkMegaManAsteroidCollisions();
		checkAsteroidFloorCollisions();
		drawAsteroid2();
		checkBullletAsteroidCollisions2();
		checkBigBulletAsteroidCollisions2();
		checkMegaManAsteroidCollisions2();
		checkAsteroidFloorCollisions2();

		drawPowerUp();
		movePowerUp();

		// update asteroids destroyed (score) label  
		getMainFrame().getDestroyedValueLabel().setText(Long.toString(status.getAsteroidsDestroyed()));
		// update lives left label
		getMainFrame().getLivesValueLabel().setText(Integer.toString(status.getLivesLeft()));
		//update level label
		getMainFrame().getLevelValueLabel().setText(Long.toString(status.getLevel()));
	}

	@Override
	public void doStart() {	
		newPowerUp(this);
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
	}

	//Creates the power up image on top of the screen
	public MegaMan newPowerUp(Level4State screen){
		int xPos = (SCREEN_WIDTH - MegaMan.WIDTH) / 2;
		int yPos = MegaMan.HEIGHT;
		powerUp = new MegaMan(xPos, yPos);
		return powerUp;
	}

	//Draws the power up
	public void drawPowerUp() {
		Graphics2D g2d = getGraphics2D();
		getGraphicsManager().drawPowerUp(powerUp, g2d, this);
	}

	//Checks if the power up collides with the platform
	public boolean powerUpCollision() {
		Platform[] platforms = this.getPlatforms();
		for(int i=0; i<getNumPlatforms(); i++){
			if((((platforms[i].getX() < powerUp.getX()) && (powerUp.getX()< platforms[i].getX() + platforms[i].getWidth()))
					|| ((platforms[i].getX() < powerUp.getX() + powerUp.getWidth()) 
							&& (powerUp.getX() + powerUp.getWidth()< platforms[i].getX() + platforms[i].getWidth())))
					&& powerUp.getY() + powerUp.getHeight() == platforms[i].getY()
					){
				return false;
			}
		}
		return true;
	}

	//Moves the power up downward 
	public void movePowerUp(){
		if(powerUpCollision()) {
			powerUp.translate(0, 1);
		} 
		powerUpCollisionMegaMan();
	}

	//Checks if the power up collides with megaMan
	public void powerUpCollisionMegaMan() {
		GameStatus status = getGameStatus();
		if(powerUp.intersects(megaMan)){
			status.setLivesLeft(status.getLivesLeft() + 5);
			removePowerUp();
		}
	}

	//Removes the power up 
	public void removePowerUp(){
		powerUp.setLocation(-powerUp.getPixelsWide(), -powerUp.getPixelsTall());
	}
}