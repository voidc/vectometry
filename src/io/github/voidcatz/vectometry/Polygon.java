package io.github.voidcatz.vectometry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.voidcatz.vectometry.util.Angle;

public class Polygon {
	public final Vector[] vertices;
	
	public Polygon(Vector[] vertices) {
		if(vertices.length < 3) throw new InvalidParameterException("A Polygon must at least have 3 vertices");
		this.vertices = vertices;
	}
	
	public Segment[] getSegments() {
		Segment[] segments = new Segment[vertices.length];
		for(int v = 0; v < vertices.length; v++) {
			segments[v] = new Segment(vertices[v], vertices[v + 1 < vertices.length ? v + 1 : 0]);
		}
		return segments;
	}
	
	public float perimeter() {
		float sum = 0;
		for(Segment seg : getSegments()) {
			sum += seg.length();
		}
		return sum;
	}
	
	public float area() {
		float sum = 0;
		for(int v = 0; v < vertices.length; v++) {
			sum += vertices[v].cross( vertices[v + 1 < vertices.length ? v + 1 : 0]); //TODO: test
		}
		return Math.abs(sum/2);
	}
	
	public Vector[] intersect(Line line) {
		List<Vector> intersects = new ArrayList<Vector>();
		for(Segment seg : getSegments()) {
			if(seg.intersect(line) != null) intersects.add(seg.intersect(line));
		}
		return intersects.toArray(new Vector[0]);
	}
	
	public boolean contains(Vector vec) { // http://stackoverflow.com/questions/217578/point-in-polygon-aka-hit-test
		boolean c = false;
		for (int i = 0, j = this.vertices.length - 1; i < this.vertices.length; j = i++) {
			if (((this.vertices[i].y > vec.y) != (this.vertices[j].y > vec.y))
					&& (vec.x < (this.vertices[j].x - this.vertices[i].x)
							* (vec.y - this.vertices[i].y)
							/ (this.vertices[j].y - this.vertices[i].y)
							+ this.vertices[i].x))
				c = !c;
		}
		return c;
	}
	
	public Polygon scale(float scalar, Vector center) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			Vector difference = this.vertices[v].substract(center);
			vtc[v] = center.add(difference.scale(scalar));
		}
		return new Polygon(vtc);
	}
	
	public Polygon move(Vector tranformation) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			vtc[v] = this.vertices[v].add(tranformation);
		}
		return new Polygon(vtc);
	}
	
	public Polygon rotate(Angle angle, Vector center) {
		Vector[] vtc = new Vector[this.vertices.length];
		for(int v = 0; v < this.vertices.length; v++) {
			vtc[v] = this.vertices[v].rotate(angle, center);
		}
		return new Polygon(vtc);
	}
	
	public Rectangle getBounds() {
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
	
	public static Polygon regularPolygon(Segment side, int vertices) {
		if(vertices < 3) throw new InvalidParameterException("A Polygon must at least have 3 vertices");
		Angle angle = Angle.deg((vertices - 1) * 180 / vertices);
		Vector[] vtc = new Vector[vertices];
		vtc[0] = side.point1;
		vtc[1] = side.point2;
		for(int v = 2; v < vertices; v++) {
			vtc[v] = vtc[v-2].substract(vtc[v-1]).rotate(angle, vtc[v-1]);
		}
		return new Polygon(vtc);
	}

}
