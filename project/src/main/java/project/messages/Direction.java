package project.messages;

/**
 * Direction enum that holds up and down.
 *
 */
public enum Direction {
	UP, DOWN;

	/**
	 * Class that returns appropriate enum based on a passed in string.
	 * @param dir
	 * @return
	 */
	public static Direction toDirection(String dir) {
		if (dir.equals("Up")) {
			return UP;
		} else {
			return DOWN;
		}
	}
	
	/**
	 * Class that returns appropriate boolean based on a passed in enum.
	 * Convention across code is false = up and true = down.
	 * @param dir Direction being passed in
	 * @return
	 */
	public static boolean toBool(Direction dir) {
		return dir.equals(UP);
	}
}
