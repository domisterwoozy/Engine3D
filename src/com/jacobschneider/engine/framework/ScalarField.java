package com.jacobschneider.engine.framework;

import com.jacobschneider.engine.math.Vector3;

/**
 * A scaler field over 3 dimensional real space
 * 
 * @author Jacob
 *
 */
public interface ScalarField {
	/**
	 * The scaler value returned by this scaler field
	 * at a certain point in space
	 * @param point the point at which to evaluate the scaler field
	 * @return The scaler value
	 */
	public double getValue(Vector3 point);
	
	/**
	 * The gradient of the scaler field at a certain
	 * point in the space
	 * @param point the point at which to evaluate the gradient of this scaler field
	 * @return the resulting gradient
	 */
	public Vector3 gradient(Vector3 point);
	
	/**
	 * Converts this scaler field into a vector field
	 * corresponding to the negative gradient of this scaler field. If this
	 * scaler field represents a potential then this function will
	 * return a force field representing the force enacted by the potential.
	 * @return a vector field equal to the negative gradient of this scaler field
	 */
	public VectorField toVectorField();
	
	/**
	 * Adds this scaler field to another.
	 * @param otherField the other scaler field to be summed
	 * @return the resulting sum
	 */
	public ScalarField add(ScalarField otherField);

	/**
	 * Converts this scaler field to a manifold at a certain potential
	 * @param potential the potential value
	 * @return the resulting manifold
	 */
	public Manifold toManifold(double potential);

	/**
	 * Traverses a path of steepest ascent/descent until
	 * the desired potential is reached.
	 * @param point starting point
	 * @param desiredPotential the potential you want to reach
	 * @return the ending point of the path once the potential is reached
	 */
	public Vector3 gradientTraversal(Vector3 point, double desiredPotential);

}
