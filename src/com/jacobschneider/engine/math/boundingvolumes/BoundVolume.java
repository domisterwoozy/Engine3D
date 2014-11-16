package com.jacobschneider.engine.math.boundingvolumes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.boundingvolumes.OverlapStrategyContainer.CircleCircleOverlap;
import com.jacobschneider.engine.math.boundingvolumes.OverlapStrategyContainer.SphereCircleOverlap;
import com.jacobschneider.engine.math.boundingvolumes.OverlapStrategyContainer.SphereSphereOverlap;
import com.jacobschneider.engine.physics.Body;
import com.jacobschneider.engine.physics.Shape;

/**
 * Represents a bounding volume around a physical object (usually a {@link Shape} object).
 * Objects of this type should be mutable and created once at the beginning of a {@link Body}'s existence.
 * If you create a subclass of this class you MUST add corresponding overlap strategies to {@link #putStrategy(OverlapStrategy)}
 * BEFORE you instantiate an object of that subclass.
 * At a minimum you must create strategies between all the implementations you make and between your implementations and
 * {@link BoundSphere} and {@link BoundCircle}. If you attempt to create a {@link BoundVolume} subclass that does not have 
 * a corresponding {@link OverlapStrategy} for all possible situations placed in {@link #putStrategy(OverlapStrategy)} an
 * {@link IllegalStateException} will be thrown as soon as the object is created.
 * 
 * @author Jacob
 *
 */
public abstract class BoundVolume {	
	private static Map<BoundVolumePair, OverlapStrategy> strategyMap = new HashMap<BoundVolumePair, OverlapStrategy>();	
	private static Set<Class<? extends BoundVolume>> implementations = new HashSet<Class<? extends BoundVolume>>(); // a set of all currently implemented BoundVolume subclasses
	
	/**
	 * Load all the default strategies onto strategyMap
	 */
	static {
		putStrategy(new SphereSphereOverlap());
		putStrategy(new SphereCircleOverlap());
		putStrategy(new CircleCircleOverlap());
	}	
	
	/**
	 * Places a new strategy into the strategy container.
	 * In order for a bounding volume to be used is must have
	 * all possible overlap strategies implemented and placed into this method.
	 * @param strat The overlap strategy to be put in the container
	 */
	protected static void putStrategy(OverlapStrategy strat) {
		strategyMap.put(strat.getVolumePair(), strat);
	}
	
	/**
	 * Retrieves the appropriate strategy to test overlap between the two
	 * bounding volumes in pair parameter.
	 * @param pair The pair of types you want to receive the strategy for
	 * @return An {@link OverlapStrategy} object that should be used to test overlap.
	 */
	private static OverlapStrategy getStrategy(BoundVolumePair pair) {
		return strategyMap.get(pair);
	}
	
	/**
	 * Checks to make sure all possible {@link OverlapStrategy} implementations that could be possibly needed
	 * are implemented.
	 */
	private static void checkStrategies() {
		for (Class<? extends BoundVolume> i1 : implementations) {
			if (i1 == BoundNull.class) {
				continue;
			}
			for (Class<? extends BoundVolume> i2 : implementations) {
				if (i2 == BoundNull.class) {
					continue;
				}
				OverlapStrategy strat = getStrategy(new BoundVolumePair(i1, i2));
				if (strat == null) {
					throw new IllegalStateException("An overlap strategy between " + i1.getName() + " and "
													+ i2.getName() + " has not been added to the strategy container. You can add a strategy" +
															" using OverlapStrategyContainer.putStrategy");
				}
				
			}
		}
		
	}
	
	public BoundVolume() {
		implementations.add(this.getClass());
		checkStrategies();
	}
	
	/**
	 * Tests if this volume overlaps another volume.
	 * Any test involving {@link BoundNull} returns false.
	 * @param other The volume to test against.
	 * @return True if volumes intersect, otherwise false
	 */
	public final boolean testOverlap(BoundVolume other) {
		if (this.getClass() == BoundNull.class || other.getClass() == BoundNull.class) {
			return true;
		}
		OverlapStrategy strat = getStrategy(new BoundVolumePair(this, other));
		if (strat == null) {
			throw new IllegalStateException("An overlap strategy between " + this.getClass().getName() + " and "
											+ other.getClass().getName() + " has not been added to the strategy container. You can add a strategy" +
													" using OverlapStrategyContainer.putStrategy");
		}
		return strat.testOverlap(this, other);
	}
	
	/**
	 * Updates the position and orientation of this bounding volume.
	 * The positions is defined as the center of mass (origin) of the volume.
	 * @param x New origin of this volume.
	 * @param q New orientation of this volume.
	 */
	public abstract void updateState(Vector3 x, Quaternion q);	
		
}
