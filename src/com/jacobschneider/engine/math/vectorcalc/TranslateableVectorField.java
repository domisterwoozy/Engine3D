package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;

/**
 * A mutable wrapper that allows you to move a {@link VectorField} in the world.
 * 
 * @author Jacob
 *
 */
public class TranslateableVectorField implements VectorField {
	private final VectorField field;
	
	private Vector3 pos = Vector3.zero; // the position of the origin of this scaler field relative to the origin of the incoming points
	
	public TranslateableVectorField(VectorField field) {
		this.field = field;
	}
	
	public void updatePosition(Vector3 pos) {
		this.pos = pos;
	}

	@Override
	public Vector3 getValue(Vector3 point) {
		return field.getValue(point.subtract(pos));
	}

	@Override
	public Vector3 curl(Vector3 point) {
		return field.curl(point.subtract(pos));
	}

	@Override
	public double divergence(Vector3 point) {
		return field.divergence(point.subtract(pos));
	}

	@Override
	public TranslateableVectorField add(VectorField otherField) {
		return new TranslateableVectorField(new SumVectorField(field, otherField));
	}

}
