package edu.uoregon;

public enum Border {

	NONE(0.0, 0.0, 0.0, 0.0),
	EUGENE(32.3214, -123.322, 32.123, -123.322),
	SANFRAN(37.7889544, -122.418728, 37.786699, -122.41309583);

	public final double top;
	public final double left;
	public final double right;
	public final double bottom;

	Border(double top, double left, double bottom, double right) {
		this.top = top;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
	}

}
