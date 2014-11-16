package com.jacobschneider.engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacobschneider.engine.framework.InputListener;

/**
 * Basic input handler.
 * Add this as a listener to the form that you are using and then map keys
 * to actions by calling {@link #mapAction(int, EngineAction)}.
 * @author Jacob
 *
 */
public class InputHandler implements KeyListener {
	/**
	 * List of currently supported actions that you can map too.
	 * 
	 * @author Jacob
	 *
	 */
	public static enum EngineAction {
		TRANSLATE_UP,
		TRANSLATE_DOWN,
		TRANSLATE_LEFT,
		TRANSLATE_RIGHT,
		TRANSLATE_FORWARD,
		TRANSLATE_BACKWARD,
		YAW_LEFT,
		YAW_RIGHT,
		PITCH_UP,
		PITCH_DOWN,
		ROLL_LEFT,
		ROLL_RIGHT
	}
	
	private Map<Integer, EngineAction> keyMapping = new HashMap<Integer, EngineAction>();
	private List<InputListener> listeners = new ArrayList<InputListener>();
	
	/**
	 * Maps a key to an action.
	 * @param keyCode Integer representation of a {@link KeyEvent}. Can be found from a KeyEvent by calling {@link KeyEvent#getKeyCode()} or
	 * by looking at the static public int fields in {@link KeyEvent}.
	 * @param action The action you want to map the keyCode to.
	 */
	public void mapAction(int keyCode, EngineAction action) {
		keyMapping.put(keyCode, action);
	}
	
	/**
	 * Any class that you want to respond to key events must implement {@link InputListener}
	 * and be added here.
	 * @param listener The listener you want to add.
	 */
	public void addListener(InputListener listener) {
		listeners.add(listener);
	}

	@Override
	public void keyPressed(KeyEvent key) {		
		int keyCode = key.getKeyCode();
		EngineAction action = keyMapping.get(keyCode);
		if (action == null) {
			return; // not mapped
		}
		for (InputListener listener : listeners) {
			if (listener.receiveInput(action)) {
				return;
			}
		}		
	}
	
	
	
	
	

	@Override
	public void keyReleased(KeyEvent key) {}

	@Override
	public void keyTyped(KeyEvent key) {}

}
