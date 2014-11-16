package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.Simulation;
import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.framework.ScalerField;
import com.jacobschneider.engine.framework.Universe;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.Collision.Contact;

/**
 * A container for {@link Body} objects. Also handles the collisions that occur between bodies.
 * Implementation not complete. Do not use.
 * 
 * @author Jacob
 *
 */
public class ParallelUniverse implements Universe,Drawable {	
	private final List<Body> bodies = new ArrayList<Body>();
	private final List<UpdateThreadRunnable> updates = new ArrayList<UpdateThreadRunnable>();
	
	ExecutorService exec;
	
	/**
	 * Creates a universe object that contains a list of bodies.
	 * @param bodies The list of bodies to be added to the universe.
	 */
	public ParallelUniverse(List<Body> bodies) {
		this.bodies.addAll(bodies);
		checkIntersection();
		
		exec = Executors.newCachedThreadPool();
		
		final float numThreads = 4;
		final int bodiesPerThread = (int) Math.ceil(bodies.size() / numThreads);
		int index = 0;
		
		for (int i = 0; i < numThreads; i++) {
			final ArrayList<Body> threadBodies = new ArrayList<Body>();
			for (int j = 0; j < bodiesPerThread; j++) {
				threadBodies.add(bodies.get(index++));
				if ((index + 1) > bodies.size()) {
					break;
				}
			}
			updates.add(new UpdateThreadRunnable(threadBodies));
			if ((index + 1) > bodies.size()) {
				break;
			}
		}		
		
	}
	
	
	private class UpdateThreadRunnable implements Runnable {
		private final List<Body> threadBodies;
		private float deltaTime = 0;
		
		public UpdateThreadRunnable(List<Body> bodies) {
			this.threadBodies = bodies;
		}
		
		public void updateDeltaTime(float deltaTime) {
			this.deltaTime = deltaTime;
		}

		@Override
		public void run() {
			updateThread(deltaTime, threadBodies);
			
		}		
	}
	
	/**
	 * Sets the acceleration of gravity for this universe.
	 * Gravity always points in the negative Z direction.
	 * 
	 * @param gravAccel Gravitational acceleration
	 */
	public final void setGravity(double gravAccel) {
		for (Body b : bodies) {
			b.addInputs(new Vector3(0, 0, -gravAccel), Vector3.zero);
		}
	}
	
	/**
	 * Updates the universe by the time deltaTime.
	 * If you are using a {@link Simulation} object the simulation
	 * will call this method for you.
	 * 
	 * @param deltaTime Time elapsed since last physics frame.
	 */
	public final void update(final float deltaTime) {
		final List<Future<?>> futures = new ArrayList<Future<?>>(); 		
		
		for (final UpdateThreadRunnable update : updates) {
			update.updateDeltaTime(deltaTime);
			futures.add(exec.submit(update));
		}
		
		for (Future<?> f : futures) {
			try {
				f.get(); // waits until runnable task is complete and then returns null if successful
			} catch (InterruptedException e) {
				System.out.println("Universe Task was interupted");
			} catch (ExecutionException e) {
				System.out.println("Universe Task failed");
			}
		}
				
	}
	
	
	
	private void updateThread(float deltaTime, List<Body> threadBodies) {
		for (int i = 0; i < threadBodies.size(); i++) {
			if (threadBodies.get(i).isFixed()) {
				continue; // does not move and therefore cannot cause a collision
			}	
			
			threadBodies.get(i).update(deltaTime); // moves the object

			List<Contact> contacts = new ArrayList<Contact>(); // all contacts that occur with this body and the rest.
			for (int j = 0; j < bodies.size(); j++) { // check for contacts with all other bodies except self
				if (i==j) {
					continue;
				}
				List<Contact> contact = threadBodies.get(i).collisionDetect(bodies.get(j)); // all contacts b/w two specific bodies
				if (contact != null && contact.size() != 0) {	
					contacts.addAll(contact);	
				}		
				
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
	
	/**
	 * Determines if any objects are currently overlapping
	 */
	private void checkIntersection() {
		for (int i = 0; i < bodies.size(); i++) {
			for (int j = 0; j < bodies.size(); j++) { // check for contacts with all other bodies except self
				if (i == j) {
					continue;
				}
				List<Contact> contact = bodies.get(i).collisionDetect(bodies.get(j)); // all contacts b/w two specific bodies
				if (contact != null && contact.size() != 0) {	
					throw new IllegalStateException("Bodies cannot be intersecting when the simulation begins");	
				}		
				
			}
		}
		
	}

	@Override
	public void addScalerPotential(ScalerField s) {
		throw new UnsupportedOperationException("Not yet implemented");
		
	}

	@Override
	public void addBodyPotential(Body b, ScalerField s) {
		throw new UnsupportedOperationException("Not yet implemented");		
	}
}
