package com.jacobschneider.engine.math.geometry;

import com.jacobschneider.engine.math.Vector3;

/**
 * A 1 dimensional or 2 dimensional surface in 3D space.
 * 
 * @author Jacob
 *
 */
public interface Manifold {	
	/**
	 * Maps an arbitrary point in 3D space to the closest point on the surface
	 * @param point A point in 3D space
	 * @return The point on the surface
	 */
	public Vector3 mapToManifold(Vector3 point);
	
	/**
	 * Projects a vector onto the surface. The tail of the vector
	 * must originate on a point on the surface.
	 * @param surfacePoint A point on the surface
	 * @param vect The vector on the surface
	 * @return The projection of the vector onto the surface
	 */
	public Vector3 projectToManifold(Vector3 surfacePoint, Vector3 vect);	
	
	/**
	 * The perpendicular vector to the surface at a point on the surface
	 * @param pointOnSurface Point on the surface which to calculate the vector at
	 * @return The resultant perpendicular vector
	 */
	public Vector3 perpVect(Vector3 pointOnSurface);
	
	/**
	 * Determines if the input point is on the surface of this manifold to within
	 * a given tolerance.
	 * @param point the point in 3D space to check
	 * @return Whether the point is on the manifold
	 */
	public boolean isOnManifold(Vector3 point);

}
