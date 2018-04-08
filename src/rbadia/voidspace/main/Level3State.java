package rbadia.voidspace.main;
import java.awt.Graphics2D;

import rbadia.voidspace.graphics.GraphicsManager;
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

	// Constructors
	public Level3State(int level, MainFrame frame, GameStatus status, 
			LevelLogic gameLogic, InputHandler inputHandler,
			GraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}

	@Override
	public void doStart() {	
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
	}

	@Override
	protected void drawAsteroid() {
		Graphics2D g2d = getGraphics2D();
		if((asteroid.getX() + asteroid.getPixelsWide() >  0)) {
			asteroid.translate(-asteroid.getSpeed(), asteroid.getSpeed()/2);
			getGraphicsManager().drawAsteroid(asteroid, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){

				asteroid.setLocation(SCREEN_WIDTH - asteroid.getPixelsWide(),
						rand.nextInt(SCREEN_HEIGHT - asteroid.getPixelsTall() - 32));
			}
			else {
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
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

	/*public void movePlatform(Platform platform) {
		platform.translate(0, platform.getSpeed());
		if(platform.getY() <= 200) {
			platform.setSpeed(1);
		} 
		if(platform.getY() >= SCREEN_HEIGHT- Floor.HEIGHT/2) {
			platform.setSpeed(-1);
		}
	}*/

	//Methods to move platform 2, 5 and megaMan (if he's on top of them) sideways
	public void movePlatformHorizontal2(Platform platform) {
		platform.translate(platform.getSpeed(), 0);
		if(platform.getX() <= 0) {
			platform.setSpeed(1);
		} 
		if(platform.getX() >= 50+ 2*50) {
			platform.setSpeed(-1);
		}
		if(!Fall2()) {
			megaMan.translate(platform.getSpeed(), 0);
		}
	}

	public void movePlatformHorizontal5(Platform platform) {
		platform.translate(platform.getSpeed(), 0);
		if(platform.getX() <= 50+ 5*50) {
			platform.setSpeed(1);
		} 
		if(platform.getX() >= 0 + 7 * Floor.WIDTH) {
			platform.setSpeed(-1);
		}
		if(!Fall5()) {
			megaMan.translate(platform.getSpeed(), 0);
		}
	}

	//Checks if megaMan is on top of platform 2 and 5
	public boolean Fall2(){
		MegaMan megaMan = this.getMegaMan(); 
		Platform[] platforms = this.getPlatforms();
		if((((platforms[2].getX() < megaMan.getX()) && (megaMan.getX()< platforms[2].getX() + platforms[2].getWidth()))
				|| ((platforms[2].getX() < megaMan.getX() + megaMan.getWidth()) 
						&& (megaMan.getX() + megaMan.getWidth()< platforms[2].getX() + platforms[2].getWidth())))
				&& megaMan.getY() + megaMan.getHeight() == platforms[2].getY()
				){
			return false;
		}
		return true;
	}

	public boolean Fall5(){
		MegaMan megaMan = this.getMegaMan(); 
		Platform[] platforms = this.getPlatforms();
		if((((platforms[5].getX() < megaMan.getX()) && (megaMan.getX()< platforms[5].getX() + platforms[5].getWidth()))
				|| ((platforms[5].getX() < megaMan.getX() + megaMan.getWidth()) 
						&& (megaMan.getX() + megaMan.getWidth()< platforms[5].getX() + platforms[5].getWidth())))
				&& megaMan.getY() + megaMan.getHeight() == platforms[5].getY()
				){
			return false;
		}
		return true;
	}

	//Animate the platform 2 and 5 sideways
	protected void drawPlatforms()  {
		Graphics2D g2d = getGraphics2D();
		movePlatformHorizontal2(platforms[2]);
		movePlatformHorizontal5(platforms[5]);
		for(int i=0; i<getNumPlatforms(); i++){
			getGraphicsManager().drawPlatform(platforms[i], g2d, this, i);
		}
	}
}
