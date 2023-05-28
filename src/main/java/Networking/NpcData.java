package Networking;

import java.io.Serializable;

import Entities.LightBandit;

public class NpcData implements Serializable {
	public int xLoc;
	public int yLoc;
	public int enemyState;
	public int aniIndex;
	public int direction;
	public LightBandit lBandit;
	public boolean isActive;
}
