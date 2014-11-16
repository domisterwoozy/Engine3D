package com.jacobschneider.engine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.Primitive;
import com.jacobschneider.engine.framework.ScalarField;
import com.jacobschneider.engine.framework.Universe;
import com.jacobschneider.engine.framework.VectorField;
import com.jacobschneider.engine.framework.PhysicsBody.Axis;
import com.jacobschneider.engine.input.BasicBodyController;
import com.jacobschneider.engine.input.BasicCameraController;
import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.geometry.Line;
import com.jacobschneider.engine.math.geometry.Manifoldable;
import com.jacobschneider.engine.math.geometry.Plane;
import com.jacobschneider.engine.math.geometry.Primitives.Circle;
import com.jacobschneider.engine.math.geometry.Primitives.OpenCylinder;
import com.jacobschneider.engine.math.vectorcalc.CartesianScalarField;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.BasicUniverse;
import com.jacobschneider.engine.physics.Bodies;
import com.jacobschneider.engine.physics.Body;
import com.jacobschneider.engine.physics.RigidShape;


public class Test {
	public static void main(String[] args) {
		test1();
		
	}
	
	public static void test() {
		// creates new cuboid bodies
		Body b1 = Bodies.newCuboid(new Vector3(0,0,10), new Vector3(0,0,0), 3.5, 0.5, 1.5, 10);
		Body b2 = Bodies.newCuboid(new Vector3(-4.5,0,10), new Vector3(0,0,0), 3.5, 1.0, 1.9, 12);		
		// creates 5 new bodies representing the walls and floor of a room
		List<Body> walls = Bodies.newRoom(new Vector3(0,0,5), 30, 30, 10);
		// add the bodies to list
		List<Body> bodies = new ArrayList<Body>();
		bodies.addAll(walls);		
		bodies.add(b1);
		bodies.add(b2);
		// constructs a universe out of the bodies
		Universe uni = new BasicUniverse(bodies); 
		// puts gravity in the universe
		uni.setGravity(9.8); 
		// creates a simulation out of uni with a resolution of 1920x1080 and 144 hz.
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		// sets the position and orientation of the camera in the simulation
		sim.setCamera(new Vector3(15, 15, 10), new Vector3(0,0,5), Vector3.k);
		// starts the simulation
		sim.startSim();
	}
	
	public static void test1() {
		ScalarField gravity = new CartesianScalarField(new double[] {1, 1, 1}, new double[] {2, 2, 2});		
		
		Body sun1 = Bodies.newBall(new Vector3(0, 0, 20), Vector3.i, 1, 25, 10);
		Body sun2 = Bodies.newBall(new Vector3(0, 0, 25), Vector3.i.inverse(), 1, 25, 10);
		
		Universe uni = new BasicUniverse(sun1, sun2);
		
		uni.addBodyPotential(sun1, gravity);
		uni.addBodyPotential(sun2, gravity);
		
		Simulation sim = Simulation.createSimulation(uni);
		sim.addListener(new BasicCameraController(sim));
		sim.startSim();
		

	}

	

}
