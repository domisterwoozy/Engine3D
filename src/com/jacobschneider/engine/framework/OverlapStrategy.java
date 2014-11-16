package com.jacobschneider.engine.framework;

import com.jacobschneider.engine.math.boundingvolumes.BoundVolumePair;

/**
 * An interface for an overlap strategy. An overlap strategy contains
 * an algorithm for testing whether two bounding volumes intersect.
 * Implementations of this interface should be stateless and added to to {@link BoundVolume#putStrategy(OverlapStrategy)}.
 * 
 * Every unique possible {@link BoundVolumePair} MUST have a corresponding implementation of this interface.
 * 
 * @author Jacob
 *
 */
public interface OverlapStrategy {
	/**
	 * Determines whether two bounding volumes intersect. When this is called by the engine the arguments
	 * are guaranteed to match the types returned by {@link #getVolumePair()} The order the arguments are input
	 * do not matter (reflexive).
	 * 
	 * @param vol1 - First volume to compare.
	 * @param vol2 - Second volume to compare.
	 * @return - Whether an intersection has occurred.
	 */
	public boolean testOverlap(BoundVolume vol1, BoundVolume vol2);
	
	/**
	 * The set of two BoundVolume types that this implementation handles.
	 * 
	 * @return - A BoundVolumePair object that contains these two types.
	 */
	public BoundVolumePair getVolumePair();
}
