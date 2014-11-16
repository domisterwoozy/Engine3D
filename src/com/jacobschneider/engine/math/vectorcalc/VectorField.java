package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.math.Vector3;

/**
 * A vector field over 3 dimensional real space
 * 
 * @author Jacob
 *
 */
public interface VectorField {
	/**
	 * The value of this vector field at a certain position
	 * in 3D space.
	 * @param point the position at which to evaluate the vector field
	 * @return the resulting vector
	 */
	public Vector3 getValue(Vector3 point);
	
	/**
	 * Evaluates the curl of this vector field at a certain position
	 * in 3D space
	 * @param point the position at which to evaluate the curl
	 * @return the resulting curl vector
	 */
	public Vector3 curl(Vector3 point);
	
	/**
	 * Evaluates the divergence of this vector field at a certain position
	 * in 3D space
	 * @param point the position at which to evaluate teh curl
	 * @return the resulting curl vector
	 */
	public double divergence(Vector3 point);
	
	/**
	 * Adds this vector field to another one
	 * @param otherField the other vector field to add
	 * @return the resulting vector field that represents the sum
	 */
	public VectorField add(VectorField otherField);

}
