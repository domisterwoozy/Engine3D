package com.jacobschneider.engine.framework;

import javax.media.opengl.GLAutoDrawable;

/**
 * A generic interface for drawing in JOGL.
 * 
 * @author Jacob
 *
 */
public interface Drawable {
	/**
	 * Draws the object.
	 * @param drawable Used to render objects using JOGL
	 */
	public void draw(GLAutoDrawable drawable);
}
