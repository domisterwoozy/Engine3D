package com.jacobschneider.engine.math;

/**
 * Immutable class representing a quaternion.
 * A unit quaternion is a fast and efficient way to represent an orientation in 3D space.
 * There is a one to one mapping between a unit quaternion in a rotation matrix (orthoganol matrix) and you can do this
 * mapping by calling {@link #toMatrix()}.
 * This class does not guarantee an object is a unit quaternion but you can turn any {@link Quaternion}
 * into a unit quaternion using {@link #normalize()}.
 * See (@link http://en.wikipedia.org/wiki/Quaternion) for more information.
 * 
 * 
 * @author Jacob
 *
 */
public class Quaternion {
	public static final Quaternion identity = new Quaternion(1, Vector3.zero);
	public static final Quaternion zero = new Quaternion(0, Vector3.zero);
	
	public final double s;
	public final Vector3 v;
	
	private Quaternion(double s, Vector3 v) {
		this.s = s;
		this.v = v;
	}
	
	/**
	 * An orientation in 3D space that is achieved by rotating
	 * about a direction by a certain angle.
	 * @param angle Amount to rotate in radians.
	 * @param dir Axis to rotate around.
	 * @return A quaternion representing this orientation
	 */
	public static Quaternion newQuaternion(double angle, Vector3 dir) {
		Vector3 v = dir.normalize().multScaler(Math.sin(angle / 2));
		Quaternion q = new Quaternion(Math.cos(angle / 2), v);
		return q;
	}
	
	/**
	 * A {@link Quaternion} that represents a vector. S always is zero and the v component is the vector.
	 * Useful for calculations and rotations
	 * @param v The vector you want to represent
	 * @return A virtual {@link Quaternion} representing a vector
	 */
	public static Quaternion vectorQ(Vector3 v) {
		return new Quaternion(0, v);
	}
	
	/**
	 * Creates an orientation {@link Quaternion} from the typical camera vectors.
	 * Assumes the y-axis is the direction the camera points in and the z-axis is up.
	 * @param centerPos Center position of the camera
	 * @param lookAtPos Position of focus of the camera. Y axis of camera points in this direction
	 * @param upDir Desired up (z axis) direction of the camera.
	 * @return The {@link Quaternion} that represents the cameras orientation in 3D space.
	 */
	public static Quaternion fromCameraVectors(Vector3 centerPos, Vector3 lookAtPos, Vector3 upDir) {
		// taking the world unit vectors and reorientating them
		// first - point the y unit vector in direction of camera pointing towards
		Vector3 pointingDir = lookAtPos.subtract(centerPos).normalize();
		Quaternion q1 = Vector3.j.rotationRequired(pointingDir);
		
		// second - attempt to match the cameras up dir (z dir) to the desired up
		// project the desired up vector to the cameras xz plane
		Vector3 targetUp = upDir.projectToPlane(pointingDir);
		Vector3 currentUp = Vector3.k.rotate(q1);
		Quaternion q2 = currentUp.rotationRequired(targetUp);
		
		return q2.multQuat(q1);
	}
	
	public Quaternion multScaler(double scaler) {
		return new Quaternion(this.s * scaler, this.v.multScaler(scaler));
	}
	
	public Quaternion multQuat(Quaternion rhs) {
		double newS = this.s * rhs.s - this.v.dot(rhs.v);
		Vector3 newV = this.v.cross(rhs.v).add(rhs.v.multScaler(this.s).add(this.v.multScaler(rhs.s)));
		return new Quaternion(newS, newV);
	}
	
	public Quaternion add(Quaternion rhs) {
		return new Quaternion(this.s + rhs.s, this.v.add(rhs.v));
	}

	public double mag() {
		return Math.sqrt(magSqr());
	} 
	
	public double magSqr() {
		return s*s+v.magSquared();
	}
	
	public Quaternion normalize() {
		double mag = mag();
		return new Quaternion(this.s / mag, this.v.multScaler(1 / mag));
	}
	
	public Matrix3 toMatrix() {		
		return new Matrix3(1 - 2*v.y*v.y - 2*v.z*v.z,
				2*v.x*v.y - 2*s*v.z,
				2*v.x*v.z + 2*s*v.y,
				2*v.x*v.y + 2*s*v.z,
				1 - 2*v.x*v.x - 2*v.z*v.z,
				2*v.y*v.z - 2*s*v.x,
				2*v.x*v.z - 2*s*v.y,
				2*v.y*v.z + 2*s*v.x,
				1 - 2*v.x*v.x - 2*v.y*v.y);
	}
	
	@Override
	public String toString() {
		return "S: " + this.s + " " + v.toString();
	}

	public Quaternion inverse() {
		return conjugate().multScaler(1 / this.magSqr());
	}
	
	public Quaternion conjugate() {
		return new Quaternion(this.s, this.v.inverse());
	}
	
	public Vector3 getVect() {
		return v;
	}

}
