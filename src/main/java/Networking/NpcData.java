package Networking;

import java.io.Serializable;

public class NpcData implements Serializable {
	public int xLoc;
	public int yLoc;
	public int enemyState;
	public int aniIndex;
	public int direction;
	public boolean isActive;
	public int healthWidth;
}
