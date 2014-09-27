package io.github.voidcatz.vectometry;

public class Segment extends Line {

	/**#creates a new segment with the given start and end point
	 * @param pointA start
	 * @param pointB end
	 */
	public Segment(Vector pointA, Vector pointB) {
		super(pointA, pointB.subtract(pointA));
	}
	
	/**
	 * @return start point
	 */
	public Vector pointA() {
		return point;
	}
	
	/**
	 * @return end point
	 */
	public Vector pointB() {
		return point.add(direction);
	}
	
	/**
	 * @return length of the segment
	 */
	public float length() {
		return this.direction.length();
	}
	
	@Override
	public Vector projection(Vector point) {
		Vector proj = super.projection(point);
		return this.contains(proj) ? proj : null;
	}
	
	@Override
	public boolean contains(Vector vector) {
		return super.contains(vector) && vector.distance(this.point) <= this.length();
	}
	
	@Override
	public Vector intersect(Line other) {
		Vector intersect = super.intersect(other);
		return this.contains(intersect) ? intersect : null;
	}

}
