package com.jacobschneider.engine.math.boundingvolumes;

import com.jacobschneider.engine.math.Vector3;


/**
 * This is a static class that contains all the {@link OverlapStrategy} implementations that
 * are apart of the original engine.
 * 
 * @author Jacob
 *
 */
public class OverlapStrategyContainer {	
	static class SphereSphereOverlap implements OverlapStrategy {
		public boolean testOverlap(BoundVolume vol1, BoundVolume vol2) {
			if (!(vol1 instanceof BoundSphere) || !(vol2 instanceof BoundSphere)) {
				throw new IllegalArgumentException("Arguments must be of type BoundSphere");
			}
			BoundSphere s1 = (BoundSphere)vol1;
			BoundSphere s2 = (BoundSphere)vol2;
			Vector3 d = s1.getCenter().subtract(s2.getCenter());
			double dist2 = d.magSquared();
			double radiusSum = s1.getRadius() + s2.getRadius();
			return dist2 <= radiusSum*radiusSum;
		}

		@Override
		public BoundVolumePair getVolumePair() {
			return new BoundVolumePair(BoundSphere.class, BoundSphere.class);
		};
	}
	
	static class SphereCircleOverlap implements OverlapStrategy {
		public boolean testOverlap(BoundVolume vol1, BoundVolume vol2) {
			// argument checking
			BoundSphere s = null;
			BoundCircle p = null;
			if (vol1 instanceof BoundSphere) {
				if (vol2 instanceof BoundCircle) {
					s = (BoundSphere)vol1;
					p = (BoundCircle)vol2;
				} 
			} else if (vol2 instanceof BoundSphere) {
				if (vol1 instanceof BoundCircle) {
					s = (BoundSphere)vol2;
					p = (BoundCircle)vol1;
				}
			} else {
				throw new IllegalArgumentException("One argument must be of type Boundsphere and one of type BoundPlane");
			}
			// overlap check
			Vector3 r = s.getCenter().subtract(p.getCenter());
			double rPerp = r.dot(p.getNormal());
			if (rPerp > s.getRadius()) {
				return false;
			}
			double rPar = r.projectToPlane(p.getNormal()).mag();
			if (rPar > p.getRadius()) {
				return false;
			}			
			return true;			
		}		
		

		@Override
		public BoundVolumePair getVolumePair() {
			return new BoundVolumePair(BoundSphere.class,BoundCircle.class);
		};
	}
	
	static class CircleCircleOverlap implements OverlapStrategy {

		@Override
		public boolean testOverlap(BoundVolume vol1, BoundVolume vol2) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public BoundVolumePair getVolumePair() {
			return new BoundVolumePair(BoundCircle.class, BoundCircle.class);
		}
		
	}		
	
	

}
