package io.github.voidcatz.vectometry;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import io.github.voidcatz.vectometry.util.Angle;
import io.github.voidcatz.vectometry.util.Matrix;

public class Vector {
	public final float x, y;
	
	public static final Vector ZERO = new Vector(0, 0);
	
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
		return this.operate(other, (a, b) -> a + b);
	}
	
	/**
	 * @param other vector which should be subtracted
	 * @return the difference between this vector and the other vector
	 */
	public Vector subtract(Vector other) {
		return this.operate(other, (a, b) -> a - b);
	}
	
	/**
	 * @return the inverted vector with (-x, -y)
	 */
	public Vector invert() {
		return new Vector(-this.x, -this.y);
	}
	
	/**
	 * @param other vector which should be multiplied
	 * @return the product of this vector and the other vector
	 */
	public Vector multiply(Vector other) {
		return this.operate(other, (a, b) -> a * b);
	}

	/**
	 * @param scalar factor by which the vector is scaled
	 * @return this vector multiplied by the scalar 
	 */
	public Vector scale(float scalar) {
		return this.operate(a -> a * scalar);
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
	
//	public Vector xprod(Vector other) {
//		return new Vector(this.x * other.y - this.y * other.x, this.x * other.x + this.y * other.y);
//	}
	
	/**
	 * @param angle by which the vector is rotated counter clockwise
	 * @return rotated vector
	 */
	public Vector rotate(Angle angle) {
		return new Vector(this.x * angle.cos() - this.y * angle.sin(), this.x * angle.sin() + this.y * angle.cos());
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
	
	/**
	 * @param operator
	 * @return manipulated vector
	 */
	public Vector operate(UnaryOperator<Float> operator) {
		return new Vector(operator.apply(this.x), operator.apply(this.y));
	}
	
	
	/**
	 * @param other
	 * @param operator
	 * @return manipulated vector
	 */
	public Vector operate(Vector other, BinaryOperator<Float> operator) {
		return new Vector(operator.apply(this.x, other.x), operator.apply(this.y, other.y));
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
		return String.format("(%f,%f)", this.x, this.y);
	}

}
