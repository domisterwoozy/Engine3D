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
	private static final int NULL_EXPONENT = 10; // when the coefficient is zero the exponent does not matter as long as it does not equal 0 or -1
	
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
		for (double d : exponents) {
			if (d == 1.0 || d == 0.0) {
				throw new IllegalArgumentException("You cannot currently enter an exponent equal to one or zero");
			}
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
		double phi = Math.atan2(cartesianCoords.y, cartesianCoords.x);
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
		System.out.println("*******");
		System.out.println(B);
		System.out.println(b);
		System.out.println(Math.pow(coords[1], b));
		System.out.println("*******");
		return A * Math.pow(coords[0], a) + B * Math.pow(coords[1], b) + C * Math.pow(coords[2], c);
	}

	@Override
	public Vector3 gradient(Vector3 point) {
		double[] coords = getSphericalCoords(point);
		double[] spherCoords = new double[] {dfdr(coords), dfdphi(coords) / (coords[0] * Math.sin(coords[2])), dfdtheta(coords) / coords[0]};
		return getCartesianCoords(spherCoords);
	}

	@Override
	public VectorField toVectorField() {
		return new SphericalVectorField(new SphericalScalarField(new double[] {-a * A, 0, 0}, new double[] {a - 1, NULL_EXPONENT, NULL_EXPONENT}),
				new SphericalScalarField(new double[] {NULL_EXPONENT, -b * B, NULL_EXPONENT}, new double[] {NULL_EXPONENT, b - 1, NULL_EXPONENT}),
				new SphericalScalarField(new double[] {NULL_EXPONENT, NULL_EXPONENT, -c * C}, new double[] {NULL_EXPONENT, NULL_EXPONENT, c - 1}));
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to r at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdr(double[] spherCoords) {
		return a * A * Math.pow(spherCoords[0], a-1);
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to theta at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdtheta(double[] spherCoords) {		
		return c * C * Math.pow(spherCoords[2], c-1);
	}
	
	/**
	 * Calculates the derivative of the scaler field with respect to phi at a point.
	 * @param spherCoords the spherical coordinates to calculate at
	 * @return the resulting derivative
	 */
	public double dfdphi(double[] spherCoords) {
		return b * B * Math.pow(spherCoords[1], b-1);
	}
	
	/**
	 * Calculates the derivative of r times the field with respect to r. d(rf)/dr
	 * @param spherCoords spherical coordinates
	 * @return the resulting value
	 */
	public double drfdr(double[] spherCoords) {
		return (a+1) * A * Math.pow(spherCoords[0], a);		
	}
	
	/**
	 * Calculates the derivative of r squared times the field with respect to r. d(r^2f)/dr
	 * @param spherCoords spherical coordinates
	 * @return the resulting value
	 */
	public double dr2fdr(double[] spherCoords) {
		return (a+2) * A * Math.pow(spherCoords[0], a + 1);		
	}
	
	/**
	 * Calculates the derivative of sin(theta) times the field with respect to theta. d(sin(theta)f)/dtheta
	 * @param spherCoords spherical coordinates
	 * @return the resulting value
	 */
	public double dsinthetafdtheta(double[] spherCoords) {
		double cosTheta = Math.cos(spherCoords[2]);
		return A * cosTheta * Math.pow(spherCoords[0], a) + B * cosTheta * Math.pow(spherCoords[1], b) + 
				C * cosTheta * Math.pow(spherCoords[2], c) + c * C * Math.sin(spherCoords[2]) * Math.pow(spherCoords[2], c - 1);
	}
	



}
