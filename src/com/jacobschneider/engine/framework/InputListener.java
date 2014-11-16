package com.jacobschneider.engine.framework;

import com.jacobschneider.engine.input.InputHandler;
import com.jacobschneider.engine.input.InputHandler.EngineAction;

/**
 * An interface that can receive input from the engine.
 * Implement this and add it to {@link InputHandler} to receive input.
 * @author Jacob
 *
 */
public interface InputListener {
	/**
	 * This is the method that is called by {@link InputHandler}.
	 * 
	 * @param action The action received by the input handler
	 * @return Returns true if the action was received and dealt with. Else returns false.
	 */
	public boolean receiveInput(EngineAction action);
}
