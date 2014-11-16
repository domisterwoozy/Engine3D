package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.Simulation;
import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.framework.ScalerField;
import com.jacobschneider.engine.framework.Universe;
import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.Collision.Contact;

/**
 * A basic implementation of {@link Universe}.
 * This implementation is used by {@link Simulation}.
 * Has good performance when the number of non-fixed {@link Body} objects is less than around 20.
 * 
 * @author Jacob
 *
 */
public class BasicUniverse implements Universe,Drawable {
	/**
	 * The duration of an 'instantaneous' force.
	 */
	private static final float INSTANT_FORCE_DURATION = 0.00001f;
	private VectorField forceField;
	private double gravAccel = 0;
	
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
	public final void setGravity(double gravAccel) {
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
	public final void addScalerPotential(ScalerField s) {
		this.forceField = s.toVectorField();
	}
	
	/**
	 * Updates the universe by the time deltaTime.
	 * If you are using a {@link Simulation} object the simulation
	 * will call this method for you.
	 * 
	 * @param deltaTime Time elapsed since last physics frame.
	 */
	@Override
	public final void update(float deltaTime) {
		for (int i = 0; i < bodies.size(); i++) {
			if (bodies.get(i).isFixed()) {
				continue; // does not move and therefore cannot cause a collision
			} else {
				if (forceField != null) {
					bodies.get(i).thrustInputs(forceField.getValue(bodies.get(i).getX()), Vector3.zero, INSTANT_FORCE_DURATION);
				}
				bodies.get(i).update(deltaTime); // moves the object	
			}

			List<Contact> contacts = new ArrayList<Contact>(); // all contacts that occur with this body and the rest.
			for (int j = 0; j < bodies.size(); j++) { // check for contacts with all other bodies except self
				if (i==j) {
					continue;
				}
				List<Contact> contact = bodies.get(i).collisionDetect(bodies.get(j)); // all contacts b/w two specific bodies
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
