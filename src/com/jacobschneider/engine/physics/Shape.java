package com.jacobschneider.engine.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.math.geometry.Primitive;
import com.jacobschneider.engine.math.geometry.Segment;
import com.jacobschneider.engine.math.geometry.Primitives.Circle;
import com.jacobschneider.engine.math.geometry.Primitives.OpenCylinder;
import com.jacobschneider.engine.math.geometry.Primitives.Sphere;
import com.jacobschneider.engine.math.geometry.Primitives.Triangle;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.Collision.CollisionInterface;

/**
 * A set of primitives that creates the overall shape of a body.
 * 
 * @author Jacob
 *
 */
public class Shape implements Drawable {
	private final Primitive[] primitives; // unique set of primitives that make up the shape in body space
	private final Segment[] segments; // unique set of segments that make up the shape in body space
	
	/**
	 * Creates a shape from a List of primitive objects. All coordinates are in body frame. 
	 * The origin of this shape must be the center of mass of the shape.
	 * @param primitives A set of primitives that make up this shape
	 */
	public Shape(Set<Primitive> primitives) {
		this.primitives = primitives.toArray(new Primitive[0]);
		
		// removes duplicate segments
		Set<Segment> segments = new HashSet<Segment>();
		for (Primitive p : primitives) {
			segments.addAll(p.getSegments());
		}
		
		// removes erroneous segments
		// erroneous segments are segments that are apart of multiple primitives with the same normal
		// these segments are embedded in a plane and therefore cannot initiate a contact
		// an example is the diaganol of a square
		Map<Segment, Vector3> segToNormal = new HashMap<Segment, Vector3> ();
		for (Primitive p : primitives) {
			if (p.getNormal() == null) { // for primitives that do not have a defined normal skip entirely
				continue;
			}
			for (Segment s : p.getSegments()) {	
				Vector3 previousNormal = segToNormal.put(s,p.getNormal());
				if (previousNormal != null && previousNormal.equals(p.getNormal())) {	
					segments.remove(s);
				}
			}
		}		
		this.segments = segments.toArray(new Segment[0]);		
	}
	
	/**
	 * A copy constructor for {@link Shape}.
	 * @param shape The {@link Shape} to copy from.
	 */
	public Shape(Shape shape) {
		this.primitives = Arrays.copyOf(shape.primitives, shape.primitives.length);
		this.segments = Arrays.copyOf(shape.segments, shape.segments.length);
	}

	
	/**
	 * Creates a ball shape object.
	 * @param x position of the ball
	 * @param r radius of the ball
	 * @param rank The complexity of the ball. The higher the rank the more accurate the simulation but performance suffers.
	 * @return A shape object representing a ball
	 */
	public static Shape newBall(Vector3 x, double r, int rank) {
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new Sphere(Vector3.zero, r, rank));
		return new Shape(prims);
	}	
	
	/**
	 * Creates a cuboid shape object.
	 * @param x length
	 * @param y width
	 * @param z height
	 * @return A shape object representing a cuboid
	 */
	public static Shape newCuboid(double x, double y, double z) {
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
		
		return new Shape(prims);
	}
	
	/**
	 * Creates a flat shape of dimension x,y facing upwards towards z.
	 * @param x width of the wall
	 * @param y length of the wall
	 * @return The shape object representing a flat plane
	 */
	public static Shape newWall(double x, double y) {
		List<Vector3> verts = new ArrayList<Vector3>();
		verts.add(new Vector3(x / 2,y / 2,0));
		verts.add(new Vector3(-x / 2,y / 2,0));
		verts.add(new Vector3(-x / 2,-y / 2,0));
		verts.add(new Vector3(x / 2,-y / 2,0));
		
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new Triangle(verts.get(0),verts.get(1),verts.get(3)));
		prims.add(new Triangle(verts.get(1),verts.get(2),verts.get(3)));
		
		return new Shape(prims);
	}
	
	/**
	 * Create a 6 sided room in one shape. The normals of each wall
	 * are facing inward.
	 * @param x width of room
	 * @param y length of room
	 * @param z height of room
	 * @return The {@link Shape} object representing a room.
	 */
	public static Shape newRoom(double x, double y, double z) {
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
//		// ceiling
//		prims.add(new Triangle(verts.get(7),verts.get(5),verts.get(4)));
//		prims.add(new Triangle(verts.get(7),verts.get(6),verts.get(5)));
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
		return new Shape(prims);		
	}
	
	/**
	 * Creates a vertically oriented cylinder
	 * 
	 * @param radius the radius of the cylinder
	 * @param length the length of the cylinder
	 * @param rank the complexity of the shape. Higher numbers lead to more realistic collisions but worse performance. Recommended 10-100.
	 * @return The {@link Shape} object representing the cylinder.
	 */
	public static Shape newCylinder(double radius, double length, int rank) {
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
		return new Shape(prims);		
	}
	


	/**
	 * Detects all CollisionInterfaces (points and normals) of collision between this shape and another shape.
	 * @param other The other shape.
	 * @param thisBody The {@link PhysicsBody} attached to this shape's body
	 * @param otherBody The {@link PhysicsBody} attached to the other shape's body
	 * @return A list of collisions.
	 */
	public List<CollisionInterface> collisionDetect(Shape other, PhysicsBody thisBody, PhysicsBody otherBody) {
		List<CollisionInterface> contacts = new ArrayList<CollisionInterface>();
		contacts.addAll(collisionDetectInternal(other, thisBody, otherBody));
		// this next line is a tough one
		// without it => a small fixed object can pass through things
		// with it => performance suffers
		contacts.addAll(CollisionInterface.flipNormals(other.collisionDetectInternal(this, otherBody, thisBody))); 
		return contacts;
	}
	
	/**
	 * Checks where the segments of another body intersects the segments of this body. Normals go outward of primBody.
	 * @param segShape
	 * @param primBody
	 * @param segBody
	 * @return
	 */
	private List<CollisionInterface> collisionDetectInternal(Shape segShape, PhysicsBody primBody, PhysicsBody segBody) {
		List<CollisionInterface> inters = new ArrayList<CollisionInterface>();		
		for (Segment s : segShape.segments) {
			Segment sprime = primBody.toBodySpace(segBody.toWorldSpace(s)); // Segment s in this bodies frame
			for (Primitive p : this.primitives) {
				CollisionInterface c = p.intersectSegment(sprime);
				if (c != null) {
					Vector3 worldR = primBody.toWorldSpace(c.r);
					Vector3 worldN = c.n.multMatrixLeft(primBody.getR());
					CollisionInterface inter = new CollisionInterface(worldR, worldN);
					inters.add(inter);						
				}
			}			
		}		
		return inters;
	}
	
	@Override
	public void draw(GLAutoDrawable drawable) {
	    for (Primitive p : primitives) {
	    	if (p instanceof Drawable) {
	    		((Drawable) p).draw(drawable);
	    	}
	    }
	}
	

}
