package Utils;

import Main.Game;

public class Constants {

	public static class Environment {
		public static final int BG_LAYER2_WIDTH_DEFAULT = 320;
		public static final int BG_LAYER2_HEIGHT_DEFAULT = 180;
		public static final int BG_LAYER2_WIDTH = (int) (BG_LAYER2_WIDTH_DEFAULT * Game.SCALE);
		public static final int BG_LAYER2_HEIGHT = (int) (BG_LAYER2_HEIGHT_DEFAULT * Game.SCALE);

		public static final int BG_LAYER3_WIDTH_DEFAULT = 320;
		public static final int BG_LAYER3_HEIGHT_DEFAULT = 180;
		public static final int BG_LAYER3_WIDTH = (int) (BG_LAYER3_WIDTH_DEFAULT * Game.SCALE);
		public static final int BG_LAYER3_HEIGHT = (int) (BG_LAYER3_HEIGHT_DEFAULT * Game.SCALE);
	}

	public static class UI {
		public static class Buttons {
			public static final int B_WIDTH_DEFAULT = 140;
			public static final int B_HEIGHT_DEFAULT = 56;
			public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
			public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
		}

		public static class PauseButtons {
			public static final int SOUND_SIZE_DEFAULT = 42;
			public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEFAULT / 2 * Game.SCALE);
		}

		public static class UrmButtons {
			public static final int URM_DEFAULT_SIZE = 56;
			public static final int URM_SIZE = (int) (URM_DEFAULT_SIZE / 1.2 * Game.SCALE);
		}
	}

	public static class Directions {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}

	public static class PlayerConstants {
		public static final int IDLE = 0;
		public static final int RUN = 2;
		public static final int JUMP = 3;
		public static final int FALL = 4;
		public static final int ATTACK = 1;

		public static int GetSpriteAmount(int player_action) {
			switch (player_action) {
			case IDLE:
				return 5;
			case RUN:
				return 7;
			case ATTACK:
				return 5;
			case JUMP:
				return 7;
			case FALL:
				return 7;
			default:
				return 1;
			}
		}
	}
}
