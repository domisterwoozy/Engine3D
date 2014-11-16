package com.jacobschneider.engine.physics;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.PhysicsBody;
import com.jacobschneider.engine.framework.Segment;
import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;

/**
 * A perfect rigid body physics object with a full 6 degrees of freedom. 
 * 
 * @author Jacob
 *
 */
public class RigidBody6DOF implements PhysicsBody {
	private static final double DOUBLE_EQUALITY_TOLERANCE = Math.pow(1, -10);
	
	// "constant" quantities
	// not final because this implementation allows a body to become fixed at any time
	private double invMass; 
	private Matrix3 inertiaBodyInv;
	
	// state variables
	private Vector3 x = Vector3.zero; // position vector
	private Quaternion q = Quaternion.identity;
	private Vector3 P = Vector3.zero; // linear momentum vector
	private Vector3 L = new Vector3(1,0,0); // angular momentum vector	
	
	// auxiliary variables
	private Matrix3 R = Matrix3.IDENTITY; // orientation matrix
	private Matrix3 transR = Matrix3.IDENTITY;
	private Matrix3 invI = Matrix3.IDENTITY; // inverse inertia tensor in world frame
	private Vector3 v = Vector3.zero;  // velocity vector
	private Vector3 omega = Vector3.zero; // angular velocity vector
	private Quaternion omegaQ = Quaternion.zero;
	
	// inputs
	private Vector3 force = Vector3.zero;
	private Vector3 torque = Vector3.zero;
	private Vector3 tempForce = Vector3.zero; // only applied if current time - tempStart is less than currTempDuration
	private Vector3 tempTorque = Vector3.zero; // only applied if current time - tempStart is less than currTempDuration
	private long tempStart = 0;
	private float currTempDuration = 0;
	
	// artificial constraints
	private Manifold constraint = null;

	
	/**
	 * 
	 * @param mass
	 * @param inertiaBody
	 * @param x
	 * @param q0
	 * @param v
	 * @param omegaBody
	 */
	RigidBody6DOF(double mass, final Matrix3 inertiaBody, Vector3 x, Quaternion q0, Vector3 v, Vector3 omegaBody) {
		// constant properties
		this.invMass = 1 / mass;
		this.inertiaBodyInv = inertiaBody.invert();
		
		// state/aux vars
		this.x = x;
		this.P = v.multScaler(mass);
		this.q = q0;
		this.R = this.q.toMatrix();
		this.omega = omegaBody.rotate(this.R);
		updateInvI();
		this.L = omega.multMatrixLeft(this.invI.invert());		
		updateAux();		
	}	

	@Override
	public final void fixBody() {
		fixRotation();
		fixPosition();		
	}
	
	@Override
	public final void constrainBody(Manifold constraint) {
		this.constraint = constraint;	
		double distToConstraint = x.subtract(constraint.mapToManifold(x)).mag();
		if (distToConstraint > DOUBLE_EQUALITY_TOLERANCE) {
			throw new IllegalArgumentException("When a constraint is added, the body must be already on the constraint. The distance between this object" +
					"and the constraint is: " + distToConstraint);
		}
	}
	
	@Override
	public final void fixPosition() {
		this.invMass = 0;
	}
	
	@Override
	public final void fixRotation() {
		this.inertiaBodyInv = Matrix3.ZERO;
		updateInvI();
	}
	
	@Override
	public final void fixAxes(Set<Axis> axes) {
		if (axes.isEmpty()) {
			return;
		}
		List<Vector3> rowVects = inertiaBodyInv.toRowVectors();
		if (axes.contains(Axis.X_AXIS)) {
			rowVects.set(0, Vector3.zero);
		}
		if (axes.contains(Axis.Y_AXIS)) {
			rowVects.set(1, Vector3.zero);
		}
		if (axes.contains(Axis.Z_AXIS)) {
			rowVects.set(2, Vector3.zero);
		}
		this.inertiaBodyInv = Matrix3.fromRowVectors(rowVects.get(0), rowVects.get(1), rowVects.get(2));
		updateInvI();
	}
	
	@Override
	public void fixRotationAroundAxis(Vector3 axisBody, double scalerInertia) {
		this.inertiaBodyInv = Matrix3.IDENTITY.multScaler(scalerInertia); // reset to identity
		fixAxes(EnumSet.of(Axis.X_AXIS, Axis.Y_AXIS)); // lock everything but Z axis
		Quaternion rot = Vector3.k.rotationRequired(axisBody); // rotation required to point z axis in direction of new fixed axis
		this.inertiaBodyInv = inertiaBodyInv.rotate(rot);
		updateInvI();
		// no matter what you multiply invI by the result will always be proportional to axisBody
		// this means that no matter what angular momentum you put on the body the angular velocity
		// will always be pointing in the direction of axisBody
		// this effectively locks it to an axis
	}	
	
	@Override
	public final boolean isAxisFixed(Axis axis) {
		List<Vector3> rowVects = inertiaBodyInv.toRowVectors();
		if (axis == Axis.X_AXIS) {
			return rowVects.get(0).equals(Vector3.zero);
		}
		if (axis == Axis.Y_AXIS) {
			return rowVects.get(1).equals(Vector3.zero);
		}
		if (axis == Axis.Z_AXIS) {
			return rowVects.get(2).equals(Vector3.zero);
		}
		throw new IllegalStateException("Axis must be either X,Y, or Z");
	}
	

	@Override
	public final boolean isFixed() {
		return (isPosFixed() && isRotFixed());
	}
	
	@Override
	public final boolean isPosFixed() {
		return invMass == 0;
	}
	
	@Override
	public final boolean isRotFixed() {
		return inertiaBodyInv.equals(Matrix3.ZERO); 
	}
	
	@Override
	public final double getInvMass() {
		return invMass;
	}
	
	@Override
	public final double getMass() {
		return 1 / invMass;
	}
	
	@Override
	public final Matrix3 getI() {
		if (isRotFixed()) {
			return Matrix3.INFINITY;
		} else {
			return invI.invert();
		}		
	}
	
	@Override
	public final Matrix3 getInvI() {
		return invI;
	}
	
	@Override
	public final Vector3 getX() {
		return x;
	}
	
	@Override
	public final Quaternion getQ() {
		return q;
	}
	
	@Override
	public final Matrix3 getR() {
		return R;
	}
	
	@Override
	public final double getEnergy() {
		Vector3 n = omega.normalize(); // direction of angular velocity
		double scalerInertia = scalerInertia(n);
		double rotEnergy = scalerInertia == Double.POSITIVE_INFINITY ? 0 : 0.5 * scalerInertia * omega.mag();
		double transEnergy = isFixed() ? 0 : 0.5 * (1 / invMass) * v.mag();
		return rotEnergy + transEnergy;
	}
	
	public final double scalerInertia(Vector3 dir) {
		if (!isRotFixed()) {
			return dir.dot(dir.multMatrixLeft(invI.invert()));
		} else {
			return Double.POSITIVE_INFINITY;
		}
		
	}
	
	@Override
	public final Vector3 getSurfaceVelocity(Vector3 rp) {
		return v.add(omega.cross(rp));
	}	
	
	@Override
	public final Vector3 getVel() {
		return v;
	}
	
	@Override
	public final Vector3 getOmega() {
		return omega;
	}
	
	private final boolean isThrusting() {
		return (System.nanoTime() - this.tempStart) < (this.currTempDuration * 1000000000f);
	}
	
	@Override
	public void addInputs(Vector3 force, Vector3 torque) {
		this.force = this.force.add(force);
		this.torque = this.torque.add(torque);
	}
	
	@Override
	public void enactImpulse(Vector3 impulse, Vector3 relativePos) {
		P = impulse.add(P);
		L = relativePos.cross(impulse).add(L);
		updateAux();
	}
	
	@Override
	public void thrustInputs(Vector3 force, Vector3 torque, float duration) {
		if (isThrusting()) {
			return;
		}
		this.tempForce = force;
		this.tempTorque = torque;
		this.tempStart = System.nanoTime();
		this.currTempDuration = duration;		
	}	
	
	@Override
	public void update(double deltaTime) {		
		updateState(deltaTime);	// updates x, q, P and L
		updateAux(); // updates all other auxiliary variables that describe the state
	}
	
	
	// private utility methods
	private void updateState(double deltaTime) {
		if (!isFixed()) {
			updatePositions(deltaTime); // x and q are updated		
			updateMomentums(deltaTime); // P and L are updated
		}		
	}
	
	private void updatePositions(double deltaTime) {
		x = v.multScaler(deltaTime).add(x);
//		if (this.constraint != null) {
//			this.x = constraint.closestPointOnSurface(x);
//		}		
		
		Quaternion dQ = omegaQ.multQuat(q).multScaler(deltaTime).multScaler(0.5); // qdot = 0.5 * w(t) * q(t)
		q = q.add(dQ);
		q = q.normalize();		
	}

	
	private void updateMomentums(double deltaTime) {
		Vector3 currForce = force;
		Vector3 currTorque = torque;
		if (isThrusting()) {
			currForce = currForce.add(this.tempForce);
			currTorque = currTorque.add(this.tempTorque);
		}

		P = currForce.multScaler(deltaTime).add(P);
		L = currTorque.multScaler(deltaTime).add(L);
	}
	
	private void updateAux() {
		v = P.multScaler(invMass);
		if (this.constraint != null) {
			v = this.constraint.projectToManifold(x, v);
		}

		R = q.toMatrix();
	
		transR = R.transpose();

		updateInvI();
		
		omega = L.multMatrixLeft(invI);	

		omegaQ = Quaternion.vectorQ(omega);
	}
	
	private void updateInvI() {
		this.invI = inertiaBodyInv.rotate(this.R);
	}
	

	@Override
	public final Vector3 toBodySpace(Vector3 pW) {
		return pW.subtract(x).multMatrixLeft(transR);
	}
	

	@Override
	public final Vector3 toWorldSpace(Vector3 pB) {
		return pB.multMatrixLeft(R).add(x);

	}

	@Override
	public final Segment toBodySpace(Segment s) {
		return new Segment(toBodySpace(s.a), toBodySpace(s.b));
	}

	@Override
	public final Segment toWorldSpace(Segment s) {
		return new Segment(toWorldSpace(s.a), toWorldSpace(s.b));
	}	
}
