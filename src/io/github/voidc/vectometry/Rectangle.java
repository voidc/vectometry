package io.github.voidc.vectometry;

/**
 * vertex indexing:
 * <pre>
 * 3------2
 * |      |
 * 0------1
 * </pre>
 */
public class Rectangle extends Polygon {
	
	/**
	 * creates new rectangle with the given width, height and origin
	 * @param origin bottom left corner of the rectangle
	 * @param width
	 * @param height
	 */
	public Rectangle(Vector origin, float width, float height) {
		super(new Vector[]{
				origin,
				new Vector(origin.x + width, origin.y),
				new Vector(origin.x + width, origin.y + height),
				new Vector(origin.x, origin.y + height)
				});
	}
	
	/**
	 * @return origin of the rectangle (bottom left corner)
	 */
	public Vector origin() {
		return vertices[0];
	}
	
	/**
	 * @return the width of the rectangle
	 */
	public float width() {
		return vertices[1].x - vertices[0].x;
	}
	   
	/**
	 * @return the height of the rectangle
	 */
	public float height() {
		return vertices[3].y - vertices[0].y;
	}
	
	/**
	 * @param padding distance by which each side is moved (if negative the rectangle gets bigger)
	 * @return new rectangle with the specified padding to this polygon
	 */
	public Rectangle padding(float padding) {
		return new Rectangle(this.origin().add(new Vector(padding, padding)), this.width() - 2 * padding, this.height() - 2 * padding);
	}
	
	@Override
	public boolean contains(Vector point) {
		return point.x >= vertices[0].x && point.y >= vertices[0].y && point.x <= vertices[2].x && point.y <= vertices[2].y;
	}
	
	@Override
	public Rectangle bounds() {
		return this;
	}
	
	@Override
	public float area() {
		return this.height() * this.width();
	}
	
}
