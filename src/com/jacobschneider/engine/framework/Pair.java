package com.jacobschneider.engine.framework;

/**
 * An ordered pair of objects.
 * 
 * @author Jacob
 *
 * @param <A> Type of first object
 * @param <B> Type of second object
 */
public class Pair<A, B> {
	public final A a;
	public final B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public static <A, B> Pair<A, B> create(A a, B b) {
		return new Pair<A, B>(a,b);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?,?>)) {
			return false;
		}
		Pair<?,?> otherPair = (Pair<?,?>) o;		
		if (otherPair == this) {
			return true;
		}
		
		if (!a.equals(otherPair.a)) {
			return false;
		}
		if (!b.equals(otherPair.b)) {
			return false;
		}		
		return true;		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + a.hashCode();
		result = 31 * result + b.hashCode();
		return result;		
	}

}
