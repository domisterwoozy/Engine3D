package com.jacobschneider.engine.physics;

import java.util.List;

import com.jacobschneider.engine.math.Vector3;

/**
 * Represents all the all of the contacts that occur on one body in a single physics frame.
 * The heart of the engine is implemented here. This entire class is hidden from the client
 * except for a useful struct called {@link CollisionInterface}.
 * 
 * @author Jacob
 *
 */
public class Collision {
	private static enum ContactType {
		Colliding,Resting,Receding
	};

	/**
	 * A lightweight struct that holds data describing a single contact.
	 * @author Jacob
	 *
	 */
	public static class CollisionInterface {
		public final Vector3 r; // point
		public final Vector3 n; // plane normal that intersects point
		
		
		public CollisionInterface(Vector3 r, Vector3 n) {
			this.r = r;
			this.n = n;
		}
		
		public static List<CollisionInterface> flipNormals(List<CollisionInterface> inters) {
			for (int i = 0; i < inters.size(); i++) {
				inters.set(i, new CollisionInterface(inters.get(i).r, inters.get(i).n.inverse()));
			}
			return inters;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof CollisionInterface)) {
				return false;
			}
			CollisionInterface otherInter = (CollisionInterface) o;
			if (otherInter == this) {
				return true;
			}
			if (!otherInter.r.equals(this.r)) {
				return false;
			}
			if (!otherInter.n.equals(this.n)) {
				return false;
			}
			
			return true;		
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + r.hashCode();
			result = 31 * result + n.hashCode();
			return result;
		}
	}
	
	// engine constants, always be tuning
	private static final int MAX_LOOPS = 100;
	private static final double RESTING_THRESHOLD = 0.01;
	private static final double STATIC_FRICTION_TOLERANCE = 0.01;
	
	// universal material constants
	// if universe and material coeffients are 1.0 these feel right
	private static final double EPISILON = 0.05;
	private static final double DYNAMIC_FRICTION_COEFF = 0.5;
	private static final double STATIC_FRICTION_COEFF = 2.0;
	
	
	private Contact[] contacts;	
	
	Collision(Contact[] contacts) {
		this.contacts = contacts;
	}
	
	
	private void collideUntilDone() {	
		int loopCount = 0;
		boolean continueLooping = true;
		while (continueLooping) {
			loopCount++;
			if (loopCount > MAX_LOOPS) {
				//System.out.println("num contacts: " + contacts.length);
				//throw new IllegalStateException();
				return;
			}
			continueLooping = false;
			for (int i = 0; i < contacts.length; i++) {
				if (contacts[i].getContactType(i) == ContactType.Colliding) {
					contacts[i].collide();
					continueLooping = true; // if any contacts are still colliding continue looping
				}
			}
		}
	}
	
	void collide() {	
//		collideUntilDone();
		for (int i = 0; i < contacts.length; i++) {
			if (contacts[i].getContactType(0) == ContactType.Colliding) {
				contacts[i].collide();
			}
		}		
	}	
	
	/**
	 * An object that represents a single point of contact b/w two bodies. It is completely defined by the point of contact, a plane of contact,
	 * and the colliding bodies.
	 * 
	 * @author Jacob
	 *
	 */
	static class Contact {
		private final PhysicsBody a; // should be the same for all contacts in this collision
		private final Material matA;
		private final PhysicsBody b; // for conceptual purposes you can assume this body is fixed
		private final Material matB;
		private Vector3 p; // world space vertex location // not currently used
		private final Vector3 n; // normal to plane of contact (collision plane)
		
		private final Vector3 ra,rb; // vectors from bodies center of masses to contact point (p)
		
		private Vector3 vpa; // velocity of point p on a
		private Vector3 vpb; // velocity of point p on b
		private Vector3 vRel; // vector - relative velocity b/w point p on a and on b
		private double vRelPerp; // scaler
		
		
		/**
		 * Normal vector should be pointing away from body A and towards body B
		 * @param a
		 * @param b
		 * @param matA
		 * @param matB
		 * @param p
		 * @param n
		 */
		Contact(PhysicsBody a, PhysicsBody b, Material matA, Material matB, Vector3 p, Vector3 n) {
			this.a = a;
			this.b = b;
			this.p = p;
			this.n = n;
			this.matA = matA;
			this.matB = matB;
			ra = p.subtract(a.getX());
			rb = p.subtract(b.getX());
		}
		
		private ContactType getContactType(int tempI) {
			vpa = a.getSurfaceVelocity(ra);				
			vpb = b.getSurfaceVelocity(rb);	
			vRel = vpa.subtract(vpb);
			vRelPerp = vRel.dot(n.inverse());
			
			
			if (vRelPerp > RESTING_THRESHOLD) {
				return ContactType.Receding; // no collision
			}
			else if (vRelPerp > -RESTING_THRESHOLD) {
				return ContactType.Resting; // no collision
			}
			else {
				return ContactType.Colliding;
			}
			

		}
		
		/**
		 * Heart and soul of the engine
		 * The "collision plane" is the plane defined by the normal n.
		 * The "parallel plane" is the plane vRel and the normal n are in. Defined by nPerp.
		 * @return The impulse applied to {@link PhysicsBody} 'a'.
		 */
		private void collide() {					
			double j = getNormalImpulse(); 
			//System.out.println(j);
			Vector3 forceNormal = n.multScaler(j).inverse();	
			Vector3 fricV = Vector3.zero; // frictional impulse vector
			
			Vector3 nPerp = vRel.cross(n); // equals zero if vRel is completely normal to collision plane
			if (nPerp.mag() != 0.0) { // there is motion in the collision plane -> friction exists				
				// unit vector parallel to the collision plane
				// equivalent to the normalized projection of vRel onto the collision plane				
				Vector3 nPar = n.cross(nPerp).normalize();
				double vRelPar = vRel.dot(nPar);
				double fric = getFrictionalImpulse(j,vRelPar);
				if (Double.isNaN(nPar.x)) {
					System.out.println("p: " + p);
					System.out.println("ra: " + ra);
					System.out.println("rb: " + rb);
					System.out.println("Vpa: " + vpa);
					System.out.println("Vpb: " + vpb);
					System.out.println("Normal: " + n);
					System.out.println("Reletive Vel: " + vRel);
					throw new IllegalStateException();
				}					
				double maxFric = getExtremeFrictionalImpulse(vRelPar, nPar, j); 
				// if maxFric is under zero this means the normal force is going to cause vRelPar to change signs.
				// therefore we want the standard friction force to be negative as well
//				if (maxFric < 0) {
//					fric = -1 * fric;
//				}
				if (Math.abs(fric) > Math.abs(maxFric)) {
					fric = maxFric;
				}
				fricV = nPar.inverse().multScaler(fric);
			}

			Vector3 forceA = fricV.add(forceNormal); // total force on body A
			Vector3 forceB = forceA.inverse(); // total force on body B
			
			//double energyInitial = a.getEnergy() + b.getEnergy();
			//System.out.println(forceA);
			
			a.enactImpulse(forceA, ra);
			b.enactImpulse(forceB, rb);	
			
			//double energyFinal = a.getEnergy() + b.getEnergy();			
//			if (energyFinal > 1.5f*energyInitial) {
//				throw new IllegalStateException("energy went up dog");
//			}
		}
		
		/**
		 * Returns the impulse that would create zero relative parallel velocity b/w the two points (pa and pb).
		 * All calculations occur in one plane. The plane is defined by the plane that nPar and vRel exist in and I am calling it the parallel plane.
		 * Reducing everything to one plane allows you to simplify the cross product and find a unique result value.
		 * @param nPar Normalized projection of vRel onto the collision plane
		 * @param j Normal impulse from collision
		 * @return
		 */
		private double getExtremeFrictionalImpulse(double vRelPar, Vector3 nPar, double j) {
			Vector3 nPerp = nPar.cross(n).normalize(); // in collision plane and perpendicular to nPar
			//double c1a = 0;
			//double c1b = 0;
			double c2a = 0;
			double c2b = 0;
			
			double thetaA = 0;
			double thetaB = 0;
			
			Vector3 raPar = null;
			Vector3 rbPar;

			// body A
			if (!a.isFixed()) {				
				raPar = ra.projectToPlane(nPerp);
				thetaA = nPar.angleBetween(raPar.inverse());
				double raMag = raPar.mag();
				Vector3 axisOfRotA = nPar.cross(raPar).normalize(); // axis of rotation that friction causes the object to spin around
				double invScalerRotInertiaA = a.getInvI().mag(axisOfRotA);
				//c1a = (raMag * j * Math.sin(Math.PI / 2 + thetaA) * raMag * Math.cos(thetaA + Math.PI / 2)) * invScalerRotInertiaA; // normal impulse contribution to delta vParA
				c2a = ((raMag * Math.sin(thetaA) * raMag * Math.cos(thetaA + Math.PI / 2)) * invScalerRotInertiaA) - a.getInvMass(); // (c2 * friction) is frictional component to delta vRelPar
			}

			// body B
			if (!b.isFixed()) {
				rbPar = rb.projectToPlane(nPerp);
				thetaB = nPar.angleBetween(rbPar.inverse());
				double rbMag = rbPar.mag();
				Vector3 axisOfRotB = nPar.cross(rbPar).normalize();
				double invScalerRotInertiaB = b.getInvI().mag(axisOfRotB);
				//c1b = (rbMag * -j * Math.sin(Math.PI / 2 + thetaB) * rbMag * Math.cos(thetaB + Math.PI / 2)) * invScalerRotInertiaB; // normal impulse contribution to delta vParA
				c2b = ((rbMag * Math.sin(thetaB) * rbMag * Math.cos(thetaB + Math.PI / 2)) * invScalerRotInertiaB) - b.getInvMass(); // (c2 * friction) is frictional component to delta vRelPar
			}
			
			//double result = (vRelPar + c1a - c1b) / (-c2b - c2a); // can be negative, takes into account normal impulse
			double result = (vRelPar) / (-c2b - c2a); // can never be negative, only takes into account friction
			if (Double.isNaN(result)) {
				System.out.println(j);
				System.out.println("Numer: " + (1 + EPISILON * ((matA.getEpsilon() + matB.getEpsilon()))/2) * Math.pow(Math.abs(vRelPerp), 1.3));
				System.out.println("Term 3: " + n.dot(a.getInvI().multVectRight(ra.cross(n)).cross(ra)));
				System.out.println("Term 4: " + n.dot(b.getInvI().multVectRight(rb.cross(n)).cross(rb)));
				throw new IllegalStateException("Friction cannot be NaN");
			}
			if (result < 0) {
				throw new IllegalStateException("Friction cannot be negative");
			}
			return result;
		}
		
		private double getFrictionalImpulse(double normalImpulse, double vRelPar) {
			if (Math.abs(vRelPar) < STATIC_FRICTION_TOLERANCE * ((matA.getStaticFric() + matB.getStaticFric())/2)) {
				return STATIC_FRICTION_COEFF * ((matA.getStaticFric() + matB.getStaticFric())/2) * normalImpulse;
			} else {
				return DYNAMIC_FRICTION_COEFF * ((matA.getDynamicFric() + matB.getDynamicFric())/2) * normalImpulse;
			}
			
		}
		private double getNormalImpulse() {
			double numerator = (1 + EPISILON * ((matA.getEpsilon() + matB.getEpsilon()))/2) * Math.pow(Math.abs(vRelPerp), 1.0);	
			double term1 = a.getInvMass();
			double term2 = b.getInvMass();
			double term3 = n.dot(a.getInvI().multVectRight(ra.cross(n)).cross(ra));
			double term4 = n.dot(b.getInvI().multVectRight(rb.cross(n)).cross(rb));
			return numerator / (term1 + term2 + term3 + term4);
		}
	}
		

}
	
	


