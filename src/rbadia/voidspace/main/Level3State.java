package rbadia.voidspace.main;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Floor;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

/**
 * Level very similar to LevelState1.  
 * Platforms arranged in triangular form. 
 * Asteroids travel at 225 degree angle
 */
public class Level3State extends Level2State {

	private static final long serialVersionUID = -2094575762243216079L;
	protected Asteroid asteroid2;

	// Constructors
	public Level3State(int level, MainFrame frame, GameStatus status, 
			LevelLogic gameLogic, InputHandler inputHandler,
			GraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}

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

		// update asteroids destroyed (score) label  
		getMainFrame().getDestroyedValueLabel().setText(Long.toString(status.getAsteroidsDestroyed()));
		// update lives left label
		getMainFrame().getLivesValueLabel().setText(Integer.toString(status.getLivesLeft()));
		//update level label
		getMainFrame().getLevelValueLabel().setText(Long.toString(status.getLevel()));
	}
	
	@Override
	public void doStart() {	
		GameStatus status = this.getGameStatus();
		newAsteroid2(this);
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
		status.setNewAsteroid2(false);
	}
	
	// Creates the additional asteroid
	public Asteroid newAsteroid2(Level3State screen){
		int xPos = (int) (Asteroid.WIDTH);
		int yPos = rand.nextInt((int)(SCREEN_HEIGHT - Asteroid.HEIGHT- 32));
		asteroid2 = new Asteroid(xPos, yPos);
		return asteroid2;
	}
	
	// Manages the asteroid2 coordinates as it moves or creates a new one if needed
	protected void drawAsteroid2() {
		// Asteroid2
		Graphics2D g2d = getGraphics2D();
				if((asteroid2.getX() + asteroid2.getWidth() <  SCREEN_WIDTH)){
					Random rand = new Random();
					int constant = rand.nextInt(4);
					asteroid2.translate(asteroid2.getSpeed() + constant, asteroid2.getSpeed()/2 + constant);
					getGraphicsManager().drawAsteroid2(asteroid2, g2d, this);	
				}
				else {
					long currentTime = System.currentTimeMillis();
					if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
						// draw a new asteroid
						lastAsteroidTime = currentTime;
						asteroid2.setLocation(asteroid2.getPixelsWide(), (rand.nextInt((int) (SCREEN_HEIGHT - asteroid2.getPixelsTall() - 32))));
					}

					else{
						// draw explosion
						getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
					}
				}
	}
	
	public void removeAsteroid2(Asteroid asteroid){
		asteroidExplosion = new Rectangle(
				asteroid.x,
				asteroid.y,
				asteroid.getPixelsWide(),
				asteroid.getPixelsTall());
		asteroid.setLocation(-asteroid.getPixelsWide(), -asteroid.getPixelsTall());
		this.getGameStatus().setNewAsteroid(true);
		lastAsteroidTime = System.currentTimeMillis();
		// play asteroid explosion sound
		this.getSoundManager().playAsteroidExplosionSound();
	}
	
	// Check bulletCollisions for second asteroid
	protected void checkBullletAsteroidCollisions2() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(asteroid2.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid2(asteroid2);
				levelAsteroidsDestroyed++;
				damage=0;
				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
	
	// Check big 
	protected void checkBigBulletAsteroidCollisions2() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if(asteroid2.intersects(bigBullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);
				removeAsteroid2(asteroid2);
				damage=0;
			}
		}
	}
	
	protected void checkMegaManAsteroidCollisions2() {
		GameStatus status = getGameStatus();
		if(asteroid2.intersects(megaMan)){
			status.setLivesLeft(status.getLivesLeft() - 1);
			removeAsteroid2(asteroid2);
		}
	}
	
	protected void checkAsteroidFloorCollisions2() {
		for(int i=0; i<9; i++){
			if(asteroid2.intersects(floor[i])){
				removeAsteroid2(asteroid2);

			}
		}
	}
	
	@Override
	public Platform[] newPlatforms(int n){
		platforms = new Platform[n];
		for(int i=0; i<n; i++){
			this.platforms[i] = new Platform(0,0);
			if(i<4)	platforms[i].setLocation(50+ i*50, SCREEN_HEIGHT/2 + 140 - i*40);
			if(i==4) platforms[i].setLocation(50 +i*50, SCREEN_HEIGHT/2 + 140 - 3*40);
			if(i>4){	
				int k=4;
				platforms[i].setLocation(50 + i*50, SCREEN_HEIGHT/2 + 20 + (i-k)*40 );
				k=k+2;
			}
		}
		return platforms;
	}

	//Methods to move platform 2, 5 and megaMan (if he's on top of them) sideways
	public void movePlatformHorizontal2(Platform platform) {
		platform.translate(platform.getSpeed(), 0);
		if(platform.getX() <= 0) {
			platform.setSpeed(1);
		} 
		if(platform.getX() >= 50+ 2*50) {
			platform.setSpeed(-1);
		}
		if(!FallMovingPlatforms(2)) {
			megaMan.translate(platform.getSpeed(), 0);
		}
	}

	public void movePlatformHorizontal5(Platform platform) {
		platform.translate(platform.getSpeed(), 0);
		if(platform.getX() <= 50+ 5*50) {
			platform.setSpeed(1);
		} 
		if(platform.getX() >= 450) {
			platform.setSpeed(-1);
		}
		if(!FallMovingPlatforms(5)) {
			megaMan.translate(platform.getSpeed(), 0);
		}
	}

	//Checks if megaMan is on top of platform 2 and 5
	public boolean FallMovingPlatforms(int n){
		MegaMan megaMan = this.getMegaMan(); 
		Platform[] platforms = this.getPlatforms();
		if((((platforms[n].getX() < megaMan.getX()) && (megaMan.getX()< platforms[n].getX() + platforms[n].getWidth()))
				|| ((platforms[n].getX() < megaMan.getX() + megaMan.getWidth()) 
						&& (megaMan.getX() + megaMan.getWidth()< platforms[n].getX() + platforms[n].getWidth())))
				&& megaMan.getY() + megaMan.getHeight() == platforms[n].getY()
				){
			return false;
		}
		return true;
	}

	//Animate the platform 2 and 5 sideways
	@Override
	protected void drawPlatforms()  {
		Graphics2D g2d = getGraphics2D();
		movePlatformHorizontal2(platforms[2]);
		movePlatformHorizontal5(platforms[5]);
		for(int i=0; i<getNumPlatforms(); i++){
			getGraphicsManager().drawPlatform(platforms[i], g2d, this, i);
		}
	}
}