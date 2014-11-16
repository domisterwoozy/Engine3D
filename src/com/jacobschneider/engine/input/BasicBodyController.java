package com.jacobschneider.engine.input;

import com.jacobschneider.engine.Simulation;
import com.jacobschneider.engine.framework.InputListener;
import com.jacobschneider.engine.input.InputHandler.EngineAction;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.Body;

/**
 * A world frame body controller implementation.
 * Handles the main translate and rotate {@link EngineAction}s and exerts
 * temporary forces and torques on a body. All forces and torques 
 * are independent of the bodies current orientation in space.
 * For example no matter how the body is orientated a {@link EngineAction#TRANSLATE_UP}
 * commands will result in a force in the worlds positive z direction.
 * 
 * Add this class to {@link InputHandler#addListener(InputListener)} or to {@link Simulation#addListener(InputListener)}
 * if you are using the default {@link Simulation} object.
 * 
 * @author Jacob
 *
 */
public final class BasicBodyController implements InputListener {
	private static final float TRANSLATION_SENSITIVITY = 250.000f;
	private static final float ROTATION_SENSITIVITY = 25.0f;
	
	private final Body b;
	
	/**
	 * Creates a new {@link BasicBodyController} that directs input
	 * from the InputHandler to a specific body.
	 * @param b the {@link Body} to be controlled
	 */
	public BasicBodyController(Body b) {
		this.b = b;
	}

	@Override
	public boolean receiveInput(EngineAction action) {		
		switch (action) {
			case TRANSLATE_UP:
				b.thrustInputs(Vector3.k.multScaler(TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case TRANSLATE_DOWN:
				b.thrustInputs(Vector3.k.multScaler(-TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case TRANSLATE_BACKWARD:
				b.thrustInputs(Vector3.j.multScaler(-TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case TRANSLATE_FORWARD:
				b.thrustInputs(Vector3.j.multScaler(TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case TRANSLATE_LEFT:
				b.thrustInputs(Vector3.i.multScaler(-TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case TRANSLATE_RIGHT:
				b.thrustInputs(Vector3.i.multScaler(TRANSLATION_SENSITIVITY), Vector3.zero, 0.1f);
				return true;
			case PITCH_DOWN:			
				b.thrustInputs(Vector3.zero, Vector3.i.multScaler(-ROTATION_SENSITIVITY), 0.1f);
				return true;
			case PITCH_UP:
				b.thrustInputs(Vector3.zero, Vector3.i.multScaler(ROTATION_SENSITIVITY), 0.1f);
				return true;
			case YAW_LEFT:
				b.thrustInputs(Vector3.zero, Vector3.k.multScaler(ROTATION_SENSITIVITY), 0.1f);
				return true;
			case YAW_RIGHT:
				b.thrustInputs(Vector3.zero, Vector3.k.multScaler(-ROTATION_SENSITIVITY), 0.1f);
				return true;
			case ROLL_LEFT:
				b.thrustInputs(Vector3.zero, Vector3.j.multScaler(-ROTATION_SENSITIVITY), 0.1f);
				return true;
			case ROLL_RIGHT:
				b.thrustInputs(Vector3.zero, Vector3.j.multScaler(ROTATION_SENSITIVITY), 0.1f);
				return true;
			default:
				return false;

		}		
	}

}
