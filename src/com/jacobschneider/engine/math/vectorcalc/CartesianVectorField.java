package com.jacobschneider.engine.math.vectorcalc;

import com.jacobschneider.engine.math.Vector3;

public class CartesianVectorField extends AbstractVectorField {
	public final CartesianScalarField x;
	public final CartesianScalarField y;
	public final CartesianScalarField z;
	
	public CartesianVectorField(CartesianScalarField xComp, CartesianScalarField yComp, CartesianScalarField zComp) {
		this.x = xComp;
		this.y = yComp;
		this.z = zComp;		
	}

	@Override
	public Vector3 getValue(Vector3 point) {
		return new Vector3(x.getValue(point), y.getValue(point), z.getValue(point));
	}

	@Override
	public Vector3 curl(Vector3 point) {
		return new Vector3(z.dfdy(point) - y.dfdz(point),
							x.dfdz(point) - z.dfdx(point),
							y.dfdx(point) - x.dfdy(point));
	}

	@Override
	public double divergence(Vector3 point) {
		return x.dfdx(point) + y.dfdy(point) + z.dfdz(point);
	}

}
