package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.ScalarField;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.geometry.ScalerFieldManifold;

/**
 * Skeleton implementation of scaler field
 * 
 * @author Jacob
 *
 */
public abstract class AbstractScalarField implements ScalarField {
	private static final double STEP_SIZE = 10000;
	private static final double DOUBLE_EQUALITY_TOLERANCE = Math.pow(10, -2);

	@Override
	public ScalarField add(ScalarField otherField) {
		return new SumScalarField(this, otherField);
	}
	
	@Override
	public Manifold toManifold(double potential) {
		return new ScalerFieldManifold(this, potential);
	}
	
	@Override
	public Vector3 gradientTraversal(Vector3 point, double desiredPotential) {
		double potentialDiff = desiredPotential - getValue(point);
		double dx = potentialDiff / STEP_SIZE;
		while (Math.abs(potentialDiff) >= DOUBLE_EQUALITY_TOLERANCE) {				
			point = point.add(gradient(point).multScaler(dx * potentialDiff));
			potentialDiff = desiredPotential - getValue(point);		
		}
		return point;		
	}

}