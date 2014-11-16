package com.jacobschneider.engine.math.boundingvolumes;

import com.jacobschneider.engine.framework.BoundVolume;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;

/**
 * Empty BoundVolume class whose overlap strategies always return true.
 * This is hardcoded into BoundVolume and any collision with this volume
 * will return true no matter what strategies are placed in {@link BoundVolume#putStrategy(OverlapStrategy)}
 * 
 * @author Jacob
 *
 */
public class BoundNull extends BoundVolume {
	@Override
	public void updateState(Vector3 x, Quaternion q) {}
}
