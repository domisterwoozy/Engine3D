package com.jacobschneider.engine.framework;

import java.util.List;

import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.CollisionInterface;
import com.jacobschneider.engine.physics.RigidShape;


/**
 * Interface for a primitive object. A primitive object is the lowest level collidable object in the engine.
 * All implementations of this interface should be immutable.
 * Any implementation that can define {@link #getSegments()} and {@link #intersectSegment(Segment)} is a valid primitive.
 * A {@link RigidShape} object is made up of a set of these primitives.
 * 
 * @author Jacob
 *
 */
public interface Primitive {
	/**
	 * Returns the CollisionInterface that occurs between a segment and this primitive.
	 * If no collision exists returns null.
	 * 
	 * @param s A segment object
	 * @return The {@link CollisionInterface} or null that represents the intersection of the segment and this primitive.
	 */
	public CollisionInterface intersectSegment(Segment s);
	/**
	 * Generates a list of all segments that make up this primitive.
	 * If your primitive had hard edges, every outer edge must have a segment returned here.
	 * If your primitive has rounded edges you must return enough segments to define the boundary
	 * of your object to your satisfaction. Consider accepting a parameter to determine accuracy and making the number of edges
	 * dynamic.
	 * @return All the segments that make up this primitive.
	 */
	public List<Segment> getSegments();
	/**
	 * Most primitives are a subset of a plane and have a well defined constant normal direction.
	 * If this is true return that normal direction.
	 * If not return null.
	 * Implementing this is not required and is used for performance purposes only.
	 * @return The constant normal vector of this primitive or null
	 */
	public Vector3 getNormal();
}
