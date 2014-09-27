package io.github.voidcatz.vectometry.util;

public class Angle {
	private final double angle;
	
	public enum AngleUnit{RADIANS, DEGREES}
	
	/**
	 * creates new angle with the given unit
	 * @param angle
	 * @param unit
	 */
	public Angle(double angle, AngleUnit unit) {
		switch(unit) {
		case RADIANS:
			this.angle = Math.toRadians(angle);
			break;
		case DEGREES:
			this.angle = angle;
			break;
		default:
			this.angle = angle;
			break;
		}
	}
	
	/**
	 * @param unit
	 * @return number for of the angle in the given unit
	 */
	public double getAngle(AngleUnit unit) {
		switch(unit) {
		case RADIANS:
			return Math.toRadians(angle);
		case DEGREES:
			return this.angle;
		}
		return angle;
	}
	
	
	/**
	 * @return angle in radians
	 */
	public double rad() {
		return getAngle(AngleUnit.RADIANS);
	}
	
	/**
	 * @return angle in degrees
	 */
	public double deg() {
		return getAngle(AngleUnit.DEGREES);
	}
	
	/**
	 * creates new angle with the given angle
	 * @param angle in radians
	 * @return new angle
	 */
	public static Angle rad(double angle) {
		return new Angle(angle, AngleUnit.RADIANS);
	}
	
	/**
	 * creates new angle with the given angle
	 * @param angle in degrees
	 * @return new angle
	 */
	public static Angle deg(double angle) {
		return new Angle(angle, AngleUnit.DEGREES);
	}
	
	
	/**
	 * @return the sine of the angle
	 */
	public float sin() {
		return (float) Math.sin(this.rad());
	}
	
	/**
	 * @return the cosine of the angle
	 */
	public float cos() {
		return (float) Math.cos(this.rad());
	}
	
	/**
	 * @return the tangent of the angle
	 */
	public float tan() {
		return (float) Math.tan(this.rad());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Angle)) return false;
		Angle alpha = (Angle) obj;
		return alpha.angle == this.angle;
	}
	
	@Override
	public String toString() {
		return this.angle + "°";
	}

}
