package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.math.Vector3;

public class SphericalVectorField extends AbstractVectorField {
	
	public final SphericalScalarField x;
	public final SphericalScalarField y;
	public final SphericalScalarField z;
	
	public SphericalVectorField(SphericalScalarField xComp, SphericalScalarField yComp, SphericalScalarField zComp) {
		this.x = xComp;
		this.y = yComp;
		this.z = zComp;		
	}

	@Override
	public Vector3 getValue(Vector3 point) {
		System.out.println(point);
		System.out.println(SphericalScalarField.getSphericalCoords(point)[0]);
		System.out.println(SphericalScalarField.getSphericalCoords(point)[1]);
		System.out.println(SphericalScalarField.getSphericalCoords(point)[2]);
		System.out.println(new Vector3(x.getValue(point), y.getValue(point), z.getValue(point)));
		System.out.println(SphericalScalarField.getCartesianCoords(new double[] {x.getValue(point), y.getValue(point), z.getValue(point)}));
		throw new IllegalStateException();
		//return new Vector3(x.getValue(point), y.getValue(point), z.getValue(point));
	}

	@Override
	public Vector3 curl(Vector3 point) {
		double[] spherCoords = SphericalScalarField.getSphericalCoords(point);
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public double divergence(Vector3 point) {
		double[] spherCoords = SphericalScalarField.getSphericalCoords(point);
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
