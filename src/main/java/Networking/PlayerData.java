package Networking;

import java.io.Serializable;

public class PlayerData implements Serializable {
	public int playerId;
	public int xLoc;
	public int yLoc;
	public int playerAction;
	public int aniIndex;
	public boolean defaultDirection;
//	animations[playerAction][aniIndex];
}
