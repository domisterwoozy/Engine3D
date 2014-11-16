package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.math.Vector3;

/**
 * A generic scaler field in the spherical coordinate system (r, phi, theta).
 * Where phi is the azimuth and theta is the inclination.
 * x = r sin(theta) cos(phi)
 * y = r sin(theta) sin(phi)
 * z = r cos(theta)
 * 
 * The field is represented as f(r,phi,theta) = A*r^a + B*phi^b + C*theta^c.
 * 
 * @author Jacob
 *
 */

public class SphericalScalarField extends AbstractScalarField {
	private final double A,B,C; // coefficients
	private final double a,b,c; // exponents
	
	/**
	 * Creates a scaler field of the form
	 * f(r,phi,theta) = A*r^a + B*phi^b + C*theta^c.
	 * @param coefficients the coefficients of the scaler field where the first,
	 * second, and third elements of the array represent A, B, and C respectively.
	 * Must be of length 3.
	 * @param exponents the exponents of the scaler field where the first,
	 * second, and third elements of the array represent a, b, and c respectively.
	 * Must be of length 3.
	 */
	public SphericalScalarField(double[] coefficients, double[] exponents) {
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
	
	/**
	 * Converts 3D Cartesian coordinates into 3D spherical coordinates (r,phi,theta).
	 * If the point is the origin than the theta/phi return value is NaN.
	 * @param cartesianCoords Cartesian coordinates for the point
	 * @return a {@link double[]} of length three corresponding to the coordinates (r,phi,theta).
	 */
	public static double[] getSphericalCoords(Vector3 cartesianCoords) {
		double r = cartesianCoords.mag();
		double theta = Math.acos(cartesianCoords.z / r);
		double phi = Math.atan(cartesianCoords.y / cartesianCoords.x);
		return new double[] {r, phi, theta};
	}
	
	public static Vector3 getCartesianCoords(double[] spherCoords) {
		if (spherCoords.length != 3) {
			throw new IllegalArgumentException("spherCoords must be of length 3.");
		}
		double x = spherCoords[0]*Math.sin(spherCoords[2])*Math.cos(spherCoords[1]);
		double y = spherCoords[0]*Math.sin(spherCoords[2])*Math.sin(spherCoords[1]);
		double z = spherCoords[0]*Math.cos(spherCoords[2]);
		return new Vector3(x, y, z);		
	}

	@Override
	public double getValue(Vector3 point) {
		double[] coords = getSphericalCoords(point);
		return Math.pow(A*coords[0], a) + Math.pow(B*coords[1], b) + Math.pow(C*coords[2], c);
	}

	@Override
	public Vector3 gradient(Vector3 point) {
		double[] coords = getSphericalCoords(point);
		double[] spherCoords = new double[] {dfdr(coords), dfdphi(coords) / (coords[0] * Math.sin(coords[2])), dfdtheta(coords) / coords[0]};
		return getCartesianCoords(spherCoords);
	}

	@Override
	public VectorField toVectorField() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to r at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdr(double[] spherCoords) {
		return Math.pow(a*A*spherCoords[0], a-1);
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to theta at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdtheta(double[] spherCoords) {		
		return Math.pow(c*C*spherCoords[2], c-1);
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to phi at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdphi(double[] spherCoords) {
		return Math.pow(b*B*spherCoords[1], b-1);
	}
	
	/**
	 * Calculates the derivative of r squared times the field with respect to r. d(r^2f)/dr
	 * @param spherCoords spherical coordinates
	 * @return the resulting value
	 */
	public double dr2fdr(double[] spherCoords) {
		return Math.pow((a+2)*A*spherCoords[0], a + 1);		
	}
	



}
