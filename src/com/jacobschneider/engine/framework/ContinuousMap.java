package com.jacobschneider.engine.framework;

/**
 * Represents a continuous mapping between a domain and a codomain.
 * @author Jacob
 *
 * @param <D> the domain of the mapping
 * @param <C> the codomain of the mapping
 */
public interface ContinuousMap<D,C> {
	/**
	 * Maps any element in the domain to an element in the codomain
	 * @param input an element in the domain
	 * @return the corresponding mapping an element in the codomain
	 */
	public C map(D input);
}
