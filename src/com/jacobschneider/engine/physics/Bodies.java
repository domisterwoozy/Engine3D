package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.List;

import com.jacobschneider.engine.framework.BoundVolume;
import com.jacobschneider.engine.framework.PhysicsBody;
import com.jacobschneider.engine.framework.Shape;
import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.boundingvolumes.BoundCircle;
import com.jacobschneider.engine.math.boundingvolumes.BoundSphere;
import com.jacobschneider.engine.physics.Body.Builder;

/**
 * Numerous static factory methods for creating {@link Body} objects.
 * 
 * @author Jacob
 *
 */
public class Bodies {	
	/**
	 * A completely custom body. Even excepts a custom implementation of {@link PhysicsBody}.
	 * @param physicsBody controls the physics
	 * @param shape geometric shape
	 * @param mat material of the body
	 * @param bound bound volume of the body
	 * @return the resulting {@link Body} object.
	 */
	public static Body customBody(PhysicsBody physicsBody, Shape shape, Material mat, BoundVolume bound) {
		return new Body(physicsBody, shape, mat, bound);
	}
	
	/**
	 * Creates a new Body object representing a solid ball.
	 * 
	 * @param centerPos position of the ball
	 * @param vel initial velocity
	 * @param radius radius of ball
	 * @param rank Complexity of the spherical shape. Recommended values from 10-100.
	 * @param mass mass of ball
	 * @return A Body object for the ball
	 */
	public static Body newBall(Vector3 centerPos, Vector3 vel, double radius, int rank, double mass) {
		double fudgeFactor = 10; // makes shit look better, doesnt spin as randomly, not physical in the slightest
		double I = (2.0/5.0) * mass * radius * radius * fudgeFactor;
		Matrix3 inertiaBody = Matrix3.IDENTITY.multScaler(I);
		BoundVolume boundingVolume = new BoundSphere(centerPos, radius);		
		RigidShape shape = Shapes.newBall(centerPos, radius, rank);
		Material mat = new Material(1.0, 1.0, 1.0); // extra bouncy material (79 is approximately elastic with a 1.0 object)
		Builder builder = new Builder(centerPos, mass, shape);
		return builder.inertiaTensor(inertiaBody).material(mat).boundVolume(boundingVolume).initialVelocity(vel).build();
	}
	
	/**
	 * Creates a new Body object representing a solid box/cuboid.
	 * 
	 * @param centerPos initial position
	 * @param velocity initial velocity of cuboid
	 * @param w thickness in x direction
	 * @param h thickness in y direction
	 * @param d thickness in z direction
	 * @param mass mass of cuboid
	 * @return A Body object for the cuboid
	 */
	public static Body newCuboid(Vector3 centerPos, Vector3 velocity, double w, double h, double d, double mass) {		
		double[] inertiaBody = new double[] {(1.0/12.0)*mass*(h*h + d*d),0,0,
										    0,(1.0/12.0)*mass*(w*w + d*d),0,
										    0,0,(1.0/12.0)*mass*(h*h + w*w)};
		BoundVolume boundingVolume = new BoundSphere(centerPos, Math.sqrt(w*w+h*h+d*d));
		RigidShape shape = Shapes.newCuboid(w/2, h/2, d/2);
		Builder builder = new Builder(centerPos, mass, shape);
		return builder.inertiaTensor(Matrix3.fromColArray(inertiaBody)).boundVolume(boundingVolume).initialVelocity(velocity).build();
	}
	
	/**
	 * Creates a wall. This is a fixed planar rectangle.
	 * 
	 * @param centerPos initial position of the center of the wall
	 * @param dirArr direction the wall faces
	 * @param w width of wall
	 * @param h height of wall
	 * @return the wall Body object
	 */
	public static Body newWall(Vector3 centerPos, Vector3 dirArr, double w, double h) {
		// initial orientation quaternion	
		Vector3 dir0 = new Vector3(0,0,1); // original direction pointed up
		Vector3 rotDir = dir0.cross(dirArr); // direction to rotate around
		if (rotDir.equals(Vector3.zero)) {
			rotDir = new Vector3(1,0,0);
		}
		double cosTheta = dirArr.dot(dir0) / (dirArr.mag() * dir0.mag());
		Quaternion q0 = Quaternion.newQuaternion(Math.acos(cosTheta), rotDir);		
		
		BoundVolume boundVol = new BoundCircle(dirArr, centerPos, Math.sqrt(w*w + h*h) / 2);
		RigidShape shape = Shapes.newWall(w, h);
		Builder builder = new Builder(centerPos, 1, shape);
		Body b = builder.initialRotation(q0).boundVolume(boundVol).fixBody().build();
		return b;
	}
	
	/**
	 * Creates a room with 5 walls (no ceiling). Each wall is a separate {@link Body} object.
	 * 
	 * @param centerPos center point of the room
	 * @param length x direction size
	 * @param width y direction size
	 * @param height z direction size
	 * @return a List of {@link Body} objects for each wall of the room
	 */
	public static List<Body> newRoom(Vector3 centerPos, double length, double width, double height) {
		double spacing = 0.000000001; // so walls dont intersect
		Body wall1 = Bodies.newWall(centerPos.add(new Vector3(0, 0, -height / 2)), Vector3.k, length - spacing, width - spacing);
		Body wall2 = Bodies.newWall(centerPos.subtract(new Vector3(-length / 2, 0, 0)), Vector3.i.inverse(), height - spacing, width - spacing);
		Body wall3 = Bodies.newWall(centerPos.subtract(new Vector3(0, -width / 2, 0)), Vector3.j.inverse(), length - spacing, height - spacing);
		Body wall4 = Bodies.newWall(centerPos.subtract(new Vector3(0, width / 2, 0)), Vector3.j, length - spacing, height - spacing);
		Body wall5 = Bodies.newWall(centerPos.subtract(new Vector3(length / 2, 0, 0)), Vector3.i, height - spacing, width - spacing);
		List<Body> bodies = new ArrayList<Body>();
		bodies.add(wall1);
		bodies.add(wall2);
		bodies.add(wall3);
		bodies.add(wall4);
		bodies.add(wall5);
		return bodies;
	}

}
