package io.github.voidcatz.vectometry;

import io.github.voidcatz.vectometry.util.Angle;
import io.github.voidcatz.vectometry.util.Matrix;

public class Vector implements Comparable<Vector> {
	public final float x, y;
	
	public static final Vector ZERO = new Vector(0, 0);
	public static final Vector RIGHT = new Vector(1, 0);
	public static final Vector UP = new Vector(0, 1);
	public static final Vector LEFT = new Vector(-1, 0);
	public static final Vector DOWN = new Vector(0, -0);
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @param angle of the vector
	 * @param length of the vector
	 */
	public Vector(Angle angle, float length) {
		this(angle.cos()*length, angle.sin()*length);
	}
	
	/**
	 * @param other vector which should be added
	 * @return the sum of this vector and the other vector
	 */
	public Vector add(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y);
	}
	
	/**
	 * @param other vector which should be subtracted
	 * @return the difference between this vector and the other vector
	 */
	public Vector subtract(Vector other) {
		return new Vector(this.x - other.x, this.y - other.y);
	}
	
	/**
	 * @return the inverted vector with (-x, -y)
	 */
	public Vector invert() {
		return new Vector(-this.x, -this.y);
	}
	
	/**
	 * @return vector with floored coordinates
	 */
	public Vector floor() {
		return new Vector((float) Math.floor(this.x), (float) Math.floor(this.y));
	}
	
	/**
	 * @param other vector which should be multiplied
	 * @return the product of this vector and the other vector
	 */
	public Vector multiply(Vector other) {
		return new Vector(this.x * other.x, this.y * other.y);
	}

	/**
	 * @param scalar factor by which the vector is scaled
	 * @return this vector multiplied by the scalar 
	 */
	public Vector scale(float scalar) {
		return new Vector(this.x * scalar, this.y * scalar);
	}
	
	/**
	 * @param other vector by which this vector is scalar multiplied
	 * @return the dot product of this vector and the other vector
	 */
	public float dot(Vector other) {
		return this.x * other.x + this.y * other.y;
	}
	
	/**
	 * @param other vector by which this vector is cross multiplied
	 * @return the cross product of this vector and the other vector
	 */
	public float cross(Vector other) {
		return this.x * other.y - this.y * other.x;
	}
	
	/**
	 * @param other vector
	 * @return new vector where x is the cross product and y the dot product of this vector and the other vector
	 */
	public Vector crossdot(Vector other) {
		return new Vector(this.cross(other), this.dot(other));
	}
	
	/**
	 * @param other vector which this vector should be projected on
	 * @return the orthogonal projection of this vector on the other vector
	 */
	public Vector project(Vector other) {
		return other.scale(this.dot(other) / other.dot(other));
	}
	
	/**
	 * @param center vector by which this vector should be reflected
	 * @return the reflected vector
	 */
	public Vector reflect(Vector center) {
		return center.add(center.subtract(this));
	}
	
	/**
	 * @return the slope of this vector
	 */
	public float slope() {
		return y / x;
	}
	
	/**
	 * @return number of the quadrant containing the vector (1 to 4) or 0 if either x or y equal 0
	 */
	public int quadrant() {
		if(this.x > 0 && this.y > 0) {
			return 1;
		} else if(this.x < 0 && this.y > 0) {
			return 2;
		} else if(this.x < 0 && this.y < 0) {
			return 3;
		} else if(this.x > 0 && this.y < 0) {
			return 4;
		} else { //x == 0 || y == 0
			return 0;
		}
	}
	
	/**
	 * @return the length of this vector
	 */
	public float length() {
		return (float) Math.sqrt(this.dot(this));
	}
	
	/**
	 * @param length of the resized vector
	 * @return vector with same direction and given length
	 */
	public Vector resize(float length) {
		return this.scale(length/this.length());
	}
	
	/**
	 * @see #resize(float)
	 */
	public Vector resize(float distance, Vector origin) {
		Vector difference = this.subtract(origin);
		return origin.add(difference.resize(distance));
	}
	
	
	/**
	 * @return angle of this vector
	 */
	public Angle angle() {
		return Angle.rad((float) Math.atan2(this.x, this.y));
	}
	
	/**
	 * @param other vector
	 * @return angle between this vector and the other vector
	 */
	public Angle angle(Vector other) {
		return Angle.rad((float) Math.acos(this.dot(other) / (this.length() * other.length())));
	}
	
	/**
	 * @param angle by which the vector is rotated counter clockwise
	 * @return rotated vector
	 */
	public Vector rotate(Angle angle) {
		Vector rotation = new Vector(angle.sin(), angle.cos());
		return new Vector(this.cross(rotation), this.dot(rotation));
	}
	
	/**
	 * @param angle by which the vector is rotated counter clockwise
	 * @param center point around which this vector is rotated
	 * @return rotated vector
	 * @see #rotate(Angle)
	 */
	public Vector rotate(Angle angle, Vector center) {
		Vector difference = this.subtract(center);
		return center.add(difference.rotate(angle));
	}
	
	/**
	 * @param other vector
	 * @return true if this vector is parallel to the other vector
	 */
	public boolean isParallel(Vector other) {
		return this.cross(other) == 0;
	}
	
	/**
	 * @param other vector
	 * @return true if this vector is orthogonal to the other vector
	 */
	public boolean isOrthogonal(Vector other) {
		return this.dot(other) == 0;
	}
	
	/**
	 * @param other vector
	 * @return the distance between this vector and the other vector
	 */
	public float distance(Vector other) {
		return other.subtract(this).length();
	}
	
	/**
	 * @param other vector
	 * @return the midpoint between this vector and the other vector
	 */
	public Vector midpoint(Vector other) {
		Vector difference = other.subtract(this);
		return this.add(difference.scale(0.5f));
	}
	
	/**
	 * @return the x and y coordinate as array
	 */
	public float[] values() {
		return new float[] {x, y};
	}
	
	/**
	 * @param matrix by which this vector should be transformed
	 * @return transformed vector
	 * @see Matrix#multiply(Matrix)
	 */
	public Vector matrixTransform(Matrix matrix) {
		float[] result = matrix.multiply(new Matrix(1, 2, this.values())).values();
		return new Vector(result[0], result[1]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Vector)) return false;
		Vector vec = (Vector) obj;
		return vec.x == this.x && vec.y == this.y;
	}
	
	@Override
	public Vector clone() {
		return new Vector(this.x, this.y);
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	@Override
	public int compareTo(Vector other) { //not working as intended: should work for every quadrant and weight length more
		return (int) (this.angle().deg() * this.length() - other.angle().deg() * other.length());
	}

}
