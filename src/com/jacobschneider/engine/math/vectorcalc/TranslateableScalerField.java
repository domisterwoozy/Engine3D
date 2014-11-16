package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.ScalerField;
import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.geometry.ScalerFieldManifold;

/**
 * A mutable wrapper that allows you to move a {@link ScalerField} in the world.
 * 
 * @author Jacob
 *
 */
public class TranslateableScalerField implements ScalerField {
	private final ScalerField field;
	
	private Vector3 pos = Vector3.zero; // the position of the origin of this scaler field relative to the origin of the incoming points
	
	public TranslateableScalerField(ScalerField field) {
		this.field = field;
	}
	
	/**
	 * Sets the position of the origin of this scaler field.
	 * @param pos position in world coordinates
	 */
	public void updatePos(Vector3 pos) {
		this.pos = pos;
	}

	@Override
	public double getValue(Vector3 point) {
		return field.getValue(point.subtract(pos));
	}

	@Override
	public Vector3 gradient(Vector3 point) {
		return field.gradient(point.subtract(pos));
	}

	@Override
	public TranslateableVectorField toVectorField() {
		return new TranslateableVectorField(field.toVectorField());
	}

	@Override
	public TranslateableScalerField add(ScalerField otherField) {
		return new TranslateableScalerField(new SumScalerField(field, otherField));
	}

	@Override
	public Manifold toManifold(double potential) {
		return new ScalerFieldManifold(this, potential);
	}

	@Override
	public Vector3 gradientTraversal(Vector3 point, double desiredPotential) {
		return field.gradientTraversal(point.subtract(pos), desiredPotential);
	}

}
