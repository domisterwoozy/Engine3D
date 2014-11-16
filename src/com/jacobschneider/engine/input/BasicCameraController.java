package com.jacobschneider.engine.input;

import com.jacobschneider.engine.Simulation;
import com.jacobschneider.engine.framework.InputListener;
import com.jacobschneider.engine.input.InputHandler.EngineAction;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;

/**
 * Basic implementation of {@link InputListener} that controls
 * the 6DOF of a camera. Must be used with a Simulation object.
 * 
 * @author Jacob
 *
 */
public class BasicCameraController implements InputListener {
	private static final float TRANSLATION_SENSITIVITY = 1.0f;
	private static final float ROTATION_SENSITIVITY = 0.05f;
	private final Simulation sim;
	
	/**
	 * Creates a new camera controller.
	 * @param sim The simulation it belongs to.
	 */
	public BasicCameraController(Simulation sim) {
		this.sim = sim;
	}

	@Override
	public boolean receiveInput(EngineAction action) {
		Vector3 camPos = sim.getCamPos();
		Vector3 lookAt = sim.getCamLookAt();
		Vector3 upDir = sim.getCamUp();
		Quaternion camOrientation = Quaternion.fromCameraVectors(camPos, lookAt, upDir);
		
		Vector3 translateDir = Vector3.zero;
		Vector3 lookAtTrans = Vector3.zero;
		
		Vector3 camDir = lookAt.subtract(camPos);
		
		
		switch (action) {
		case TRANSLATE_UP:
			translateDir = Vector3.k.rotate(camOrientation);
			break;
		case TRANSLATE_DOWN:
			translateDir = Vector3.k.inverse().rotate(camOrientation);
			break;
		case TRANSLATE_BACKWARD:
			translateDir = Vector3.j.inverse().rotate(camOrientation);
			break;
		case TRANSLATE_FORWARD:
			translateDir = Vector3.j.rotate(camOrientation);
			break;
		case TRANSLATE_LEFT:
			translateDir = Vector3.i.inverse().rotate(camOrientation);
			break;
		case TRANSLATE_RIGHT:
			translateDir = Vector3.i.rotate(camOrientation);
			break;
		case PITCH_DOWN:			
			Vector3 newCamDir = camDir.rotate(Quaternion.newQuaternion(-ROTATION_SENSITIVITY, Vector3.i.rotate(camOrientation)));
			lookAtTrans = newCamDir.subtract(camDir);
			break;
		case PITCH_UP:
			Vector3 newCamDir1 = camDir.rotate(Quaternion.newQuaternion(ROTATION_SENSITIVITY, Vector3.i.rotate(camOrientation)));
			lookAtTrans = newCamDir1.subtract(camDir);
			break;
		case YAW_LEFT:
			Vector3 newCamDir3 = camDir.rotate(Quaternion.newQuaternion(ROTATION_SENSITIVITY, Vector3.k.rotate(camOrientation)));
			lookAtTrans = newCamDir3.subtract(camDir);
			break;
		case YAW_RIGHT:
			Vector3 newCamDir2 = camDir.rotate(Quaternion.newQuaternion(-ROTATION_SENSITIVITY, Vector3.k.rotate(camOrientation)));
			lookAtTrans = newCamDir2.subtract(camDir);
			break;
		default:
			return false;

		}	

		sim.setCamera(camPos.add(translateDir.multScaler(TRANSLATION_SENSITIVITY)),
				lookAt.add(translateDir.multScaler(TRANSLATION_SENSITIVITY)).add(lookAtTrans), upDir);	
		return true;
	}

}
