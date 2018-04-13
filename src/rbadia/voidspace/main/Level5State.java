package rbadia.voidspace.main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.GameObject;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.sounds.SoundManager;

public class Level5State extends Level2State{
	private static final long serialVersionUID = -2094575762243216079L;
	protected MegaMan shipL;
	protected List<Bullet> bulletShipL;
	protected boolean wait = false;
	private long lastBulletTime;

	public long myLong = 1234;

	// Constructors
	public Level5State(int level, MainFrame frame, GameStatus status, 
			LevelLogic gameLogic, InputHandler inputHandler,
			GraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}

	@Override 
	public void drawAsteroid(){

	}

	//Getters
	public List<Bullet> getBulletShipL() 			{ return bulletShipL; 		}

	//Override to add the boss and its bullets
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
		
		drawShipL();
		moveShipL();
		drawBulletShipL();
		delayBullet();
		
		checkBulletMegaManCollisions();
		checkBossMegaManCollisions();
		checkBulletBossCollisions();

		// update asteroids destroyed (score) label  
		getMainFrame().getDestroyedValueLabel().setText(Long.toString(status.getAsteroidsDestroyed()));
		// update lives left label
		getMainFrame().getLivesValueLabel().setText(Integer.toString(status.getLivesLeft()));
		//update level label
		getMainFrame().getLevelValueLabel().setText(Long.toString(status.getLevel()));
	}

	public void doStart() {	
		newShipL(this);
		bulletShipL = new ArrayList<Bullet>();
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
	}

	//Creates the boss 
	public MegaMan newShipL(Level5State screen){
		int xPos = SCREEN_WIDTH - MegaMan.WIDTH;
		int yPos = SCREEN_HEIGHT/2 - 50;
		shipL = new MegaMan(xPos, yPos);
		return shipL;
	}

	//Draws the boss 
	public void drawShipL() {
		Graphics2D g2d = getGraphics2D();
		getGraphicsManager().drawShipL(shipL, g2d, this);
	}


	//Moves the boss upwards and downwards 
	public void moveShipL() {
		if(shipL.getY() == SCREEN_HEIGHT/2 - 50) {
			megaMan.setDirectionShip(1);
		} else if(shipL.getY() ==  SCREEN_HEIGHT - floor[7].getHeight()){
			megaMan.setDirectionShip(-1);
		}
		shipL.translate(0, megaMan.getDirectionShip()*2);
	}

	//Moves the boss bullets 
	public boolean moveBulletShipL(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){ 
			bullet.translate(bullet.getSpeed()*(-1), 0);
			return false;
		}
		else{
			return true;
		}
	}

	//Fire the boss bullets 
	public void fireBulletShipL(){
		Bullet bullet = new Bullet(shipL.x - Bullet.WIDTH/2, shipL.y + shipL.width/2 - Bullet.HEIGHT +2);
		bullet.setDirection(GameObject.LEFT);
		bulletShipL.add(bullet);
		this.getSoundManager().playBulletSound();
	}

	//Draws the boss bullets 
	protected void drawBulletShipL() {
		Graphics2D g2d = getGraphics2D();
		for(int i=0; i<bulletShipL.size(); i++){
			Bullet bullet = bulletShipL.get(i);
			getGraphicsManager().drawBulletShipL(bullet, g2d, this);

			boolean remove =   this.moveBulletShipL(bullet);
			if(remove){
				bulletShipL.remove(i);
				i--;

			}
		}
	}

	//Makes the firing on the boss slower 
	public void delayBullet() {
		// fire only up to 5 bullets per second
		long currentTime = System.currentTimeMillis();
		if((currentTime - lastBulletTime) > 5000/5){
			lastBulletTime = currentTime;
			fireBulletShipL();
		}
	}
	
	//Checks if the boss bullets intersect megaMan 
	protected void checkBulletMegaManCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bulletShipL.size(); i++){
			Bullet bullet = bulletShipL.get(i);
			if(megaMan.intersects(bullet)){
				status.setLivesLeft(status.getLivesLeft() - 1);
				// remove bullet
				bulletShipL.remove(i);
				break;
			}
		}
	}
	
	//Checks if megaMan intersects the boss 
	protected void checkBossMegaManCollisions() {
		GameStatus status = getGameStatus();
		if(shipL.intersects(megaMan)){
			status.setLivesLeft(status.getLivesLeft() - 1);
		}
	}
	
	//Checks if the boss intersects megaMan bullets 
	protected void checkBulletBossCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(shipL.intersects(bullet)){
				status.setLivesLeftBoss(status.getLivesLeftBoss() - 1);
				this.getSoundManager().playAsteroidExplosionSound();
				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
}
