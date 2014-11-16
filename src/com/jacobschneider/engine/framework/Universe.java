package com.jacobschneider.engine.framework;

import com.jacobschneider.engine.physics.Body;


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
	 * Updates the universe and all the bodies in this universe by the time increment deltaTime.
	 * 
	 * @param deltaTime time elapsed since last physics frame.
	 */
	public void update(float deltaTime);
	
	/**
	 * Adds a scaler potential to this universe. All objects in this universe
	 * will undergo a force on each frame equal to the negative gradient
	 * of this field. The reason why you are not able to add a {@link VectorField} directly to this
	 * universe is to ensure that all forces are conservative.
	 * @param s the scaler field
	 */
	public void addScalerPotential(ScalerField s);

}
