package io.github.voidcatz.vectometry;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import io.github.voidcatz.vectometry.util.Angle;

public class Vector {
	public final float x, y;
	
	public static final Vector ZERO = new Vector(0, 0);
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Angle angle, float length) {
		this(angle.cos()*length, angle.sin()*length);
	}
	
	
	public Vector add(Vector other) {
		return this.operate(other, (a, b) -> a + b);
	}
	
	public Vector substract(Vector vec) {
		return this.operate(vec, (a, b) -> a - b);
	}
	
	public Vector multiply(Vector vec) {
		return this.operate(vec, (a, b) -> a * b);
	}

	public Vector scale(float scalar) {
		return this.operate(a -> a * scalar);
	}
	
	public float dot(Vector vec) {
		return this.x * vec.x + this.y * vec.y;
	}
	
	public float cross(Vector vec) {
		return this.x * vec.y - this.y * vec.x;
	}
	
	public Vector project(Vector vec) {
		return this.scale(this.dot(vec) / vec.dot(vec));
	}
	
	public float slope() {
		return y / x;
	}
	
	public float length() {
		return (float) Math.sqrt(this.dot(this));
	}
	
	public Vector resize(float length) {
		return this.scale(length/this.length());
	}
	
	public Angle angle() {
		return Angle.rad((float) Math.atan2(this.x, this.y));
	}
	
	public Angle angle(Vector vec) {
		return Angle.rad((float) Math.acos(this.dot(vec) / (this.length() * vec.length())));
	}
	
	public Vector rotate(Angle ang) {
		return new Vector(this.x * ang.cos() - this.y * ang.sin(), this.x * ang.sin() + this.y * ang.cos());
	}
	
	public Vector rotate(Angle angle, Vector center) {
		Vector rotVec = this.substract(center);
		rotVec.rotate(angle);
		return rotVec.add(center);
	}
	
	public boolean isParallel(Vector vec) {
		return this.cross(vec) == 0;
	}
	
	public boolean isOrthogonal(Vector vec) {
		return this.dot(vec) == 0;
	}
	
	public float distance(Vector vec) {
		return vec.substract(this).length();
	}
	
	public Vector midpoint(Vector vec) {
		Vector difference = vec.substract(this);
		return this.add(difference.scale(0.5f));
	}
	
//	public Vec2D matrixTransform(Matrix mat) throws DimensionExeption {
//		return mat.multiply(this.toMatrix()).toVec2D();
//	}
	
	public Vector operate(UnaryOperator<Float> operator) {
		return new Vector(operator.apply(this.x), operator.apply(this.y));
	}
	
	public Vector operate(Vector vec, BinaryOperator<Float> operator) {
		return new Vector(operator.apply(this.x, vec.x), operator.apply(this.y, vec.y));
	}
	
//	public Matrix toMatrix() {
//		return new Matrix(new float[][]{{x, y}});	
//	}
	
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
