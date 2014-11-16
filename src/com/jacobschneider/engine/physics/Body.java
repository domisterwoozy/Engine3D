package com.jacobschneider.engine.physics;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.boundingvolumes.BoundNull;
import com.jacobschneider.engine.math.boundingvolumes.BoundVolume;
import com.jacobschneider.engine.math.geometry.Manifold;
import com.jacobschneider.engine.physics.Collision.CollisionInterface;
import com.jacobschneider.engine.physics.Collision.Contact;
import com.jacobschneider.engine.physics.PhysicsBody.Axis;

/**
 * Holds information about a single body.
 * This includes the material, the shape, the bounding volume, and the physics.
 * Also contains a {@link Drawable} mixin that is called by {@link BasicUniverse}.
 * Can be created by a {@link Builder} or through one of the static factory methods in {@link Bodies}.
 * 
 * @author Jacob
 *
 */
public class Body implements Drawable {
	private final Material mat;
	private final Shape shape;
	private final BoundVolume boundingVolume;
	private final PhysicsBody rigidBody;	
	
	/**
	 * Builder pattern for the Body class
	 * 
	 * @author Jacob
	 *
	 */
	public static class Builder {
		// required parameters
		// not final so that you can still modify them in the builder
		private Vector3 position;
		private double mass;
		private Shape shape;
		
		// optional parameters
		private Quaternion q0 = Quaternion.identity;
		private Vector3 velocity = Vector3.zero;
		private Vector3 omegaBody = Vector3.zero;
		private Matrix3 inertiaBody = Matrix3.IDENTITY;
		private Material mat = Material.defaultMaterial;
		private BoundVolume boundingVolume = new BoundNull();
		private boolean fixedPos = false;
		private Set<Axis> fixedAxes = EnumSet.noneOf(Axis.class);
		private Vector3 rotateAround = null;
		private double scalerInertia = 0;
		public Manifold constraint = null;
		
		public Builder(Vector3 position, double mass, Shape shape) {
			this.position = position;
			this.mass = mass;
			this.shape = shape;
		}
		
		/**
		 * Static factory method that creates a new builder object from an existing body.
		 * This is useful if you want to make a new body that is only slightly different
		 * than an existing body. If you call {@link #build()} on the return value of this method
		 * without modifying it you will receive a an exact duplicate of the body in the parameter b.
		 * @param b The body to create the builder from.
		 * @return The resulting {@link Builder} object.
		 */
		public static Builder builderFromBody(Body b) {
			Builder builder = new Builder(b.rigidBody.getX(), b.rigidBody.getMass(), b.shape);
			builder.q0 = b.rigidBody.getQ();
			builder.velocity = b.rigidBody.getVel();
			builder.omegaBody = b.rigidBody.getOmega().rotate(b.rigidBody.getR().invert()); // converts omega to the body frame
			builder.inertiaBody = b.rigidBody.getI().rotate(b.rigidBody.getR().invert()); // converts inertia to the body frame
			builder.mat = b.mat;
			builder.boundingVolume = b.boundingVolume;
			if (b.rigidBody.isPosFixed()) {
				builder.fixedPos = true;
			}
			for (Axis axis : Axis.values()) {
				if (b.rigidBody.isAxisFixed(axis)) {
					builder.fixedAxes.add(axis);
				}
			}
			return builder;
		}
		
		/**
		 * @param shape New shape of the body
		 * @return The updated {@link Builder} object
		 */
		public Builder changeShape(Shape shape) {this.shape = new Shape(shape); return this;};
		
		/**
		 * Changes the mass of the body. If the body's position was fixed
		 * it will not be once you give if a finite mass.
		 * @param mass New mass of the body
		 * @return The updated {@link Builder} object
		 */
		public Builder changeMass(double mass) {
			this.fixedPos = false;
			this.mass = mass;
			return this;
		};
		
		/**
		 * @param pos New position of the body
		 * @return The updated {@link Builder} object
		 */
		public Builder changePos(Vector3 pos) {this.position = pos; return this;};
		
		/**
		 * 
		 * Sets the position and rotation (on all axes) of the body as fixed
		 * @return The updated {@link Builder} object
		 */
		public Builder fixBody(){
			this.fixedPos = true;
			this.fixedAxes = EnumSet.allOf(Axis.class);
			return this;
		}
		
		/**
		 * Sets the position of the body as fixed
		 * @return The updated {@link Builder} object
		 */
		public Builder fixPos() {this.fixedPos = true; return this;};	
		
		/**
		 * 
		 * Sets the rotation of the body as fixed on all axes.
		 * @return The updated {@link Builder} object
		 */
		public Builder fixRot() {this.fixedAxes = EnumSet.allOf(Axis.class); return this;};		
		
		/**
		 * Sets the rotation of the body to fixed on
		 * a specified set of axes.
		 * @param axes The set of axes
		 * @return The updated {@link Builder} object.
		 */
		public Builder fixAxes(Set<Axis> axes) {
			for (Axis axis : axes) {
				fixedAxes.add(axis);
			}
			return this;
		}
		
		public Builder setConstraint(Manifold constraint) {
			this.constraint = constraint;
			return this;
		}
		
		/**
		 * Fixes the rotation around a specific axis (body coordinates). This will override all other changes
		 * to rotational properties including {@link #fixRot()}, {@link #fixPos()}, {@link #inertiaTensor(Matrix3)}, and {@link #fixAxes(Set)}.
		 * @param axisBody rotation is limited to around this axis (body coordinates)
		 * @param scalerInertia scaler inertia around the axis
		 * @return The updated {@link Builder} object
		 */
		public Builder fixRotationAround(Vector3 axisBody, double scalerInertia) {
			this.fixedAxes = EnumSet.noneOf(Axis.class);
			this.rotateAround = axisBody;
			this.scalerInertia = scalerInertia;
			return this;
		}
		
		/**
		 * @param q0 Sets the initial orientation of the body in space.
		 * @return The updated {@link Builder} object
		 */
		public Builder initialRotation(Quaternion q0) {this.q0 = q0; return this;}
		/**
		 * @param v Sets the initial velocity of the body
		 * @return The updated {@link Builder} object
		 */
		public Builder initialVelocity(Vector3 v) {this.velocity = v; return this;}
		/**
		 * @param omega Sets the initial angular velocity of the body in body coordinates
		 * @return The updated {@link Builder} object
		 */
		public Builder initialOmega(Vector3 omega) {this.omegaBody = omega; return this;};
		/**
		 * Changes the inertia tensor of the body. If the body's rotation was fixed
		 * it will no longer be once the inertia tensor is set to a finite value.
		 * @param inertia Sets the moment of inertia of the body in the body frame coordinates.
		 * @return The updated {@link Builder} object
		 */
		public Builder inertiaTensor(Matrix3 inertia) {
			this.fixedAxes = EnumSet.noneOf(Axis.class);
			this.inertiaBody = inertia;
			return this;
		}
		
		/**
		 * @param mat Gives the body a new material
		 * @return The updated {@link Builder} object
		 */
		public Builder material(Material mat) {this.mat = mat; return this;};
		/**
		 * @param boundingVolume Gives the body a custom bounding volume
		 * @return The updated {@link Builder} object
		 */
		public Builder boundVolume(BoundVolume boundingVolume) {this.boundingVolume = boundingVolume; return this;};
		/**
		 * Creates the Body object
		 * @return The {@link Body} object created from this builder.
		 */
		public Body build() {
			return new Body(this);
		}
	}
	
	
	private Body(Builder builder) {
		this.mat = builder.mat;
		this.shape = builder.shape;
		this.boundingVolume = builder.boundingVolume;
		this.rigidBody = new RigidBody6DOF(builder.mass, builder.inertiaBody, builder.position, builder.q0, builder.velocity, builder.omegaBody);	
		if (builder.fixedPos) {			
			rigidBody.fixPosition();
		}
		this.rigidBody.fixAxes(builder.fixedAxes);
		if (builder.rotateAround != null) {
			this.rigidBody.fixRotationAroundAxis(builder.rotateAround, builder.scalerInertia);
		}
		if (builder.constraint != null) {
			this.rigidBody.constrainBody(builder.constraint);
		}
	}	

	
	/**
	 * Updates the Body by one physics frame.
	 * @param deltaTime time elapsed since last physics frame.
	 */
	public void update(float deltaTime) {
		move(deltaTime);
	}
	
	/**
	 * Draws the Body graphically in the window. 
	 * 
	 * @param drawable JOGL drawable object to draw to
	 */
	@Override
	public void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		// rotates into the bodies frame of reference
		gl.glLoadIdentity();                // reset the model-view matrix		
	    double[] x = rigidBody.getX().toArray();
	    gl.glTranslated(x[0], x[1], x[2]);    // translate left and into the screen	    
	    double mag = Math.sqrt( 1 - rigidBody.getQ().s*rigidBody.getQ().s);
	    gl.glRotated(Math.toDegrees(2 * Math.acos(rigidBody.getQ().s)), rigidBody.getQ().v.x / mag, rigidBody.getQ().v.y / mag, rigidBody.getQ().v.z / mag); // rotate about the y-axis
	    
		shape.draw(drawable);
	}
	
	private void move(float deltaTime) {
		rigidBody.update(deltaTime);
		boundingVolume.updateState(rigidBody.getX(), rigidBody.getQ());
	}
	
	/**
	 * Inputs additional forces and torques onto the body. 
	 * These inputs remain on the body for the duration of objects existence.
	 * 
	 * @param force force being exerted on the object
	 * @param torque torque being exerted on the object
	 */
	public void addInputs(Vector3 force, Vector3 torque) {
		rigidBody.addInputs(force, torque);
	}
	
	/**
	 * Inputs a temporary force/torque on a body for a certain duration of time.
	 * @param force force being exerted on the body
	 * @param torque torque being exerted on the object
	 * @param duration the amount of time the force and torque are held for
	 */
	public void thrustInputs(Vector3 force, Vector3 torque, float duration) {
		rigidBody.thrustInputs(force, torque, duration);
	}
	
	/**
	 * The position of this body in 3D space.
	 * @return position of the body
	 */
	public Vector3 getX() {
		return rigidBody.getX();
	}	
	
	/**
	 * Adds a permanent acceleration to the center of mass of this body.
	 * This remains affecting the body for the duration of the simulation
	 * @param accel Acceleration to add to this body
	 */
	public void addAccel(Vector3 accel) {
		if (!rigidBody.isPosFixed()) { // acceleration is not calculable if mass if infinite so ignore
			rigidBody.addInputs(accel.multScaler(rigidBody.getMass()), Vector3.zero);
		}
	}
	
	/**
	 * Determines all points of contact between this body and another body
	 * 
	 * @param other The other {@link Body} object
	 * @return a list of {@link Contact} objects
	 */
	public List<Contact> collisionDetect(Body other) {
		if (boundingVolume.testOverlap(other.boundingVolume)) {
			List<CollisionInterface> inters = shape.collisionDetect(other.shape, this.rigidBody, other.rigidBody);
			 if (inters.size() != 0) {	
				 List<Contact> contacts = new ArrayList<Contact>();
				 for (CollisionInterface i : inters) {
					 contacts.add(new Contact(this.rigidBody, other.rigidBody, this.mat, other.mat, i.r, i.n));
				 }
				return contacts;
			 }
		}
		return null;
	}

	/**
	 * Determines if a body is fixed
	 * 
	 * @return Whether this body is fixed
	 */
	public boolean isFixed() {
		return rigidBody.isFixed();
	}



	

}
