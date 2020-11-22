package crach.stage.game.entites.Shell;

import com.badlogic.gdx.math.Vector2;
import crach.stage.game.Assest;

public class Medium_Shell extends Shell{

	public Medium_Shell(Vector2 position, float r, float force, short mask) {
		super(position, r, force, TypeShell.Medium_Shell, mask);
	}

	@Override
	public void setTexture() {
		setTexture(Assest.Medium_Shell);
		
	}

}
