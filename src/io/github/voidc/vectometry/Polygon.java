package io.github.voidc.vectometry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.voidc.vectometry.util.Angle;

public class Polygon implements IShape {
	public final Vector[] vertices;
	
	/**
	 * creates a new polygon with the given vertices
	 * @param vertices
	 */
	public Polygon(Vector[] vertices) {
		if(vertices.length < 3) throw new InvalidParameterException("A Polygon must at least have 3 vertices");
		this.vertices = vertices;
	}
	
	/**
	 * creates new regular polygon with the given side and the specified number of vertices
	 * @param side
	 * @param vertices
	 * @return regular polygon 
	 */
	public static Polygon regular(Segment side, int vertices) {
		if(vertices < 3) throw new InvalidParameterException("A Polygon must at least have 3 vertices");
		Angle angle = Angle.deg((vertices - 1) * 180 / vertices);
		Vector[] vtc = new Vector[vertices];
		vtc[0] = side.pointA();
		vtc[1] = side.pointB();
		for(int v = 2; v < vertices; v++) {
			vtc[v] = vtc[v-2].subtract(vtc[v-1]).rotate(angle, vtc[v-1]);
		}
		return new Polygon(vtc);
	}

	/**
	 * @return an array of all segments between the vertices
	 */
	public Segment[] segments() {
		Segment[] segments = new Segment[vertices.length];
		for(int v = 0; v < vertices.length; v++) {
			segments[v] = new Segment(vertices[v], vertices[v + 1 < vertices.length ? v + 1 : 0]);
		}
		return segments;
	}
	
	/**
	 * @return all diagonals of this polygons
	 * the polygon must have more than three vertices or else the returned array will be empty
	 */
	public Segment[] diagonals() {
		if(this.vertices.length <= 3) {
			return new Segment[0];
		}
		int newN = (this.n() * (this.n() - 3)) / 2;
		List<Segment> diagonals = new ArrayList<Segment>();
		for(int x = 0; diagonals.size() < newN; x++) {
			for(int v = 0; v < this.n(); v++) {
				Vector v2 = this.vertices[v == this.vertices.length - x ? 0 : v + x];
				Segment seg = new Segment(this.vertices[v], v2);
				if(!diagonals.contains(seg)) diagonals.add(seg);
			}
		}
		return diagonals.toArray(new Segment[newN]);
		
	}
	
	/**
	 * @return an array of all angles between the vertices
	 */
	public Angle[] angles() {
		Angle[] angles = new Angle[vertices.length];
		for(int v = 0; v < vertices.length; v++) {
			Vector v1 = this.vertices[v == 0 ? this.vertices.length - 1 : v - 1];
			Vector v2 = this.vertices[v == this.vertices.length - 1 ? 0 : v + 1];
			angles[v] = this.vertices[v].subtract(v1).angle(v2.subtract(this.vertices[v]));
		}
		return angles;
	}

	/**
	 * @param other polygon
	 * @return all intersects with the given polygon
	 */
	public Vector[] intersections(Polygon other) {
		List<Vector> intersects = new ArrayList<Vector>();
		for(Segment seg : this.segments()) {
			intersects.addAll(Arrays.asList(seg.intersection(seg)));
		}
		return intersects.toArray(new Vector[0]);
	}
	
//	/**
//	 * @param other
//	 * @return all intersections between the outline of this shape and the other shape
//	 */
//	public Vector[] intersections(IShape shape) {
//		if(shape instanceof Polygon) {
//			return this.intersections((Polygon) shape);
//		} else if(shape instanceof Circle) {
//			return ((Circle) shape).intersections(this);
//		} else return null;
//	}

	@Override
	public float perimeter() {
		float sum = 0;
		for(Segment seg : this.segments()) {
			sum += seg.length();
		}
		return sum;
	}
	
	@Override
	public float area() {
		float sum = 0;
		for(int v = 0; v < vertices.length; v++) {
			sum += vertices[v].cross( vertices[v + 1 < vertices.length ? v + 1 : 0]);
		}
		return Math.abs(sum/2);
	}
	
	@Override
	public boolean contains(Vector point) {
		Rectangle bounds = this.bounds();
		if(!bounds.contains(point)) {
			return false;
		}
		Segment ray = new Segment(new Vector(bounds.origin().x - bounds.width() / 100, point.y), point);
		int intersections = ray.intersections(this).length;
		return intersections % 2 != 0;
	}
	
	/**
	 * @param scalar factor by which the polygon is scaled
	 * @param center 
	 * @return polygon which is scaled starting from the given center point
	 */
	public Polygon scale(float scalar, Vector center) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			Vector difference = this.vertices[v].subtract(center);
			vtc[v] = center.add(difference.scale(scalar));
		}
		return new Polygon(vtc);
	}
	
	/**
	 * @param scalarX factor by which the polygon is scaled in the x direction
	 * @param scalarY factor by which the polygon is scaled in the y direction
	 * @param center 
	 * @return polygon which is scaled starting from the given center point
	 */
	public Polygon scale(float scalarX, float scalarY, Vector center) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			Vector difference = this.vertices[v].subtract(center);
			vtc[v] = center.add(difference.multiply(new Vector(scalarX, scalarY)));
		}
		return new Polygon(vtc);
	}
	
	/**
	 * @param scalar factor by which the polygon is scaled
	 * @return polygon which is scaled starting from the centroid
	 */
	public Polygon scale(float scalar) {
		return this.scale(scalar, this.centroid());
	}
	
	/**
	 * @param scalarX factor by which the polygon is scaled in the x direction
	 * @param scalarY factor by which the polygon is scaled in the y direction
	 * @return polygon which is scaled starting from the centroid
	 */
	public Polygon scale(float scalarX, float scalarY) {
		return this.scale(scalarX, scalarY, this.centroid());
	}
	
	@Override
	public Polygon move(Vector tranformation) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			vtc[v] = this.vertices[v].add(tranformation);
		}
		return new Polygon(vtc);
	}
	
	/**
	 * @param angle
	 * @param center
	 * @return polygon which is rotated by the angle around the center
	 */
	public Polygon rotate(Angle angle, Vector center) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			vtc[v] = this.vertices[v].rotate(angle, center);
		}
		return new Polygon(vtc);
	}
	
	/**
	 * @param angle
	 * @return polygon which is rotated by the angle around the center
	 */
	public Polygon rotate(Angle angle) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			vtc[v] = this.vertices[v].rotate(angle, this.centroid());
		}
		return new Polygon(vtc);
	}
	
	@Override
	public Rectangle bounds() {
		float xMin = vertices[0].x;
		float xMax = vertices[0].x;
		float yMin = vertices[0].y;
		float yMax = vertices[0].y;
		for(Vector vertex : vertices) {
			if(vertex.x > xMax) xMax = vertex.x;
			if(vertex.x < xMax) xMin = vertex.x;
			if(vertex.y > xMax) yMax = vertex.y;
			if(vertex.y < xMax) yMin = vertex.y;
		}
		return new Rectangle(new Vector(xMin, yMin), xMax - xMin, yMax - yMin);
	}
	
	/**
	 * @return number of vertices
	 */
	public int n() {
		return this.vertices.length;
	}
	
	/**
	 * @return the centroid of this polygon
	 */
	public Vector centroid() {
		float  scalar = 1 / 6 * this.area();
		Vector centroid  = Vector.ZERO;
		
		for(int v = 0; v < vertices.length; v++) {
			Vector v1 = vertices[v];
			Vector v2 = vertices[v + 1 < vertices.length ? v + 1 : 0];
			centroid = centroid.add(v1.add(v2).scale(v1.cross(v2)));
		}
		
		return centroid.scale(scalar);
	}
	
	/** 
	 * @param other
	 * @return the point on this polygon which is nearest to the given polygon
	 */
	public Vector nearestPoint(Vector other) {
		Vector min = vertices[0];
		for(Segment seg : segments()) {
			Vector proj = seg.projection(other);
			if(proj == null){
				continue;
			}
			if(other.distance(proj) < other.distance(min)) {
				min = proj;
			}
		}
		return min;
	}
	
	/**
	 * @param other
	 * @return unified polygon which consists of this polygon and the other polygon
	 */
	public Polygon merge(Polygon other) {
		List<Vector> newVertices = new ArrayList<Vector>();
		for(Vector vertex : this.vertices) {
			if(!other.contains(vertex)) {
				newVertices.add(vertex);
			}
		}
		for(Vector vertex : other.vertices) {
			if(!this.contains(vertex)) {
				newVertices.add(vertex);
			}
		}
		newVertices.addAll(Arrays.asList(this.intersections(other)));
		Collections.sort(newVertices);
		return new Polygon(newVertices.toArray(new Vector[0]));
		
	}
	
	/**
	 * @param other polygon
	 * @return true if the other polygon is congruent with this polygon (all segment lengths and angles match)
	 */
	public boolean isCongruent(Polygon other) {
		if(other.vertices.length != this.vertices.length) {
			return false;
		}
		theseSegments:
		for(Segment seg : this.segments()) {
			for(Segment otherSeg : other.segments()) {
				if(otherSeg.equals(seg)) {
					continue theseSegments;
				}
			}
			return false;
		}
	
		theseAngles:
		for(Angle ang : this.angles()) {
			for(Angle otherAng : other.angles()) {
				if(otherAng.equals(ang)) {
					continue theseAngles;
				}
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Polygon)) {
			return false;
		}
		Polygon poly = (Polygon) obj;
		for(int vtx = 0; vtx < this.vertices.length; vtx++) {
			if(!(vtx < poly.vertices.length && this.vertices[vtx].equals(poly.vertices[vtx]))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected Polygon clone() {
		return new Polygon(this.vertices.clone());
	}
	
	@Override
	public String toString() {
		String str = "[";
		for(Vector vtx : vertices) {
			str += vtx.toString();
			str += ", ";
		}
		return str + "]";
	}

}
