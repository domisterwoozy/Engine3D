package com.jacobschneider.engine.physics;

import java.util.List;

import com.jacobschneider.engine.math.Vector3;

/**
 * A lightweight struct that holds data describing a single contact.
 * @author Jacob
 *
 */
public class CollisionInterface {
	public final Vector3 r; // point
	public final Vector3 n; // plane normal that intersects point
	
	
	public CollisionInterface(Vector3 r, Vector3 n) {
		this.r = r;
		this.n = n;
	}
	
	public static List<CollisionInterface> flipNormals(List<CollisionInterface> inters) {
		for (int i = 0; i < inters.size(); i++) {
			inters.set(i, new CollisionInterface(inters.get(i).r, inters.get(i).n.inverse()));
		}
		return inters;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CollisionInterface)) {
			return false;
		}
		CollisionInterface otherInter = (CollisionInterface) o;
		if (otherInter == this) {
			return true;
		}
		if (!otherInter.r.equals(this.r)) {
			return false;
		}
		if (!otherInter.n.equals(this.n)) {
			return false;
		}
		
		return true;		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + r.hashCode();
		result = 31 * result + n.hashCode();
		return result;
	}
}
