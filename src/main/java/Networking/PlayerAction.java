package Networking;

import java.io.Serializable;

public class PlayerAction implements Serializable {
	public boolean moving = false, attacking = false;
	public boolean left = false, right = false, jump = false, knockback = false, sprinting = false;
}
