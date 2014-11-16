package com.jacobschneider.engine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jacobschneider.engine.framework.Manifold;
import com.jacobschneider.engine.framework.Primitive;
import com.jacobschneider.engine.framework.ScalerField;
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
import com.jacobschneider.engine.math.vectorcalc.CartesianScalerField;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.BasicUniverse;
import com.jacobschneider.engine.physics.Bodies;
import com.jacobschneider.engine.physics.Body;
import com.jacobschneider.engine.physics.RigidShape;


public class Test {
	public static void main(String[] args) {
		Sims.fallingBlocks();
		
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
		Body floor = Bodies.newWall(Vector3.zero, Vector3.k, 50, 50);
		Body.Builder floorBuilder = Body.Builder.builderFromBody(floor);
		// hard to spin but spinnable
		floorBuilder.inertiaTensor(Matrix3.IDENTITY.multScaler(100));
		floorBuilder.fixAxes(EnumSet.of(Axis.Y_AXIS, Axis.X_AXIS));
		floor = floorBuilder.build();
		
		Body ball1 = Bodies.newBall(new Vector3(0,-3,5), Vector3.zero, 1, 10,1);		
		Body.Builder ballBuilder = Body.Builder.builderFromBody(ball1);

		ballBuilder.changePos(new Vector3(0,3,5));
		Body ball2 = ballBuilder.build();
		
		List<Body> bodies = new ArrayList<Body>();
		bodies.add(floor);
		bodies.add(ball1);
		bodies.add(ball2);
		
		Universe uni = new BasicUniverse(bodies);
		uni.setGravity(9.8);
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		sim.showFrameData(false);
		sim.startSim();

	}
	
	public static void test2() {
		Set<Primitive> prims = new HashSet<Primitive>();
		prims.add(new OpenCylinder(Vector3.zero, Vector3.j, 2, 3, 100));
		prims.add(new Circle(Vector3.zero.add(Vector3.j), Vector3.j, 3, 100));
		prims.add(new Circle(Vector3.zero.subtract(Vector3.j), Vector3.j, 3, 100));
		RigidShape shape = new RigidShape(prims);
		Body.Builder builder = new Body.Builder(new Vector3(0,0,15), 1, shape);
		//builder.initialOmega(new Vector3(0.1,0.1,0.1));
		Body disk = builder.build();
		
		Body floor = Bodies.newWall(Vector3.zero, Vector3.k, 100, 100);
		
		List<Body> bodies = new ArrayList<Body>();
		
		Body ball = Bodies.newBall(new Vector3(0,0,55), Vector3.zero, 1, 50, 1);
		
		bodies.add(ball);
		bodies.add(disk);
		bodies.add(floor);
		
		Universe uni = new BasicUniverse(bodies);
		uni.setGravity(9.8);
		Simulation sim = Simulation.createSimulation(uni, 1920, 1080, 144);
		sim.setTimeScale(0.5f);
		sim.addListener(new BasicBodyController(ball));
		sim.setCamera(new Vector3(0, -50, 15), Vector3.zero, Vector3.k);
		sim.startSim();
	}
	

}
