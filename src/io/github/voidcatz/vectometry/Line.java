package io.github.voidcatz.vectometry;

import io.github.voidcatz.vectometry.util.Angle;

public class Line {
	public final Vector point1, point2;
	
	public static final Line xAXIS = new Line(new Vector(1, 0));
	public static final Line yAXIS = new Line(new Vector(0, 1));
	
	public Line(Vector point1, Vector point2) {
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public Line(float slope, float yIntersect) {
		this(new Vector(0, yIntersect), new Vector(0, yIntersect).add(new Vector(1, slope)));
	}
	
	public Line(Vector vec) {
		this(Vector.ZERO, vec);
	}
	
	public Vector vector() {
		return point2.substract(point1);
	}
	
	public float slope() {
		return this.vector().slope();
	}
	
	public Vector axisIntersects() {
		float y = this.point1.y - this.slope() * this.point1.x;
		float x = -y/this.slope();
		return new Vector(x, y);
	}
	
	public Vector intersect(Line other) { // http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
		float x = (other.axisIntersects().y - this.axisIntersects().y) / (this.slope() - other.slope());
		float y = this.slope() * x + this.axisIntersects().y;
		return new Vector(x, y);
	}
	
	public Angle angle(Line l) {
		return this.vector().angle(l.vector());
	}
	
	public boolean contains(Vector vec) {
		return vec.y == this.slope()*vec.x + this.axisIntersects().y; //TODO: test
	}
	
	public Line getPerpendicular(Vector point) {
		return new Line(point, point.add(this.vector()));
	}
	
	public Line getParallel(Vector point) {
		return new Line(point, point.add(this.vector()));
	}
	
	public boolean isParallel(Line other) {
		return this.vector().isParallel(other.vector());
	}

}
