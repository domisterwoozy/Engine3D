package com.jacobschneider.engine.math.vectorcalc;


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
