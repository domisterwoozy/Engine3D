package com.jacobschneider.engine.math.boundingvolumes;

import com.jacobschneider.engine.framework.BoundVolume;

/**
 * This class represents an unordered pair of BoundVolume types.
 * This is used so the framework can determine which overlap test strategy to invoke.
 * 
 * @author Jacob
 *
 */
public class BoundVolumePair {
	private final Class<? extends BoundVolume> a;
	private final Class<? extends BoundVolume> b;
	
	public BoundVolumePair(BoundVolume a, BoundVolume b) {
		if (!(a instanceof BoundVolume) || !(b instanceof BoundVolume)) {
			throw new IllegalArgumentException("All arguments must be implementations of BoundingVolume");
		}
		
		this.a = a.getClass();
		this.b = b.getClass();

	}
	
	public BoundVolumePair(Class<? extends BoundVolume> a, Class<? extends BoundVolume> b) {			
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BoundVolumePair)) {
			return false;
		}
		BoundVolumePair otherPair = (BoundVolumePair) o;
		if (otherPair == this) {
			return true;
		}
		if (a == otherPair.a) {
			if (b == otherPair.b){
				return true;
			}
		} else if (a == otherPair.b) {
			if (b == otherPair.a) {
				return true;
			}
		}
		return false;		
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() * b.hashCode();
	}

}
