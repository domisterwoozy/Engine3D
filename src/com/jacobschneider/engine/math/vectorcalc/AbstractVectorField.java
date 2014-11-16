package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.VectorField;


/**
 * Skeleton implementation of a {@link VectorField}
 * 
 * @author Jacob
 *
 */
public abstract class AbstractVectorField implements VectorField {

	@Override
	public VectorField add(VectorField otherField) {
		return new SumVectorField(this, otherField);
	}

}
