package io.github.voidcatz.vectometry;

public interface IShape {
	
	/**
	 * @return the area of this shape
	 */
	public float area();
	
	/**
	 * @return the perimeter of this shape
	 */
	public float perimeter();
	
	/**
	 * @param vector
	 * @return true if this shape contains the given vector
	 */
	public boolean contains(Vector vector);
	
	/**
	 * @param transformation vector
	 * @return move this shape by the given vector
	 */
	public IShape move(Vector transformation);
	
	/**
	 * @return the bounding rectangle of this shape
	 */
	public Rectangle bounds();
	
	/**
	 * @return the centroid of this shape
	 */
	public Vector centroid();

}
