package com.jacobschneider.engine.math;

/**
 * Immutable 3 dimensional cartesian vector class
 * @author Jacob
 *
 */
public final class Vector3 {
	private static final double DOUBLE_EQUALITY_THRESHOLD = 0.0000000001;
	/**
	 * The zero vector. All elements are zero.
	 */
	public final static Vector3 zero = new Vector3(0,0,0);
	/**
	 * The unit vector in the positive x direction.
	 */
	public final static Vector3 i = new Vector3(1,0,0);
	/**
	 * The unit vector in the positive y direction.
	 */
	public final static Vector3 j = new Vector3(0,1,0);
	/**
	 * The unit vector in the positive z direction.
	 */
	public final static Vector3 k = new Vector3(0,0,1);
	
	public final double x;
	public final double y;
	public final double z;
	
	/**
	 * Creates a 3D vector from three scalar components.
	 * @param x X component
	 * @param y Y component
	 * @param z Z component
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a 3D vector from a length 3 array of double.
	 * @param v Array of length 3 representing a vector. Throws a {@link IllegalArgumentException} if an array is passed with an incorrect size.
	 */
	public Vector3(double[] v) {
		if (v.length != 3) {
			throw new IllegalArgumentException("Array must have length of 3");
		}
		this.x = v[0];
		this.y = v[1];
		this.z = v[2];
	}
	
	/**
	 * Performs vector addition between this {@link Vector3} and another {@link Vector3}
	 * @param other Other {@link Vector3} to add
	 * @return A new {@link Vector3} representing the sum.
	 */
	public Vector3 add(Vector3 other) {
		return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Vector3 subtract(Vector3 rhs) {
		return new Vector3(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z);
	}
	
	public Vector3 multScaler(double scaler) {
		return new Vector3(this.x * scaler, this.y * scaler, this.z * scaler);
	}
	
	/**
	 * Returns the additive inverse of this vector. 
	 * @return The additive inverse of this vector.
	 */
	public Vector3 inverse() {
		return multScaler(-1);
	}
	
	/**
	 * Calculates the cross product of this vector and another vector where this vector
	 * is on the left hand side of the operator and the parameter rhs is on the right hand side.
	 * @param rhs The vector on the right hand side of the cross product operator.
	 * @return The cross product between this vector and another vector.
	 */
	public Vector3 cross(Vector3 rhs) {
		return new Vector3(this.y*rhs.z - this.z*rhs.y, this.z*rhs.x - this.x*rhs.z, this.x*rhs.y - this.y*rhs.x);
	}
	
	/**
	 * Returns a vector that represents the projection of this vector onto a plane.	 * 
	 * @param planeNormal The normal of the plane
	 * @return The resulting vector after projection has occurred.
	 */
	public Vector3 projectToPlane(Vector3 planeNormal) {
		planeNormal = planeNormal.normalize();
		return this.subtract(planeNormal.multScaler(this.dot(planeNormal)));
	}
	
	/**
	 * Projects a vector onto another vector. The resulting vector will have the same direction
	 * as the vector in the parameter toVector.
	 * @param toVector The vector on which to project to.
	 * @return The resulting vector after projection has occurred.
	 */
	public Vector3 projectToVector(Vector3 toVector) {
		return toVector.normalize().multScaler(this.dot(toVector) / toVector.magSquared());
	}	
	
	public Vector3 multMatrixLeft(Matrix3 lhs) {
		return new Vector3(this.x * lhs.xx + this.y * lhs.xy + this.z * lhs.xz,
						   this.x * lhs.yx + this.y * lhs.yy + this.z * lhs.yz,
						   this.x * lhs.zx + this.y * lhs.zy + this.z * lhs.zz);		
	}
	
	/**
	 * Rotates this vector by a rotation matrix. The rotation matrix passed into this
	 * method must be orthogonal (the inverse matrix must equal the transverse matrix).
	 * @param rotMatrix The orthogonal matrix to rotate by.
	 * @return The resulting vector after the rotation has occurred.
	 */
	public Vector3 rotate(Matrix3 rotMatrix) {
		if (!rotMatrix.isOrthoganol()) {
			throw new IllegalArgumentException("rotMatrix must be orthogonal");
		}
		return this.multMatrixLeft(rotMatrix);
	}
	
	/**
	 * Rotate a vector by unit quaternion. The quaternion passed into this method must 
	 * have a magnitude equal to one (normalized).
	 * @param rotQ The unit quaternion to rotate by.
	 * @return The resulting quaternion after rotation.
	 */
	public Vector3 rotate(Quaternion rotQ) {
		if (Math.abs(rotQ.mag()) < DOUBLE_EQUALITY_THRESHOLD) {
			return this; // no change
		} else if (Math.abs(rotQ.mag() - 1.0) > DOUBLE_EQUALITY_THRESHOLD) {
			throw new IllegalArgumentException("rotQ must be a unit quaternion. The magnitude of rotQ is: " + rotQ.mag());
		} 
		return rotQ.multQuat(Quaternion.vectorQ(this)).multQuat(rotQ.inverse()).getVect();
	}
	
	public Vector3 multMatrixRight(Matrix3 rhs) {
		throw new IllegalStateException("Unimplemented");	
	}
	
	public Vector3 normalize() {
		double mag = mag();
		if (mag == 0) {
			return Vector3.zero;
		} else {
			return new Vector3(this.x / mag, this.y / mag, this.z / mag);
		}
	}
	
	public double dot(Vector3 other) {
		return (this.x * other.x + this.y * other.y + this.z * other.z);
	}
	
	public double mag() {
		return Math.sqrt(magSquared());
	}
	
	public double magSquared() {
		return (x*x + y*y + z*z);
	}
	
	/**
	 * Calculates the angle between this vector and another vector
	 * @param other The other vector
	 * @return An angle in radians
	 */
	public double angleBetween(Vector3 other) {
		double x = this.dot(other) / (this.mag() * other.mag());
		// fix floating point errors
		x = Math.min(1.0, x);
		x = Math.max(-1.0, x);
		return Math.acos(x);
	}
	
	/**
	 * Calculates the rotation required to orient this vector
	 * in the direction of other vector.
	 * @param other The direction you want this vector to be pointed in
	 * @return The {@link Quaternion} required to rotate this vector
	 */
	public Quaternion rotationRequired(Vector3 other) {
		double angleDiff = this.angleBetween(other);
		Vector3 rotAxis = this.cross(other); // axis to rotate around to get there
		return Quaternion.newQuaternion(angleDiff, rotAxis);
	}
	
	/**
	 * Generates a random unit vector that is perpendicular
	 * to this vector. If this vector equals {@link #zero}
	 * then it returns {@link zero}.
	 * @return a vector perpendicular to this vector
	 */
	public Vector3 randomPerp() {
		if (this.equals(Vector3.zero)) {
			return Vector3.zero;
		} else if (this.equals(Vector3.k)){
			return this.cross(Vector3.i).normalize();			
		} else {
			return this.cross(Vector3.k).normalize();
		}		
	}
	
	public double distBetween(Vector3 other) {
		return other.subtract(this).mag();
	}
	
	public double[] toArray() {
		return new double[] {this.x, this.y, this.z};
	}
	
	@Override
	public String toString() {
		return "X: " + this.x + ", Y: " + this.y + ", Z: " + this.z;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector3)) {
			return false;
		}
		Vector3 otherVect = (Vector3) o;
		if (otherVect == this) {
			return true;
		}
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(otherVect.x)) {
		return false;
		}
		if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(otherVect.y)) {
			return false;
		}
		if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(otherVect.z)) {
			return false;
		}
		return true;		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (int)Double.doubleToLongBits(this.x);
		result = 31 * result + (int)Double.doubleToLongBits(this.y);
		result = 31 * result + (int)Double.doubleToLongBits(this.z);
		return result;
	}
	

}
