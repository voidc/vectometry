package io.github.voidc.vectometry;

import java.util.Arrays;
import java.util.List;

public class Segment extends Line {

	/**
	 * creates a new segment with the given start and end point
	 * @param pointA start
	 * @param pointB end
	 */
	public Segment(Vector pointA, Vector pointB) {
		super(pointA, pointB.subtract(pointA));
	}
	
	/**
	 * creates a segment from this vector
	 * @param vector
	 * @see Line#Line(Vector)
	 */
	public Segment(Vector vector) {
		this(Vector.ZERO, vector);
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
	public Vector intersection(Line other) {
		Vector intersection = super.intersection(other);
		return this.contains(intersection) ? intersection : null;
	}
	
	@Override
	public Vector[] intersections(Circle circle) {
		List<Vector> intersections = Arrays.asList(super.intersections(circle));
		for(Vector is : intersections) {
			if(!this.contains(is)) {
				intersections.remove(is);
			}
		}
		return intersections.toArray(new Vector[intersections.size()]);
	}
	
	@Override
	public Vector[] intersections(Polygon poly) {
		List<Vector> intersections = Arrays.asList(super.intersections(poly));
		for(Vector is : intersections) {
			if(!this.contains(is)) {
				intersections.remove(is);
			}
		}
		return intersections.toArray(new Vector[intersections.size()]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Segment)) {
			return false;
		}
		Segment other = (Segment) obj;
		return (this.pointA().equals(other.pointA()) && this.pointB().equals(other.pointB()))
			|| (this.pointA().equals(other.pointB()) && this.pointB().equals(other.pointA()));
	}

}
