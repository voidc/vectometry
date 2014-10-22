package io.github.voidc.vectometry;

import java.util.ArrayList;
import java.util.List;

import io.github.voidc.vectometry.util.Angle;

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
	public Vector axisIntersections() {
		float y = this.point.y - this.slope() * this.point.x;
		float x = -y/this.slope();
		return new Vector(x, y);
	}
	
	/**
	 * @param other
	 * @return the intersect between this line and the other line
	 */
	public Vector intersection(Line other) {
		float x = (other.axisIntersections().y - this.axisIntersections().y) / (this.slope() - other.slope());
		float y = this.slope() * x + this.axisIntersections().y;
		return new Vector(x, y);
	}
	
	/**
	 * @param circle
	 * @return all intersections with the given circle
	 */
	public Vector[] intersections(Circle circle) {
        float m = this.slope();
        float t = this.axisIntersections().y;
        float cx = circle.center.x;
        float cy = circle.center.y;
        float r = circle.radius;
        
        float s = cx*m*(2*cy-cx*m+2*t)-cy*(cy+2*t)+2*r*(m*m+1)-t*t;
        float x1 = (float) ((-Math.sqrt(s)-cx-cy*m-m*t) / (m*m+1));
        float y1 = m*x1+t;
        float x2 = (float) ((Math.sqrt(s)-cx-cy*m-m*t) / (m*m+1));
        float y2 = m*x2+t;
        return new Vector[]{new Vector(x1, y1), new Vector(x2, y2)};
    }
	
	/**
	 * @param poly
	 * @return all intersects with the given polygon
	 */
	public Vector[] intersections(Polygon poly) {
		List<Vector> intersections = new ArrayList<Vector>();
		for(Segment seg : poly.segments()) {
			if(seg.intersection(this) != null) intersections.add(seg.intersection(this));
		}
		return intersections.toArray(new Vector[intersections.size()]);
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
		return point.y == this.slope()*point.x + this.axisIntersections().y;
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
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Line)) {
			return false;
		}
		Line other = (Line) obj;
		return this.axisIntersections().equals(other.axisIntersections());
	}

}
