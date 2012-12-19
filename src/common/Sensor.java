package common;

public class Sensor {
	public int xOffset;
	public int yOffset;
	public int dir;
	public int zMax;
	public double sigma;
	public int id;

	public Sensor(int xOffset, int yOffset, int dir, int zMax, double sigma, int id) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.dir = dir;
		this.zMax = zMax;
		this.sigma = sigma;
		this.id = id;
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
		return "Sensor [id=" + id + ", x=" + xOffset + ", y=" + yOffset
				+ ", dir=" + dir + "]";
	}
}
