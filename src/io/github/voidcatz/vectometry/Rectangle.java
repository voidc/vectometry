package io.github.voidcatz.vectometry;

public class Rectangle extends Polygon {
	
	public Rectangle(Vector origin, float width, float height) {
		super(new Vector[]{
				origin,
				new Vector(origin.x, origin.y + height),
				new Vector(origin.x + width, origin.y + height),
				new Vector(origin.x + width, origin.y)
				});
	}
}
