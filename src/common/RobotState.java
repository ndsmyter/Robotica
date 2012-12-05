package common;

import java.awt.Point;

public class RobotState {

	public int x;
	public int y;
	public int dir;

	public RobotState(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	public RobotState(Point p, int dir) {
		this.x = p.x;
		this.y = p.y;
		this.dir = dir;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		RobotState other = (RobotState) obj;
		return x == other.x && y == other.y && dir == other.dir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RobotState [x=" + x + ", y=" + y + ", dir=" + dir + "]";
	}
	
	public RobotState clone() {
		return new RobotState(x, y, dir);
	}
}
