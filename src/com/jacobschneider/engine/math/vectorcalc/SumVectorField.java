package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;

/**
 * A vector field that represents the sum of two other vector fields.
 * 
 * @author Jacob
 *
 */
public final class SumVectorField extends AbstractVectorField {
	private final VectorField a;
	private final VectorField b;
	
	/**
	 * Creates a new vector field that represents the vector addition
	 * of each vector at every point in 3D space.
	 * @param a first vector field to sum
	 * @param b second vector field to sum
	 */
	public SumVectorField(VectorField a, VectorField b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Vector3 getValue(Vector3 point) {
		return a.getValue(point).add(b.getValue(point)); // linear
	}

	@Override
	public Vector3 curl(Vector3 point) {
		return a.curl(point).add(b.curl(point)); // linear
	}

	@Override
	public double divergence(Vector3 point) {
		return a.divergence(point) + b.divergence(point); // linear
	}


}
