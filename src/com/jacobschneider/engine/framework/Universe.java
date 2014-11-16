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
	public void addScalerPotential(ScalarField s);

	/**
	 * Adds a scaler potential to a {@link Body} in the universe. If the body does not exist
	 * in the universe throws an {@link IllegalArgumentException}. The origin of the scaler field is updated
	 * to align with the body's position before each frame. If a body already contains a potential the original one
	 * will be overwritten.
	 * @param b the body to attach the scaler potential to
	 * @param s the scaler potential
	 */
	public void addBodyPotential(Body b, ScalarField s);

}
