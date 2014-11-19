package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.framework.ScalarField;

/**
 * A collection of static factory methods to create scalar fields that represent physical potentials.
 * 
 * @author Jacob
 *
 */
public class Potentials {
	
	/**
	 * Creates a scalar potential representing an inverse square force law.
	 * For example entering inverseSquare(G*m) where G = gravitational constant and m is the mass of an object
	 * will result in the scalar field representing the gravitational potential created by the mass of the object.
	 * @param strength the strength of the scaler field
	 * @return the resulting inverse square potential field
	 */
	public static ScalarField inverseSquare(double strength) {
		return centralForce(strength, -1);
	}
	
	/**
	 * Creates a generic scalar potential representing a center seeking force equal to strength * r^(power).
	 * If strength and power have opposite signs the force will be towards the center else the force will be away from the center.
	 * @param strength the strength of the scalar field
	 * @param power how the field varies with increased distance
	 * @return the resulting central force potential field
	 */
	public static ScalarField centralForce(double strength, int power) {
		return new SphericalScalarField(new double[] {-strength, 0, 0}, new double[] {power, -2, -2});
	}
	

}
