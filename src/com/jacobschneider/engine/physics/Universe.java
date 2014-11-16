package com.jacobschneider.engine.physics;


/**
 * A container for {@link Body} objects. Also handles the collisions that occur between bodies.
 * 
 * @author Jacob
 *
 */
public interface Universe {
	/**
	 * Sets the acceleration of gravity for this universe.
	 * Gravity always points in the negative Z direction.
	 * 
	 * @param gravAccel Gravitational acceleration
	 */
	public void setGravity(double gravAccel);
	
	/**
	 * Updates the universe and all the bodies in this universe by the time increment @deltaTime.
	 * 
	 * @param deltaTime Time elapsed since last physics frame.
	 */
	public void update(float deltaTime);

}
