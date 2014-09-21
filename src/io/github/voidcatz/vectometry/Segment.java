package io.github.voidcatz.vectometry;

public class Segment extends Line {

	public Segment(Vector point1, Vector point2) {
		super(point1, point2);
	}
	
	public float length() {
		return this.vector().length();
	}
	
	@Override
	public boolean contains(Vector vec) {
		return super.contains(vec) && vec.distance(point1) <= this.length();
	}
	
	@Override
	public Vector intersect(Line other) {
		Vector intersect = super.intersect(other);
		return this.contains(intersect) ? intersect : null;
	}

}
