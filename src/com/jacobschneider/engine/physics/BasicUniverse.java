package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.Simulation;
import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.framework.ScalarField;
import com.jacobschneider.engine.framework.Universe;
import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.vectorcalc.TranslateableScalarField;
import com.jacobschneider.engine.math.vectorcalc.TranslateableVectorField;
import com.jacobschneider.engine.physics.Collision.Contact;

/**
 * A basic implementation of {@link Universe}.
 * This implementation is used by {@link Simulation}.
 * Has good performance when the number of non-fixed {@link Body} objects is less than around 20.
 * 
 * @author Jacob
 *
 */
public final class BasicUniverse implements Universe,Drawable {
	private static final float INSTANT_FORCE_DURATION = 0.00001f; // the duration of an 'instantaneous' force.
	private VectorField forceField; // global vector field
	private double gravAccel = 0; // global gravity
	private Map<Body, TranslateableVectorField> bodyForces = new HashMap<>();
	
	private final List<Body> bodies = new ArrayList<Body>();
	
	/**
	 * Creates a universe object that contains a list of bodies
	 * @param bodies variable number comma delimited list of {@link Body} objects
	 */
	public BasicUniverse(Body... bodies) {
		for (Body b : bodies) {
			this.bodies.add(b);
		}
		initialIntersectionCheck();
	}
	
	/**
	 * Creates a universe object that contains a list of bodies.
	 * @param bodies The list of bodies to be added to the universe.
	 */
	public BasicUniverse(List<Body> bodies) {
		this.bodies.addAll(bodies);
		initialIntersectionCheck();
	}
	
	/**
	 * A copy constructor for Universe.
	 * The resulting object is guaranteed to be completely decoupled from the original.
	 * @param uni The object to copy
	 */
	public BasicUniverse(BasicUniverse uni) {
		this.gravAccel = uni.gravAccel;
		this.forceField = uni.forceField;
		this.bodies.addAll(uni.bodies);
	}
	
	/**
	 * Sets the acceleration of gravity for this universe.
	 * Gravity always points in the negative Z direction.
	 * 
	 * @param gravAccel Gravitational acceleration
	 */
	public void setGravity(double gravAccel) {
		this.gravAccel = gravAccel;
		for (Body b : bodies) {
			b.addAccel(new Vector3(0, 0, -gravAccel));
		}
	}
	
	/**
	 * Adds a scaler potential to this universe. All objects in this universe
	 * will undergo a force on each frame equal to the negative gradient
	 * of this field. The reason why you are not able to add a {@link VectorField} directly to this
	 * universe is to ensure that all forces are conservative.
	 * @param s the scaler field
	 */
	@Override
	public void addScalerPotential(ScalarField s) {
		this.forceField = s.toVectorField();
	}
	
	/**
	 * Adds a scaler potential to a {@link Body} in the universe. If the body does not exist
	 * in the universe throws an {@link IllegalArgumentException}. The origin of the scaler field is updated
	 * to align with the body's position before each frame. If a body already contains a potential the original one
	 * will be overwritten.
	 * @param b the body to attach the scaler potential to
	 * @param s the scaler potential
	 */
	@Override
	public void addBodyPotential(Body b, ScalarField s) {
		if (!bodies.contains(b)) {
			throw new IllegalArgumentException("The universe does not contain this body");
		}
		bodyForces.put(b, new TranslateableScalarField(s).toVectorField());
	}
	
	/**
	 * Updates the universe by the time deltaTime.
	 * If you are using a {@link Simulation} object the simulation
	 * will call this method for you.
	 * 
	 * @param deltaTime Time elapsed since last physics frame.
	 */
	@Override
	public void update(float deltaTime) {
		for (Body b1 : bodies) {
			if (b1.isFixed()) {
				continue; // does not move and therefore cannot cause a collision
			} else {
				TranslateableVectorField myField = null;
				if (bodyForces.containsKey(b1)) { // update this bodies body force
					myField = bodyForces.get(b1);
					myField.updatePosition(b1.getX());
				}
				for (VectorField f : bodyForces.values()) { // enact all body forces
					if (f != myField) { // dont exert potential on myself
						b1.thrustInputs(f.getValue(b1.getX()), Vector3.zero, INSTANT_FORCE_DURATION);
					}
				}
				if (forceField != null) { // enact global force
					b1.thrustInputs(forceField.getValue(b1.getX()), Vector3.zero, INSTANT_FORCE_DURATION);
				}
				
				b1.update(deltaTime); // moves the object	
			}

			List<Contact> contacts = new ArrayList<Contact>(); // all contacts that occur with this body and all other bodies.
			for (Body b2 : bodies) { // check for contacts with all other bodies except self
				if (b1 == b2) {
					continue;
				}
				List<Contact> contact = b1.collisionDetect(b2); // all contacts b/w two specific bodies
				if (contact != null && contact.size() != 0) {	
					contacts.addAll(contact);	
				}		
				
			}			
			if (contacts.isEmpty()) {
				continue;
			}			
			Collision collision = new Collision(contacts.toArray(new Contact[contacts.size()]));
			collision.collide();
		}
				
	}
	
	/**
	 * Calls the the draw method on each body in this universe.
	 * If you are using a {@link Simulation} object the simulation
	 * will call this for you. Override this method to implement your own
	 * JOGL graphics.
	 * 
	 * @param drawable JOGL drawable object
	 */
	public void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();			
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear color and depth buffer
		
		for (int i = 0; i < bodies.size(); i++) {
			bodies.get(i).draw(drawable);
		}
	}
	
	private void initialIntersectionCheck() {
		if (checkIntersection()) {
			throw new IllegalStateException("Bodies cannot be intersecting when the simulation begins");
		}
	}
	
	/**
	 * Determines if any objects are currently overlapping
	 */
	private boolean checkIntersection() {
		for (int i = 0; i < bodies.size(); i++) {
			for (int j = 0; j < bodies.size(); j++) { // check for contacts with all other bodies except self
				if (i == j) {
					continue;
				}
				List<Contact> contact = bodies.get(i).collisionDetect(bodies.get(j)); // all contacts b/w two specific bodies
				if (contact != null && contact.size() != 0) {	
					return true;	
				}		
				
			}
		}
		return false;		
	}
}
