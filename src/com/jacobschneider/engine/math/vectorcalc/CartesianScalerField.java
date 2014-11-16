package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.ScalerField;
import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.geometry.Manifoldable;

/**
 * An implementation of {@link ScalerField} of the form f(x,y,z) = A*x^a + B*y^b + C*z^c.
 * 
 * @author Jacob
 *
 */
public class CartesianScalerField extends AbstractScalerField implements Manifoldable {
	private final double A,B,C; // coefficients
	private final double a,b,c; // exponents
	
	/**
	 * Creates a scaler field of the form
	 * f(x,y,z) = A*x^a + B*y^b + C*z^c.
	 * @param coefficients the coefficients of the scaler field where the first,
	 * second, and third elements of the array represent A, B, and C respectively.
	 * Must be of length 3.
	 * @param exponents the exponents of the scaler field where the first,
	 * second, and third elements of the array represent a, b, and c respectively.
	 * Must be of length 3.
	 */
	public CartesianScalerField(double[] coefficients, double[] exponents) {
		if (coefficients.length != 3) {
			throw new IllegalArgumentException("Coefficients array must have 3 values. Consider using a value of one.");
		}
		if (exponents.length != 3) {
			throw new IllegalArgumentException("Exponents array must have 3 values. Consider using a value of zero.");
		}		
		this.A = coefficients[0];
		this.B = coefficients[1];
		this.C = coefficients[2];		
		this.a = exponents[0];
		this.b = exponents[1];
		this.c = exponents[2];
	}	
	

	@Override
	public double getValue(Vector3 point) {
		return A * Math.pow(point.x, a) + B * Math.pow(point.y, b) + C * Math.pow(point.z, c);
	}

	@Override
	public Vector3 gradient(Vector3 point) {
		return new Vector3(dfdx(point), dfdy(point), dfdz(point));
	}
	
	@Override
	public VectorField toVectorField() {
		return new CartesianVectorField(new CartesianScalerField(new double[] {-a * A, 0, 0}, new double[] {a - 1, 0, 0}),
				new CartesianScalerField(new double[] {0, -b * B, 0}, new double[] {0, b - 1, 0}),
				new CartesianScalerField(new double[] {0, 0, -c * C}, new double[] {0, 0, c - 1}));
	}	
	
	/**
	 * The derivative of this scaler field with respect to x
	 * at a point.
	 * @param point point to be evaluated at
	 * @return the derivative
	 */
	public double dfdx(Vector3 point) {
		return a * A * Math.pow(point.x, a - 1);
	}
	
	/**
	 * The derivative of this scaler field with respect to y
	 * at a point.
	 * @param point point to be evaluated at
	 * @return the derivative
	 */
	public double dfdy(Vector3 point) {
		return b * B * Math.pow(point.y, b - 1);
	}
	
	/**
	 * The derivative of this scaler field with respect to z
	 * at a point.
	 * @param point point to be evaluated at
	 * @return the derivative
	 */
	public double dfdz(Vector3 point) {
		return c * C * Math.pow(point.z, c - 1);
	}


}
