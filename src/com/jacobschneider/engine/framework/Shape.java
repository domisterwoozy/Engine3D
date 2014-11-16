package com.jacobschneider.engine.framework;

import java.util.List;

import com.jacobschneider.engine.physics.CollisionInterface;

/**
 * A collection of primitives and segments attached to one rigid body.
 * 
 * @author Jacob
 *
 */
public interface Shape {
	/**
	 * Detects all CollisionInterfaces (points and normals) of collisions between this shape and another shape.
	 * @param other The other shape.
	 * @param thisBody The {@link PhysicsBody} attached to this shape's body
	 * @param otherBody The {@link PhysicsBody} attached to the other shape's body
	 * @return A list of collisions.
	 */
	public List<CollisionInterface> collisionDetect(Shape other, PhysicsBody thisBody, PhysicsBody otherBody);
	
	/**
	 * The set of primitives the make up this shape.
	 * @return an array of primitives
	 */
	public Primitive[] getPrimitives();
	
	/**
	 * The set of segments that make up this shape.
	 * @return an array of segments
	 */
	public Segment[] getSegments();

}