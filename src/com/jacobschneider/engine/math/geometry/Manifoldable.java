package com.jacobschneider.engine.math.geometry;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.ScalarField;
import com.jacobschneider.engine.math.Vector3;

/**
 * A mixin that should be added to an implementation of {@link ScalarField}.
 * This allows the object to be converted into a manifold.
 * @author Jacob
 *
 */
public interface Manifoldable {
	/**
	 * Creates a manifold at a certain scaler value. The set of
	 * points that make up the manifold will be all points where
	 * {@link ScalarField#getValue(Vector3)}=potential when evaluated at that point.
	 * @param potential the value at which to create the manifold
	 * @return a manifold representing this scaler field at a certain scaler value
	 */
	public Manifold toManifold(double potential);

}