package io.github.voidcatz.vectometry;

import io.github.voidcatz.vectometry.util.Angle;

public class Line {
	public final Vector point, direction;
	
	public static final Line xAXIS = new Line(new Vector(1, 0));
	public static final Line yAXIS = new Line(new Vector(0, 1));
	
	/**
	 * creates a new line which goes through the given point and has the given direction
	 * @param point
	 * @param direction
	 */
	public Line(Vector point, Vector direction) {
		this.point = point;
		this.direction = direction;
	}
	
	/** 
	 * creates a new line with the given slope and the y intersect
	 * @param slope
	 * @param yIntersect
	 */
	public Line(float slope, float yIntersect) {
		this(new Vector(0, yIntersect), new Vector(1, slope));
	}
	
	/**
	 * creates a new line which goes through the origin and has the given direction
	 * @param direction
	 */
	public Line(Vector direction) {
		this(Vector.ZERO, direction);
	}
	
	/**
	 * @return the slope of the line
	 * @see Vector#slope()
	 */
	public float slope() {
		return direction.slope();
	}
	
	/**
	 * @return the x and y intersects
	 */
	public Vector axisIntersects() {
		float y = this.point.y - this.slope() * this.point.x;
		float x = -y/this.slope();
		return new Vector(x, y);
	}
	
	/**
	 * @param other
	 * @return the intersect between this line and the other line
	 */
	public Vector intersect(Line other) { // http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
		float x = (other.axisIntersects().y - this.axisIntersects().y) / (this.slope() - other.slope());
		float y = this.slope() * x + this.axisIntersects().y;
		return new Vector(x, y);
	}
	
	/**
	 * @param other
	 * @return the angle between this line and the other line
	 */
	public Angle angle(Line other) {
		return this.direction.angle(other.direction);
	}
	
	/**
	 * @param point
	 * @return true if this line contains the given point
	 */
	public boolean contains(Vector point) {
		return point.y == this.slope()*point.x + this.axisIntersects().y;
	}
	
	/**
	 * @param point to be projected
	 * @return the orthogonal projection of the vector on this line
	 */
	public Vector projection(Vector point) {
		return point.add(point.subtract(point).project(this.direction));
	}
	
	/**
	 * @param point
	 * @return reflected point
	 */
	public Vector reflection(Vector point) {
		return point.reflect(this.projection(point));
	}
	
	/**
	 * @param point
	 * @return line which is parallel to this line and goes through the given point
	 */
	public Line getParallel(Vector point) {
		return new Line(point, point.add(this.direction));
	}
	
	/**
	 * @param other
	 * @return true if the given line is parallel to this line
	 */
	public boolean isParallel(Line other) {
		return this.direction.isParallel(other.direction);
	}

}
