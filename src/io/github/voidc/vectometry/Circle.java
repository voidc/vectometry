package io.github.voidc.vectometry;

public class Circle implements IShape {
	public final Vector center;
	public final float radius;

	/**
	 * @param center of the circle
	 * @param radius of the circle
	 * creates new circle with the given center and radius
	 */
	public Circle(Vector center, float radius) {
		this.center = center;
		this.radius = radius;
	}
	
	@Override
	public float perimeter() {
		return (float) (2 * this.radius * Math.PI);
	}
	
	@Override
	public float area() {
		return (float) (Math.pow(radius, 2) * Math.PI);
	}
	
	private Vector[] intersections(Line line) { //segment handling??
        return null; //TODO: implement
    }

    private Vector[] intersections(Polygon poly) {
        return null; //TODO: implement
    }
	
	private Vector[] intersections(Circle other) {
        if(this.center.equals(other.center)) {  //the two circles are parallel
            return new Vector[0];
        }
		float distance = this.center.distance(other.center);
        if(distance > this.radius + other.radius) {  //the circles don't intersect because they are too far away from each other
            return new Vector[0];
        } else if(distance == this.radius + other.radius) { //the circles touch in one point
            return null; //TODO: implement
        } else { //there are two intersections
            return null; //TODO: implement
        }
	}
	
	@Override
	public boolean contains(Vector vector) {
		float distance = vector.distance(this.center);
		return distance <= this.radius && distance >= 0;
	}
	
	/**
	 * @param scalar factor by which the circle is scaled
	 * @return new sector with the same center and scaled radius
	 */
	public Circle scale(float scalar) {
		return new Circle(this.center, this.radius * scalar);
	}

	@Override
	public Circle move(Vector transformation) {
		return new Circle(this.center.add(transformation), this.radius);
	}

	@Override
	public Rectangle bounds() {
		return new Rectangle(this.center.subtract(new Vector(this.radius, this.radius)), 2 * this.radius, 2 * this.radius);
	}

	@Override
	public Vector centroid() {
		return this. center;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Circle)) {
			return false;
		}
		Circle other = (Circle) obj;
		return other.center.equals(this.center) && other.radius == this.radius;
	}

}
