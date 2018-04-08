package rbadia.voidspace.model;

public class Platform extends GameObject {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 44;
	private static final int HEIGHT = 4;

	public Platform(int xPos, int yPos) {
		super(xPos, yPos, WIDTH, HEIGHT);
		this.setSpeed(-1);
	}
}
