package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.math.Vector3;

/**
 * Skeleton implementation of {@link Manifold}.
 *
 * @author Jacob
 *
 */
public abstract class AbstractManifold implements Manifold {
	private static final double DOUBLE_EQUALITY_TOLERANCE = Math.pow(10, -15);

	@Override
	public boolean isOnManifold(Vector3 point) {
		double dist = point.subtract(mapToManifold(point)).mag();
		if (dist <= DOUBLE_EQUALITY_TOLERANCE) {
			return true;
		} else {
			return false;
		}
	}



}
