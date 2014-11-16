package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jacobschneider.engine.framework.Primitive;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.geometry.Primitives.Circle;
import com.jacobschneider.engine.math.geometry.Primitives.OpenCylinder;
import com.jacobschneider.engine.math.geometry.Primitives.Sphere;
import com.jacobschneider.engine.math.geometry.Primitives.Triangle;


public class Shapes {
	/**
	 * Creates a ball shape object.
	 * @param x position of the ball
	 * @param r radius of the ball
	 * @param rank The complexity of the ball. The higher the rank the more accurate the simulation but performance suffers.
	 * @return A shape object representing a ball
	 */
	public static RigidShape newBall(Vector3 x, double r, int rank) {
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new Sphere(Vector3.zero, r, rank));
		return new RigidShape(prims);
	}	
	
	/**
	 * Creates a cuboid shape object.
	 * @param x length
	 * @param y width
	 * @param z height
	 * @return A shape object representing a cuboid
	 */
	public static RigidShape newCuboid(double x, double y, double z) {
		List<Vector3> verts = new ArrayList<Vector3>();
		verts.add(new Vector3(x,y,z));
		verts.add(new Vector3(-x,y,z));
		verts.add(new Vector3(-x,-y,z));
		verts.add(new Vector3(x,-y,z));
		verts.add(new Vector3(x,y,-z));
		verts.add(new Vector3(-x,y,-z));
		verts.add(new Vector3(-x,-y,-z));
		verts.add(new Vector3(x,-y,-z));
		
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new Triangle(verts.get(0),verts.get(1),verts.get(2)));
		prims.add(new Triangle(verts.get(0),verts.get(2),verts.get(3)));
		prims.add(new Triangle(verts.get(4),verts.get(6),verts.get(5)));
		prims.add(new Triangle(verts.get(4),verts.get(7),verts.get(6)));
		prims.add(new Triangle(verts.get(2),verts.get(7),verts.get(3)));
		prims.add(new Triangle(verts.get(2),verts.get(6),verts.get(7)));
		prims.add(new Triangle(verts.get(0),verts.get(5),verts.get(1)));
		prims.add(new Triangle(verts.get(0),verts.get(4),verts.get(5)));
		prims.add(new Triangle(verts.get(0),verts.get(7),verts.get(4)));
		prims.add(new Triangle(verts.get(0),verts.get(3),verts.get(7)));
		prims.add(new Triangle(verts.get(1),verts.get(6),verts.get(2)));
		prims.add(new Triangle(verts.get(1),verts.get(5),verts.get(6)));
		
		return new RigidShape(prims);
	}
	
	/**
	 * Creates a flat shape of dimension x,y facing upwards towards z.
	 * @param x width of the wall
	 * @param y length of the wall
	 * @return The shape object representing a flat plane
	 */
	public static RigidShape newWall(double x, double y) {
		List<Vector3> verts = new ArrayList<Vector3>();
		verts.add(new Vector3(x / 2,y / 2,0));
		verts.add(new Vector3(-x / 2,y / 2,0));
		verts.add(new Vector3(-x / 2,-y / 2,0));
		verts.add(new Vector3(x / 2,-y / 2,0));
		
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new Triangle(verts.get(0),verts.get(1),verts.get(3)));
		prims.add(new Triangle(verts.get(1),verts.get(2),verts.get(3)));
		
		return new RigidShape(prims);
	}
	
	/**
	 * Create a 6 sided room in one shape. The normals of each wall
	 * are facing inward.
	 * @param x width of room
	 * @param y length of room
	 * @param z height of room
	 * @return The {@link RigidShape} object representing a room.
	 */
	public static RigidShape newRoom(double x, double y, double z) {
		List<Vector3> verts = new ArrayList<Vector3>();
		verts.add(new Vector3(x / 2,y / 2, -z / 2)); // 0
		verts.add(new Vector3(-x / 2,y / 2, -z / 2)); // 1
		verts.add(new Vector3(-x / 2,-y / 2, -z / 2)); // 2
		verts.add(new Vector3(x / 2,-y / 2, -z / 2)); // 3
		
		verts.add(new Vector3(x / 2,y / 2, z / 2)); // 4
		verts.add(new Vector3(-x / 2,y / 2, z / 2)); // 5
		verts.add(new Vector3(-x / 2,-y / 2, z / 2)); // 6
		verts.add(new Vector3(x / 2,-y / 2, z / 2)); // 7
		
		
		Set<Primitive> prims = new HashSet<Primitive>();
		// floor
		prims.add(new Triangle(verts.get(0),verts.get(1),verts.get(3)));
		prims.add(new Triangle(verts.get(1),verts.get(2),verts.get(3)));
		// ceiling
		prims.add(new Triangle(verts.get(7),verts.get(5),verts.get(4)));
		prims.add(new Triangle(verts.get(7),verts.get(6),verts.get(5)));
		// back wall
		prims.add(new Triangle(verts.get(0),verts.get(5),verts.get(4)));
		prims.add(new Triangle(verts.get(0),verts.get(1),verts.get(5)));
		// front wall
		prims.add(new Triangle(verts.get(2),verts.get(6),verts.get(7)));
		prims.add(new Triangle(verts.get(2),verts.get(7),verts.get(3)));
		// left wall
		prims.add(new Triangle(verts.get(2),verts.get(5),verts.get(6)));
		prims.add(new Triangle(verts.get(2),verts.get(1),verts.get(5)));
		// right wall
		prims.add(new Triangle(verts.get(0),verts.get(3),verts.get(7)));
		prims.add(new Triangle(verts.get(0),verts.get(7),verts.get(4)));		
		return new RigidShape(prims);		
	}
	
	/**
	 * Creates a vertically oriented cylinder
	 * 
	 * @param radius the radius of the cylinder
	 * @param length the length of the cylinder
	 * @param rank the complexity of the shape. Higher numbers lead to more realistic collisions but worse performance. Recommended 10-100.
	 * @return The {@link RigidShape} object representing the cylinder.
	 */
	public static RigidShape newCylinder(double radius, double length, int rank) {
		Set<Primitive> prims = new HashSet<Primitive>();
		// top
		Primitive top = new Circle(new Vector3(0, 0, length / 2), Vector3.k, radius, rank);
		// bottom
		Primitive bottom = new Circle(new Vector3(0, 0, -length / 2), Vector3.k.inverse(), radius, rank);
		// cylinder
		Primitive cyl = new OpenCylinder(Vector3.zero, Vector3.k, length, radius, rank);
		
		prims.add(top);
		prims.add(bottom);
		prims.add(cyl);		
		return new RigidShape(prims);		
	}

}
