package com.jacobschneider.engine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.jacobschneider.engine.framework.Universe;
import com.jacobschneider.engine.framework.PhysicsBody.Axis;
import com.jacobschneider.engine.input.BasicCameraController;
import com.jacobschneider.engine.math.Matrix3;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.vectorcalc.CartesianScalarField;
import com.jacobschneider.engine.physics.BasicUniverse;
import com.jacobschneider.engine.physics.Bodies;
import com.jacobschneider.engine.physics.Body;
import com.jacobschneider.engine.physics.RigidShape;
import com.jacobschneider.engine.physics.Shapes;

/**
 * A collection of static methods that produce sample simulations.
 * 
 * @author Jacob
 *
 */
public class Sims {	
	public static void sceneOne() {
		Body.Builder builder;
		
		Body floor = Bodies.newWall(Vector3.zero, Vector3.k, 100, 100);
		
		Body launcher = Bodies.newCuboid(new Vector3(0, 0, 1.1), Vector3.zero, 6, 2, 0.1, 1);
		
		Body dropBall = Bodies.newCuboid(new Vector3(2, 0, 10), new Vector3(0, 0, -0), 0.5, 0.5, 0.5, 12);
		
		Body launchedSquare = Bodies.newCuboid(new Vector3(-2.0, 0, 1.6), Vector3.zero, 0.5, 0.5, 0.5, 0.05);
		builder = Body.Builder.builderFromBody(launchedSquare);//.setConstraint(new Line(new Vector3(-2.0, 0, 1.6), Vector3.k));
		launchedSquare = builder.build();
		
		//List<Body> bList = Body.newRoom(new Vector3(6, 0, 2), 2, 2, 0.5);
		
		builder = new Body.Builder(new Vector3(5.0, 2.5, 3), 1.0, Shapes.newWall(3, 3));
		builder.inertiaTensor(Matrix3.IDENTITY.multScaler(1));
		builder.fixPos();
		builder.fixAxes(EnumSet.of(Axis.X_AXIS, Axis.Z_AXIS));
		Body ramp1 = builder.build();	
		
		builder = new Body.Builder(new Vector3(7.0, 0, 6), 1.0, Shapes.newWall(3, 3));
		builder.inertiaTensor(Matrix3.IDENTITY.multScaler(1));
		builder.fixPos();
		builder.fixRotationAround(new Vector3(1,1,0), 1.0);
		Body ramp2 = builder.build();
		
		builder = new Body.Builder(new Vector3(0, 0, 0.50001), 1, Shapes.newCylinder(0.5, 5.0, 25));
		Body fulcrum = builder.initialRotation(Quaternion.newQuaternion(Math.PI / 2, Vector3.i)).build();				

		BasicUniverse uni = new BasicUniverse(floor, dropBall, fulcrum, launcher, launchedSquare, ramp1, ramp2);
		
		uni.setGravity(9.8);
		
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		sim.addListener(new BasicCameraController(sim));
		sim.setTimeScale(0.5f);
		//sim.showFrameData(true);
		sim.startSim();		
	}	
	
	/**
	 * A room full of bouncing balls.
	 */
	public static void bouncyBalls() {
		List<Body> bodies = new ArrayList<Body>();
		List<Body> walls = Bodies.newRoom(new Vector3(0,0,5), 50, 50, 30);
		bodies.addAll(walls);
		for (int i = -5; i < 5; i++) {
			bodies.add(Bodies.newBall(new Vector3(i*4,i*4,10), Vector3.zero, 2, 25,1));
		}
		for (int i = -5; i < 5; i++) {
			bodies.add(Bodies.newBall(new Vector3(i*6,i*6,25), Vector3.zero, 3, 25,2));
		}
		
		BasicUniverse uni = new BasicUniverse(bodies);
		uni.setGravity(9.8);
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		sim.setCamera(new Vector3(25, 25, 10), new Vector3(0,0,5), Vector3.k);
		sim.startSim();
	}
	
	/**
	 * A pyramid of blocks getting hit by a ball.
	 */
	public static void pyramid() {		
		Body b1 = Bodies.newCuboid(new Vector3(0,0,1), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b2 = Bodies.newCuboid(new Vector3(-4.5,0,1), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b3 = Bodies.newCuboid(new Vector3(4.5,0,1), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b8 = Bodies.newCuboid(new Vector3(-9,0,1), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b9 = Bodies.newCuboid(new Vector3(9,0,1), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		
		Body b4 = Bodies.newCuboid(new Vector3(2.5,0,3), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b5 = Bodies.newCuboid(new Vector3(-2.5,0,3), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b11 = Bodies.newCuboid(new Vector3(7,0,3), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b10 = Bodies.newCuboid(new Vector3(-7,0,3), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		
		Body b6 = Bodies.newCuboid(new Vector3(0,0,5), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b12 = Bodies.newCuboid(new Vector3(4.5,0,5), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b13 = Bodies.newCuboid(new Vector3(-4.5,0,5), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		
		Body b14 = Bodies.newCuboid(new Vector3(2.5,0,7), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		Body b15 = Bodies.newCuboid(new Vector3(-2.5,0,7), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		
		Body b16 = Bodies.newCuboid(new Vector3(0,0,9), new Vector3(0,0,0), 3.9999, 0.9999, 1.9999, 10);
		
		Body b7 = Bodies.newBall(new Vector3(0, 40, 2.5),new Vector3(0,-150,0),1,25,10);

		List<Body> bodies = new ArrayList<Body>();
		List<Body> walls = Bodies.newRoom(new Vector3(0,0,5), 30, 30, 10);
		bodies.addAll(walls);		
		bodies.add(b1);
		bodies.add(b2);
		bodies.add(b3);
		bodies.add(b4);
		bodies.add(b5);
		bodies.add(b6);
		bodies.add(b7);
		bodies.add(b8);
		bodies.add(b9);
		bodies.add(b10);
		bodies.add(b11);
		bodies.add(b12);
		bodies.add(b13);
		bodies.add(b14);
		bodies.add(b15);
		bodies.add(b16);
		
		Universe uni = new BasicUniverse(bodies); // constructs a universe out of the bodies
		uni.setGravity(9.8); // puts gravity in the universe
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144); // creates a simulation out of uni with a resolution of 1920x1080 and 144 hz.
		sim.setCamera(new Vector3(15, 15, 10), new Vector3(0,0,5), Vector3.k); // sets the position and orientation of the camera in the simulation
		sim.startSim(); // starts the simulation

	}
	
	/**
	 * Blocks falling into a room with no ceiling.
	 */
	public static void fallingBlocks() {
		List<Body> bodies = new ArrayList<Body>();
		
		for (int i = 0; i < 1; i++) {
			Body b2 = Bodies.newCuboid(new Vector3(-1,1,25 + 100*i), new Vector3(0,0,0), 1, 5, 5, 10);
			Body b3 = Bodies.newCuboid(new Vector3(1,-1,30 + 100*i), new Vector3(0,0,0), 1, 5, 1, 3);
			Body b1 = Bodies.newCuboid(new Vector3(0,0,35 + 100*i), new Vector3(0,0,0), 1, 1, 1, 3);
			Body b4 = Bodies.newCuboid(new Vector3(-1,1,40 + 100*i), new Vector3(0,0,-.1), 1, 5, 5, 10);
			Body b5 = Bodies.newCuboid(new Vector3(1,-1,20 + 100*i), new Vector3(0,0,-.5), 1, 5, 1, 3);
			Body b6 = Bodies.newCuboid(new Vector3(0,0,10 + 100*i), new Vector3(0,0,0), 1, 1, 1, 1);
			Body b7 = Bodies.newCuboid(new Vector3(0,-50,50 + 100*i), new Vector3(0, 8, -8), 3, 3, 3, 25);
			Body b8 = Bodies.newCuboid(new Vector3(-1,1,75 + 100*i), new Vector3(0,0,0), 1, 5, 5, 10);
			
			Body b10 = Bodies.newCuboid(new Vector3(0,0,85 + 100*i), new Vector3(0,0,0), 1, 1, 1, 3);
			Body b11 = Bodies.newCuboid(new Vector3(-5,5,90 + 100*i), new Vector3(0,0,-.1), 1, 5, 5, 10);
			Body b13 = Bodies.newCuboid(new Vector3(0,0,90 + 100*i), new Vector3(0,0,0), 1, 1, 1, 1);
			Body b14 = Bodies.newCuboid(new Vector3(0,50,50 + 100*i), new Vector3(0, 8, -8), 3, 3, 3, 25);
			bodies.add(b1);
			bodies.add(b2);
			bodies.add(b3);
			bodies.add(b4);
			bodies.add(b5);
			bodies.add(b6);
			bodies.add(b7);
			bodies.add(b8);
				
			bodies.add(b10);		
			bodies.add(b11);
			bodies.add(b13);
			bodies.add(b14);
		}

		Body b9 = Bodies.newCuboid(new Vector3(1,-1,70 + 100), new Vector3(0,0,0), 1, 5, 1, 3);
		bodies.add(b9);	
		List<Body> room = Bodies.newRoom(Vector3.zero, 30, 30, 30);


		bodies.addAll(room);
		
		BasicUniverse uni = new BasicUniverse(bodies);
		uni.setGravity(9.8);
		//uni.addScalerPotential(new CartesianScalerField(new double[] {10, 10, 10}, new double[] {2, 2, 2}));
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		sim.setTimeScale(0.5f);
		sim.showFrameData(true);
		sim.addListener(new BasicCameraController(sim));
		sim.setCamera(new Vector3(15, 15, -10), new Vector3(0, 0, -25), Vector3.k);
		sim.startSim();	
	}

}
