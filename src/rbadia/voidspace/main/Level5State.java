package rbadia.voidspace.main;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.sounds.SoundManager;

public class Level5State extends Level2State{
	private static final long serialVersionUID = -2094575762243216079L;
	
	// Constructors
		public Level5State(int level, MainFrame frame, GameStatus status, 
				LevelLogic gameLogic, InputHandler inputHandler,
				GraphicsManager graphicsMan, SoundManager soundMan) {
			super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
		}
		
		@Override 
		public void drawAsteroid(){
			
		}
		
		
}
