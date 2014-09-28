package io.github.voidcatz.vectometry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.github.voidcatz.vectometry.util.Angle;

public class Polygon {
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
	 * @return the perimeter of this polygon
	 */
	public float perimeter() {
		float sum = 0;
		for(Segment seg : segments()) {
			sum += seg.length();
		}
		return sum;
	}
	
	/**
	 * @return the area of this polygon
	 */
	public float area() {
		float sum = 0;
		for(int v = 0; v < vertices.length; v++) {
			sum += vertices[v].cross( vertices[v + 1 < vertices.length ? v + 1 : 0]); //TODO: test
		}
		return Math.abs(sum/2);
	}
	
	/**
	 * @param line
	 * @return all intersects with the given line
	 */
	public Vector[] intersections(Line line) {
		List<Vector> intersects = new ArrayList<Vector>();
		for(Segment seg : segments()) {
			if(seg.intersect(line) != null) intersects.add(seg.intersect(line));
		}
		return intersects.toArray(new Vector[0]);
	}
	
	/**
	 * @param point
	 * @return true if this polygon contains the given point
	 */
	public boolean contains(Vector point) { // http://stackoverflow.com/questions/217578/point-in-polygon-aka-hit-test
		boolean c = false;
		for (int i = 0, j = this.vertices.length - 1; i < this.vertices.length; j = i++) {
			if (
					((this.vertices[i].y > point.y) != (this.vertices[j].y > point.y))
					&& (
							point.x < (this.vertices[j].x - this.vertices[i].x)
							* (point.y - this.vertices[i].y)
							/ (this.vertices[j].y - this.vertices[i].y)
							+ this.vertices[i].x
						)
				)
				c = !c;
		}
		return c;
	}
	
	/**
	 * @param scalar
	 * @param center
	 * @return scaled polygon
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
	 * @param tranformation vector by which each vertex is moved
	 * @return polygon which is moved by the transformation
	 */
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
	 * @return the bounding rectangle of this polygon
	 */
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
		List<Vector> newVertices = new LinkedList<Vector>();
		Polygon current = this;
		Vector lastVtx = this.vertices[this.vertices.length-1];
		Vector originalVtx = this.vertices[0];
		boolean first = true;
		
		for(int cv = 0; ; cv++) {
			if(cv >= current.vertices.length) {
				cv = 0;
			}
			Vector vtx = current.vertices[cv];
			if(vtx.equals(originalVtx) && !first) {
				break;
			}
			first = false;
			if(other.contains(vtx)) {
				Segment seg = new Segment(lastVtx, vtx);
				Vector is = null;
				int v;
				for(v = 0; v < other.vertices.length; v++) {
					is = other.segments()[v].intersect(seg);
					if(is != null) break;
				}
				newVertices.add(is);
				lastVtx = is;
				Polygon tmp = current;
				current = other;
				other = tmp;
				cv = v + 1;
			} else {
				newVertices.add(vtx);
				lastVtx = vtx;
			}
		}
		
		return new Polygon(newVertices.toArray(new Vector[0]));
		
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
