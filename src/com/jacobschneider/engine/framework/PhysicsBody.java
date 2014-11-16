package com.jacobschneider.engine.framework;

import java.util.Set;

import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;

/**
 * This interface describes the physics of a body. This includes the number of degrees of freedom and how these
 * degrees of freedom respond to forces on the body.
 * 
 * @author Jacob
 *
 */
public interface PhysicsBody {
	/**
	 * An enum representing a possible axis of rotation.
	 * @author Jacob
	 *
	 */
	public static enum Axis {X_AXIS, Y_AXIS, Z_AXIS};
	// the setters
	/**
	 * Enacts an instantaneous impulse on this body. Collisions use this method to instantaneously change
	 * the momentums of this {@link PhysicsBody}.
	 * @param force Instantaneous force with units Force*Time
	 * @param relativePos Position of the impulse relative to the center of mass of this body
	 */
	public void enactImpulse(Vector3 force, Vector3 relativePos);

	/**
	 * Adds a permanent force and torque to this body
	 * @param force Force to be added
	 * @param torque Torque to be added
	 */
	public void addInputs(Vector3 force, Vector3 torque);
	
	/**
	 * Adds a temporary force and torque to this body for a certain duration.
	 * @param force force to be exerted
	 * @param torque torque to be exerted
	 * @param duration the amount of time the force/torque is exerted for
	 */
	public void thrustInputs(Vector3 force, Vector3 torque, float duration);
	
	/**
	 * Constraints the position of this body to a {@link Manifold}. The body must
	 * initially be on the manifold and the constraint will ensure that the velocity
	 * of this body is always tangent to the manifold.
	 * 
	 * @param constraint Manifold that acts as a constraint
	 */
	public void constrainBody(Manifold constraint);
	
	/**
	 * Updates the state of this body by one physics frame.
	 * @param deltaTime Time since last physics frame
	 */
	public void update(double deltaTime);
	/**
	 * Fixes the body completely in space.
	 * Gives the body infinite mass and infinite rotational inertia.
	 */
	public void fixBody();
	
	/**
	 * Fixes the rotation of a body in space.
	 * Gives the body infinite rotational inertia.
	 */
	public void fixRotation();
	
	/**
	 * Fixes the position of a body in space.
	 * Gives the body infinite mass.
	 */
	public void fixPosition();
	
	/**
	 * Fixes the rotation of a body in space around a set of axes. If an axis is fixed the body cannot
	 * rotate around that axis. These axes are the body coordinate basis.
	 * Supplying this method with a set of all 3 axis is identical 
	 * to calling {@link #fixRotation()}.
	 * @param axes The set of axis you want to fix rotation around.
	 */
	public void fixAxes(Set<Axis> axes);
	
	/**
	 * Prevents all rotation except around a certain arbitrary axis (body coordinates).
	 * The scaler inertia  of the axis is set to the scalerInertia parameter.
	 * @param axisBody an arbitrary direction in body space
	 * @param scalerInertia the scaler inertia value around axisBody
	 */
	public void fixRotationAroundAxis(Vector3 axisBody, double scalerInertia);
	
	// the rest are getters
	/**
	 * Determines if this body is completely fixed
	 * @return Whether the body is completely fixed
	 */
	public boolean isFixed();
	
	/**
	 * Determines if the body's rotation is fixed
	 * @return Whether the body's rotation is fixed
	 */
	public boolean isRotFixed();
	
	/**
	 * Determines if the body's position is fixed
	 * @return Whether the body's position is fixed
	 */
	public boolean isPosFixed();
	
	/**
	 * Determines whether the rotation around a specific
	 * axis is fixed.
	 * @param axis The axis.
	 * @return Whether the rotation is fixed.
	 */
	public boolean isAxisFixed(Axis axis);

	/**
	 * Returns one divided by the mass of the object.
	 * A body with infinite mass returns a value of zero.
	 * @return The multiplicative inverse of the mass.
	 */
	public double getInvMass();
	
	/**
	 * Returns the mass of the object. If the mass of the object
	 * is infinite ({@link #getInvMass()} is zero) then this value is Infinity.
	 * @return The mass of the object.
	 */
	public double getMass();

	/**
	 * The inertia tensor of this body. If this body is locked on any axis
	 * than this value if 
	 * @return 3x3 matrix representing the inertia tensor of this body.
	 */
	public Matrix3 getI();

	/**
	 * Returns the inverse of the moment of inertia tensor. This
	 * value is always valid even when some axes of rotation are locked.
	 * @return 3x3 matrix representing the inverse of the inertia tensor.
	 */
	public Matrix3 getInvI();

	public Vector3 getX();

	public Quaternion getQ();

	public Matrix3 getR();

	public Vector3 getP();

	/**
	 * Calculates and returns the total kinetic energy of the {@link PhysicsBody}. 
	 * This includes the translational and rotational energy.
	 * @return - total kinetic energy
	 */
	public abstract double getEnergy();

	/**
	 * Get absolute velocity (world coordinates) of a point on the bodies surface.
	 * @param rp vector from this body to point
	 * @return Velocity of the point
	 */
	public Vector3 getSurfaceVelocity(Vector3 rp);
	

	public Vector3 getVel();

	public Vector3 getOmega();
	

	/**
	 * Transforms a point in world space to a point in this bodies space.
	 * 
	 * @param pW A point in world space
	 * @return The point transformed into body space coordinates
	 */
	public Vector3 toBodySpace(Vector3 pW);

	/**
	 * Transforms a point in this bodies space to world space
	 * 
	 * @param pB A point in body space coordinates.
	 * @return The point transformed into world space coordinates
	 */
	public Vector3 toWorldSpace(Vector3 pB);

	/**
	 * Converts a segment from world space to this bodies space
	 * 
	 * @param s A segment in world space coordinates
	 * @return The point transformed into body space coordinates
	 */
	public Segment toBodySpace(Segment s);

	/**
	 * Converts a segment from this bodies space to world space
	 * 
	 * @param s A segment in body space coordinates
	 * @return The segment transformed into world space coordinates.
	 */
	public Segment toWorldSpace(Segment s);

}