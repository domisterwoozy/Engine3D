package com.jacobschneider.engine.math.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.framework.Primitive;
import com.jacobschneider.engine.framework.Segment;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.CollisionInterface;


public class Primitives {	
	/**
	 * A primitive implementation that represents a triangle in 3d space.
	 * @author Jacob
	 *
	 */
	public static class Triangle implements Primitive,Drawable {
		public final Vector3 a,b,c,ab,ac,bc; // points and segments
		private final Vector3 n; // normal of triangle plane
		private final Vector3 abn,acn,bcn; // normals of side planes of triangle
		
		/**
		 * The three points that make up a triangle in body space.
		 * Normal is pointing up and points are assigned counter clockwise.
		 * @param a first point
		 * @param b second point
		 * @param c third point
		 */
		public Triangle(Vector3 a, Vector3 b, Vector3 c) {
			this.a = a;
			this.b = b;
			this.c = c;
			
			this.ab = b.subtract(a);
			this.ac = c.subtract(a);
			this.bc = c.subtract(b);
			
			this.n = ab.cross(ac).normalize();
			
			this.abn = ab.cross(n);
			this.acn = n.cross(ac);
			this.bcn = bc.cross(n);	
		}
		
		@Override
		public CollisionInterface intersectSegment(Segment sBody) {	
			Vector3 pBody = sBody.a;
			Vector3 qBody = sBody.b;
			Vector3 pq = qBody.subtract(pBody);
			double d = pq.dot(n); // abs(d) distance between p and q perpendicular to triangle plane
			if (d == 0) return null; // pq is parallel to the plane	
			//System.out.println("d: " + d);
			
			Vector3 ap = a.subtract(pBody);
			double t = ap.dot(n); // perpindicular distance from triangle plane to point p
			//System.out.println("t: " + t);
			if (t*d < 0) return null; // distance spanned is in wrong direction to plane
			if (Math.abs(t) > Math.abs(d)) return null; // not even intersecting the triangle plane
			
			Vector3 r = pBody.add(pq.multScaler(t / d)); // point of intersection with triangle plane
			
			Vector3 rx = a.subtract(r);
			double dist = rx.dot(abn); // distance between intersection point and segment x.
			if (dist < 0) return null; // outside of segment ab
			
			rx = b.subtract(r);				
			dist = rx.dot(bcn);
			if (dist < 0) return null; // outside of segment bc
			
			rx = c.subtract(r);
			dist = rx.dot(acn);
			if (dist < 0) return null; // outside of segment ac					
		
			return new CollisionInterface(r, n);			
		}

		@Override
		public List<Segment> getSegments() {
			List<Segment> segments = new ArrayList<Segment>();
			segments.add(new Segment(a,b));
			segments.add(new Segment(a,c));
			segments.add(new Segment(b,c));
			return segments;
		}

		@Override
		public Vector3 getNormal() {
			return n;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Triangle)) {
				return false;
			}
			Triangle otherTri = (Triangle) o;
			if (otherTri == this) {
				return true;
			}
			if (!(a.equals(otherTri.a) || a.equals(otherTri.b) || a.equals(otherTri.c))) {
				return false;
			}
			if (!(b.equals(otherTri.a) || b.equals(otherTri.b) || b.equals(otherTri.c))) {
				return false;
			}
			if (!(c.equals(otherTri.a) || c.equals(otherTri.b) || c.equals(otherTri.c))) {
				return false;
			}
			return true;	
		}
		@Override
		public int hashCode() {
			return a.hashCode() + b.hashCode() + c.hashCode(); // order does not matter
		}

		@Override
		public void draw(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f); // Red
			gl.glVertex3dv(a.toArray(), 0);
			gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); // Green
			gl.glVertex3dv(b.toArray(), 0);
			gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f); // Blue
			gl.glVertex3dv(c.toArray(), 0);
			gl.glEnd(); 			
		}
	}

	/**
	 * A primitive implementation that represents a circle in 3d space.
	 * This implementation accepts a rank parameter where rank is equal to the
	 * number of segments returned by {@link #getSegments()}.
	 * @author Jacob
	 *
	 */
	public static class Circle implements Primitive,Drawable {
		private final Vector3 centerPos;
		private final Vector3 n;
		private final double radius;
		private final int numSegments;
		private final List<Segment> segs = new ArrayList<Segment>();
		
		public Circle(Vector3 centerPos, Vector3 n, double radius, int numSegments) {
			this.centerPos = centerPos;
			this.n = n;
			this.radius = radius;
			this.numSegments = numSegments;
			
			createSegments();
		}
		
		private void createSegments() {
			Quaternion rotation = Vector3.k.rotationRequired(n); // rotation neccesary to rotate world z up to the normal of the circle
			for (int i = 0; i < numSegments; i++) {
				double angle = 2*Math.PI * ((float)i / (float)numSegments);
				double x = radius * Math.cos(angle);
				double y = radius * Math.sin(angle);
				double z = 0;
				Vector3 rimPoint = new Vector3(x,y,z);
				if (!(n.equals(Vector3.k) || n.equals(Vector3.k.inverse()))) { // rotation not required if normal is already aligned with the z-axis
					rimPoint = rimPoint.rotate(rotation);
				}
				segs.add(new Segment(centerPos, centerPos.add(rimPoint)));
			}
		}

		@Override
		public CollisionInterface intersectSegment(Segment s) {
			Vector3 ra = s.a.subtract(centerPos);
			Vector3 rb = s.b.subtract(centerPos);			
			Vector3 ab = rb.subtract(ra);
			
			double denom = ab.dot(n);			
			if (denom == 0) {
				return null; // segment is parallel to circle plane -> no intersection or infinite intersection (meh)
			}
			double numer = -ra.dot(n);
			double t = numer / denom;
			
			if (t < 0 || t > 1.0) {
				return null; // segment doesnt intersect but the line does
			}
			
			Vector3 p = ra.add(ab.multScaler(t));
			if (p.mag() > radius) {
				return null; // segment intersects plane outside of the circle
			}
			
			return new CollisionInterface(centerPos.add(p), n);
		}

		@Override
		public List<Segment> getSegments() {
			return segs;
		}

		@Override
		public Vector3 getNormal() {
			return n;
		}
		
		@Override
		public void draw(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f); // Red
			gl.glVertex3dv(centerPos.toArray(), 0);
			gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); // Green
			for (Segment s : segs) {				
				gl.glVertex3dv(s.b.toArray(), 0);
			}
			gl.glVertex3dv(segs.get(0).b.toArray(), 0);
			
			gl.glEnd();			
		}
		
	}
	
	/**
	 * A {@link Primitive} implementation of a cylinder with no ends.
	 * 
	 * @author Jacob
	 *
	 */
	public static class OpenCylinder implements Primitive,Drawable {
		private final Vector3 centerPos;
		private final Vector3 n; // direction of the cylinder lengthwise
		private final double length; // full length
		private final double radius;
		private final int numSegs;
		private final List<Segment> segs = new ArrayList<Segment>();
		
		public OpenCylinder(Vector3 centerPos, Vector3 normal, double length, double radius, int numSegments) {
			this.n = normal;
			this.centerPos = centerPos;
			this.length = length;
			this.radius = radius;
			this.numSegs = numSegments;
			createSegments();
		}		

		@Override
		public CollisionInterface intersectSegment(Segment s) {
			Vector3 ra = s.a.subtract(centerPos);
			Vector3 rb = s.b.subtract(centerPos);
			Vector3 ab = rb.subtract(ra);
			double an = ra.dot(n);
			double bn = ab.dot(n);
			// x(t) -> vector equation from a to b (t = 0 to t = 1)
			// project x(t) onto plane normal to n
			// quadratic theorem equation with a,b, and c results:
			double A = ab.magSquared() - bn * bn;
			double B = 2 * ra.dot(ab) - 2 * an * bn;
			double C = ra.magSquared() - radius * radius - an * an;			
			double D = Math.sqrt(B*B - 4*A*C); // descriminant
			if (Double.isNaN(D)) {
				// descriminant is imaginary
				// the line created by the segment will never intersect even an infinite version of this cylinder
				return null;
			}			
			double t1 = (-B + D) / (2 * A);
			double t2 = (-B - D) / (2 * A);
			if (t1 >= 0 && t1 <= 1.0) {
				// t1 intersects the infinite cylinder
				Vector3 r = ra.add(ab.multScaler(t1));
				if (r.projectToVector(n).mag() < length / 2) { // t2 intersects the actual cylinder
					Vector3 normal = r.projectToPlane(n).normalize();
					return new CollisionInterface(r,normal);
				}
			}
			if (t2 >= 0 && t2 <= 1.0) {
				// t2 intersects the infinite cylinder
				Vector3 r = ra.add(ab.multScaler(t2));
				if (r.projectToVector(n).mag() < length / 2) { // t2 intersects the actual cylinder
					Vector3 normal = r.projectToPlane(n).normalize();
					return new CollisionInterface(r,normal);
				}
			}
			
			// intersects outside of the segment boundaries
			return null;
		}
		
		public void createSegments() {
			Vector3 halfLength = n.multScaler(length / 2);
			Quaternion rotation = Vector3.k.rotationRequired(n); // rotation neccesary to rotate world z up to the normal of the circle
			for (int i = 0; i < numSegs; i++) {
				double angle = 2*Math.PI * ((float)i / (float)numSegs);
				double x = radius * Math.cos(angle);
				double y = radius * Math.sin(angle);
				double z = 0;
				Vector3 rimPoint = new Vector3(x,y,z);
				if (!(n.equals(Vector3.k) || n.equals(Vector3.k.inverse()))) { // rotation not required if normal is already aligned with the z-axis
					rimPoint = rimPoint.rotate(rotation);
				}
				segs.add(new Segment(centerPos.add(rimPoint.add(halfLength)), centerPos.add(rimPoint.subtract(halfLength))));
			}
		}

		@Override
		public List<Segment> getSegments() {
			return segs;
		}

		/**
		 * Cylinders have no well defined normal.
		 * Always returns null.
		 */
		@Override
		public Vector3 getNormal() {
			return null;
		}
		
		@Override
		public void draw(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glBegin(GL.GL_LINES);
			for (Segment s : segs) {
				//System.out.println(s.b.toString());
				gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f); // Red
				gl.glVertex3dv(s.a.toArray(), 0);
				gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); // Green
				gl.glVertex3dv(s.b.toArray(), 0);
			}
			gl.glEnd();			
		}
		
	}	
	
	/**
	 * A primitive implementation that represents a sphere in 3d space.
	 * This implementation accepts a rank parameter where rank*rank are the number
	 * of segments returned by {@link #getSegments()}
	 * 
	 * @author Jacob
	 *
	 */
	public static class Sphere implements Primitive,Drawable {
		private final Vector3 center;
		private final double radius;
		private final int rank;
		private final List<Segment> segs = new ArrayList<Segment>();
		
		public Sphere(Vector3 center, double radius, int rank) {
			this.center = center;
			this.radius = radius;
			this.rank = rank;
			
			createSegments();
		}		

		@Override
		public CollisionInterface intersectSegment(Segment sBody) {
			Vector3 ra = sBody.a.subtract(center);
			Vector3 rb = sBody.b.subtract(center);			
			Vector3 ab = rb.subtract(ra);
			
			// segment equation -> x(t) = a + (b - a)t where t goes from 0 to 1
			// intersection point is where the magnitude of (x(t) - center) equals radius
			// therefore |ra + ab*t| = r
			// using foil -> ra^2 + ab^2*t^2 + t^2*ab^2 + 2t*(ra*ab) = r^2
			// solve quadratic formula ab^2*t^2 + (2*(ra*ab))t - (r^2 - ra^2) = 0
			double A = ab.magSquared();
			double B = 2 * ra.dot(ab);
			double C = ra.magSquared() - radius*radius;
			double D = Math.sqrt(B*B - 4*A*C);
			if (Double.isNaN(D)) {
				// descriminant is imaginary
				// the line created by the segment will never intersect the sphere
				return null;
			}
			double t1 = (-B + D) / (2 * A);
			double t2 = (-B - D) / (2 * A);
			double t = 0;
			// currently ignoring two intersections (choosing first), might fix later if needed
			if (t1 < 1.0 && t1 > 0.0) {
				t = t1;
			}
			else if (t2 < 1.0 && t2 > 0.0) {
				t = t2;					
			} else {
				// the line created by the segment intersects the sphere outside of a and b
				return null;
			}			
			
			Vector3 p = center.add(ra.add(ab.multScaler(t))); // point of intersection
			Vector3 n = p.subtract(center).normalize(); // normal at point of intersection

			
			return new CollisionInterface(p,n);
			
		}
		
		private void createSegments() {
			// starting at center and going out in the normal direction to points evenly distributed on surface of sphere
			double theta = 0;
			double phi = 0;
			for (int i = 0; i < rank; i++) {
				for (int j = 0; j < (rank / 2); j++) {
					theta = ((float)i / (float)rank) * 2 * Math.PI;
					phi = ((float)j / (float)(rank / 2)) * Math.PI;
					double x = radius * Math.cos(theta) * Math.sin(phi);
					double y = radius * Math.sin(theta) * Math.sin(phi);
					double z = radius * Math.cos(phi);
					segs.add(new Segment(center, new Vector3(x,y,z).add(center)));
				}
			}
		}

		@Override
		public List<Segment> getSegments() {		
			return segs;
		}

		/**
		 * Always returns null because a sphere does not have a constant normal.
		 */
		@Override
		public Vector3 getNormal() {
			return null;
		}

		@Override
		public void draw(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glBegin(GL.GL_LINES);
			for (Segment s : segs) {
				//System.out.println(s.b.toString());
				gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f); // Red
				gl.glVertex3dv(s.a.toArray(), 0);
				gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); // Green
				gl.glVertex3dv(s.b.toArray(), 0);
			}
			gl.glEnd();			
		}
		
	}
	

	
}