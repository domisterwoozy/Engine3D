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
import com.jacobschneider.engine.framework.PhysicsBody;
import com.jacobschneider.engine.framework.Primitive;
import com.jacobschneider.engine.framework.Segment;
import com.jacobschneider.engine.framework.Shape;
import com.jacobschneider.engine.math.Vector3;

/**
 * A set of primitives that creates the overall shape of a body.
 * 
 * @author Jacob
 *
 */
public class RigidShape implements Drawable, Shape {
	private final Primitive[] primitives; // unique set of primitives that make up the shape in body space
	private final Segment[] segments; // unique set of segments that make up the shape in body space
	
	/**
	 * Creates a shape from a List of primitive objects. All coordinates are in body frame. 
	 * The origin of this shape must be the center of mass of the shape.
	 * @param primitives A set of primitives that make up this shape
	 */
	public RigidShape(Set<Primitive> primitives) {
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
	 * A copy constructor for {@link RigidShape}.
	 * @param shape The {@link RigidShape} to copy from.
	 */
	public RigidShape(RigidShape shape) {
		this.primitives = Arrays.copyOf(shape.primitives, shape.primitives.length);
		this.segments = Arrays.copyOf(shape.segments, shape.segments.length);
	}
	
	@Override
	public Primitive[] getPrimitives() {
		return Arrays.copyOf(primitives, primitives.length);
	}

	@Override
	public Segment[] getSegments() {
		return Arrays.copyOf(segments, segments.length);
	}

	/**
	 * Detects all CollisionInterfaces (points and normals) of collision between this shape and another shape.
	 * @param other The other shape.
	 * @param thisBody The {@link PhysicsBody} attached to this shape's body
	 * @param otherBody The {@link PhysicsBody} attached to the other shape's body
	 * @return A list of collisions.
	 */
	@Override
	public List<CollisionInterface> collisionDetect(Shape other, PhysicsBody thisBody, PhysicsBody otherBody) {
		List<CollisionInterface> contacts = new ArrayList<CollisionInterface>();
		contacts.addAll(collisionDetectInternal(other, this, thisBody, otherBody));
		// this next line is a tough one
		// without it => a small fixed object can pass through things
		// with it => performance suffers
		contacts.addAll(CollisionInterface.flipNormals(RigidShape.collisionDetectInternal(this, other, otherBody, thisBody))); 
		return contacts;
	}
	
	/**
	 * Checks where the segments of another body intersects the segments of this body. Normals go outward of primBody.
	 * @param segShape
	 * @param primBody
	 * @param segBody
	 * @return
	 */
	private static List<CollisionInterface> collisionDetectInternal(Shape segShape, Shape primShape, PhysicsBody primBody, PhysicsBody segBody) {
		List<CollisionInterface> inters = new ArrayList<CollisionInterface>();		
		for (Segment s : segShape.getSegments()) {
			Segment sprime = primBody.toBodySpace(segBody.toWorldSpace(s)); // Segment s in this bodies frame
			for (Primitive p : primShape.getPrimitives()) {
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
