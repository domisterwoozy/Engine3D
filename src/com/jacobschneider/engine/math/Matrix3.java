package com.jacobschneider.engine.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable 3x3 matrix class
 * @author Jacob
 *
 */
public final class Matrix3 {
	/**
	 * A 3x3 matrix with all diagonal elements equal to {@link Double#POSITIVE_INFINITY}
	 */
	public final static Matrix3 INFINITY = diagMat(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	/**
	 * The 3x3 identity matrix.
	 */
	public final static Matrix3 IDENTITY = new Matrix3(1,0,0,0,1,0,0,0,1);
	/**
	 * The 3x3 null matrix. All elements are zero.
	 */
	public final static Matrix3 ZERO = new Matrix3(0,0,0,0,0,0,0,0,0);
	
	private static double DOUBLE_EQUALITY_TOLERANCE = Math.pow(10, -10);
	
	// row 1
	public final double xx;
	public final double xy;
	public final double xz;
	// row 2
	public final double yx;
	public final double yy;
	public final double yz;
	// row 3
	public final double zx;
	public final double zy;
	public final double zz;	
	
	public Matrix3(double xx, double xy, double xz, double yx, double yy, double yz, double zx, double zy, double zz) {
		this.xx = xx;
		this.xy = xy;
		this.xz = xz;
		this.yx = yx;
		this.yy = yy;
		this.yz = yz;
		this.zx = zx;
		this.zy = zy;
		this.zz = zz;
	}
	
	public static Matrix3 diagMat(double xx, double yy, double zz) {
		return new Matrix3(xx, 0, 0, 0, yy, 0, 0, 0, zz);
	}
	
	public static Matrix3 fromColArray(double[] colArr) {
		return new Matrix3(colArr[0], colArr[3], colArr[6], colArr[1], colArr[4], colArr[7], colArr[2], colArr[5], colArr[8]);
	}
	
	public static Matrix3 fromRowArray(double[] rowArr) {
		throw new IllegalStateException("Not implemented");
	}
	
	public static Matrix3 fromRowVectors(Vector3 row1, Vector3 row2, Vector3 row3) {
		return new Matrix3(row1.x, row1.y, row1.z, row2.x, row2.y, row2.z, row3.x, row3.y, row3.z);
	}
	
	public static Matrix3 fromColVectors(Vector3 row1, Vector3 row2, Vector3 row3) {
		return new Matrix3(row1.x, row2.x, row3.x, row1.y, row2.y, row3.y, row1.z, row2.z, row3.z);
	}
	
	public List<Vector3> toRowVectors() {
		List<Vector3> rowVects = new ArrayList<Vector3>();
		rowVects.add(new Vector3(xx, xy, xz));
		rowVects.add(new Vector3(yx, yy, yz));
		rowVects.add(new Vector3(zx, zy, zz));
		return rowVects;
	}
	
	public List<Vector3> toColVectors() {
		List<Vector3> colVects = new ArrayList<Vector3>();
		colVects.add(new Vector3(xx, yx, zx));
		colVects.add(new Vector3(xy, yy, zy));
		colVects.add(new Vector3(xz, yz, zz));
		return colVects;
	}
	
	public Matrix3 multScaler(double scaler) {
		return new Matrix3(this.xx * scaler, this.xy * scaler, this.xz * scaler,
				this.yx * scaler, this.yy * scaler, this.yz * scaler,
				this.zx * scaler, this.zy * scaler, this.zz * scaler);
	}
	
	public Matrix3 add(Matrix3 m) {
		return new Matrix3(this.xx + m.xx, this.xy + m.xy, this.xz + m.xz,
				this.yx + m.yx, this.yy + m.yy, this.yz + m.yz,
				this.zx + m.zx, this.zy + m.zy, this.zz + m.zz);
		
	}
	
	public Matrix3 subtract(Matrix3 rhs) {
		return new Matrix3(this.xx - rhs.xx, this.xy - rhs.xy, this.xz - rhs.xz,
				this.yx - rhs.yx, this.yy - rhs.yy, this.yz - rhs.yz,
				this.zx - rhs.zx, this.zy - rhs.zy, this.zz - rhs.zz);
		
	}
	
	public Matrix3 multMatrix(Matrix3 rhs) {
		return new Matrix3(
		// row 1
		this.xx * rhs.xx + this.xy * rhs.yx + this.xz * rhs.zx,
		this.xx * rhs.xy + this.xy * rhs.yy + this.xz * rhs.zy,
		this.xx * rhs.xz + this.xy * rhs.yz + this.xz * rhs.zz,
		// row 2
		this.yx * rhs.xx + this.yy * rhs.yx + this.yz * rhs.zx,
		this.yx * rhs.xy + this.yy * rhs.yy + this.yz * rhs.zy,
		this.yx * rhs.xz + this.yy * rhs.yz + this.yz * rhs.zz,
		// row 3
		this.zx * rhs.xx + this.zy * rhs.yx + this.zz * rhs.zx,
		this.zx * rhs.xy + this.zy * rhs.yy + this.zz * rhs.zy,
		this.zx * rhs.xz + this.zy * rhs.yz + this.zz * rhs.zz);
	}
	
	public Matrix3 invert() {		
		double factor = determinant();
		if (factor == 0) {
			throw new IllegalStateException("Singular matrix cannot be inverted");
		}
		return new Matrix3(		
		(this.yy*this.zz - this.yz*this.zy)/factor,
		(this.xz*this.zy - this.xy*this.zz)/factor,
		(this.xy*this.yz - this.xz*this.yy)/factor,
		(this.yz*this.zx - this.yx*this.zz)/factor,
		(this.xx*this.zz - this.xz*this.zx)/factor,
		(this.xz*this.yx - this.xx*this.yz)/factor,
		(this.yx*this.zy - this.yy*this.zx)/factor,
		(this.xy*this.zx - this.xx*this.zy)/factor,
		(this.xx*this.yy - this.xy*this.yx)/factor);
	}
	
	public boolean isOrthoganol() {
		return (Math.abs(determinant()-1) <= DOUBLE_EQUALITY_TOLERANCE);
	}
	
	public Matrix3 rotate(Matrix3 rotationMatrix) {
		if (rotationMatrix.isOrthoganol() == false) {
			throw new IllegalArgumentException("rotationMatrix must be orthoganol");
		}
		return rotationMatrix.multMatrix(this).multMatrix(rotationMatrix.transpose());
	}
	
	public Matrix3 rotate(Quaternion q) {
		return rotate(q.toMatrix());
	}
	
	/**
	 * Finds the magnitude of this matrix in a certain direction.
	 * For example this will determine the scaler moment of inertia around an axis
	 * for an inertia tensor.
	 * @param dir Direction to calculate magnitude in
	 * @return A scaler value representing the magnitude
	 */
	public double mag(Vector3 dir) {
		dir = dir.normalize();
		return dir.multMatrixLeft(this).dot(dir);
	}
	
	public Matrix3 transpose() {
		return new Matrix3(this.xx, this.yx, this.zx, this.xy, this.yy, this.zy, this.xz, this.yz, this.zz);
	}
	
	/**
	 * The vector result of multiplying a vector times this matrix. Where the vector is on the left
	 * and the matrix is on the right.
	 * @param lhs Vector multiplied on the left of this matrix
	 * @return The resulting matrix
	 */
	public Vector3 vectMultLeft(Vector3 lhs) {
		throw new IllegalStateException("Unimplemented");
	}
	
	/**
	 * The 3-vector result of multiplying this matrix times a vector. Where the matrix is on the left
	 * and the vector is on the right.
	 * @param rhs Vector multiplied on the right of this matrix
	 * @return The resulting matrix
	 */
	public Vector3 multVectRight(Vector3 rhs) {
		return rhs.multMatrixLeft(this);
	}
	
	public double determinant() {		
		return (this.xx*this.yy*this.zz + this.xy*this.yz*this.zx + this.xz*this.yx*this.zy) - 
				(this.xz*this.yy*this.zx + this.xy*this.yx*this.zz + this.xx*this.yz*this.zy);
	}
	
	public double[] toColArray() {
		return new double[] {this.xx, this.yx, this.zx, this.xy, this.yy, this.zy, this.xz, this.yz, this.zz};
	}
	
	public String toString() {
		return "Row 1: " + this.xx + ", " + this.xy + ", " + this.xz + "\n" +
				"Row 2: " + this.yx + ", " + this.yy + ", " + this.yz + "\n" +
				"Row 3: " + this.zx + ", " + this.zy + ", " + this.zz + "\n";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Matrix3)) {
			return false;
		}
		Matrix3 otherMat = (Matrix3) o;
		if (otherMat == this) {
			return true;
		}
		if (Double.doubleToLongBits(this.xx) != Double.doubleToLongBits(otherMat.xx)) {
		return false;
		}
		if (Double.doubleToLongBits(this.yy) != Double.doubleToLongBits(otherMat.yy)) {
			return false;
		}
		if (Double.doubleToLongBits(this.zz) != Double.doubleToLongBits(otherMat.zz)) {
			return false;
		}
		if (Double.doubleToLongBits(this.xy) != Double.doubleToLongBits(otherMat.xy)) {
			return false;
		}
		if (Double.doubleToLongBits(this.yx) != Double.doubleToLongBits(otherMat.yx)) {
			return false;
		}
		if (Double.doubleToLongBits(this.zx) != Double.doubleToLongBits(otherMat.zx)) {
			return false;
		}
		if (Double.doubleToLongBits(this.xz) != Double.doubleToLongBits(otherMat.xz)) {
			return false;
		}
		if (Double.doubleToLongBits(this.yz) != Double.doubleToLongBits(otherMat.yz)) {
			return false;
		}
		if (Double.doubleToLongBits(this.zy) != Double.doubleToLongBits(otherMat.zy)) {
			return false;
		}
		return true;		
	}
	
	@Override
	public int hashCode() {		
		int result = 17;
		result = 31 * result + (int)Double.doubleToLongBits(this.xx);
		result = 31 * result + (int)Double.doubleToLongBits(this.yy);
		result = 31 * result + (int)Double.doubleToLongBits(this.zz);
		result = 31 * result + (int)Double.doubleToLongBits(this.xy);
		result = 31 * result + (int)Double.doubleToLongBits(this.yx);
		result = 31 * result + (int)Double.doubleToLongBits(this.zx);
		result = 31 * result + (int)Double.doubleToLongBits(this.xz);
		result = 31 * result + (int)Double.doubleToLongBits(this.yz);
		result = 31 * result + (int)Double.doubleToLongBits(this.zy);
		return result;
	}
	
	

}
