package com.jacobschneider.engine.physics;

/**
 * A class the holds the surface properties of an object.
 * 
 * @author Jacob
 *
 */
public class Material {	
	/**
	 * A default Material object where all constants are equal to 1.0.
	 */
	public static final Material defaultMaterial = new Material(1.0,1.0,1.0);
	
	private final double epsilon; // bounciness
	private final double dynamicFric; // slidiness
	private final double staticFric; // stand stilliness
	
	
	/**
	 * 
	 * @param epsilon This is the bounciness of the body. A epsilon of 79 will result in an approximately elastic collision
	 * between this body and another body if that other body is fixed and has an epsilon of 1.0.
	 * @param dynamicFric This is how difficult it is to get the body to slide.
	 * @param staticFric This is how difficult it is to get the body sliding from rest
	 */
	public Material(double epsilon, double dynamicFric, double staticFric) {
		this.epsilon = epsilon;
		this.dynamicFric = dynamicFric;
		this.staticFric = staticFric;		
	}	
	
	/**
	 * @return The double that represents the bounciness of the body.
	 * 
	 */
	public double getEpsilon() {
		return epsilon;
	}
	
	/**
	 * @return A double that represents how difficult it is to get the body to slide.
	 */
	public double getDynamicFric() {
		return dynamicFric;
	}
	
	/**
	 * @return A double that represents how difficult it is to get the body sliding from rest
	 */
	public double getStaticFric() {
		return staticFric;
	}

}
