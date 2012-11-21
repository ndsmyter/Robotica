package common;

/**
 * 
 * @author sofie
 */
public class Sensor {
	public int xOffset;
	public int yOffset;
	public int dir;
	public int zMax;

	public Sensor(int xOffset, int yOffset, int dir, int zMax) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.dir = dir;
		this.zMax = zMax;
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
		Sensor other = (Sensor) obj;
		return xOffset == other.xOffset && yOffset == other.yOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sensor [x=" + xOffset + ", y=" + yOffset + ", dir=" + dir + "]";
	}
}
