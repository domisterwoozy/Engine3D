Engine3D
========

A three dimensional rigid body physics simulation engine. It is written completely from scratch from the ground up in Java. It is very robust and physically accurate in a variety of situations. It simulates collisions between complex geometries and also simulates the effects of external forces. A very simple visualization implementation is included through the Drawable interface using JOGL (the only dependency - JARS are included in the JARS folder).

**Framework Hierarchy:**

1. Segments: A line in 3D space with a start point and an end point.
2. Primitives: A collection of segments. Can detect if other segments are colliding with it.
3. Shape: A collection of primitives. All primitives in a shape are completely rigid and do not move relative to one another.
4. Body: A combination of a shape, a physics body, and a bounding volume. 
	* Physics Body: The physics body describes the physics of the body including the number of degrees of freedom, and how these	degrees of freedom respond to forces on the body. These forces can come from collisions or other external forces (end user applied force, gravity, magnetism, etc).
	* Bounding Volume: A volume that completely encloses the shape. Optional and for performance purposes.
	* Shape: Described above
5. Universe: A collection of bodies. Iniates the collision engine between bodies and handles external forces.

Each of these 7 concepts defined above exist as an interface in the framework and can be implemented and extended in many different ways. Many of these implementations are included in this project.

For convienence I have included a simulation class that handles user input, handles the graphics, and incrementally updates the universe in real time.

**Quick Start Guide**

1. Create Body objects by calling the static factory methods in the Bodies class or using the builder in Body.Builder.
2. Add these bodies to a BasicUniverse object. Add desired forces to the universe.
3. Add the universe to a Simulation object and then start the simulation.

Example:
```
// two boxes falling to the floor in a room
public static void main(String[] args) {	
	// creates new cuboid bodies
	Body b1 = Bodies.newCuboid(new Vector3(0,0,10), new Vector3(0,0,0), 3.5, 0.5, 1.5, 10);
	Body b2 = Bodies.newCuboid(new Vector3(-4.5,0,10), new Vector3(0,0,0), 3.5, 1.0, 1.9, 12);		
	// creates 5 new bodies representing the walls and floor of a room
	List<Body> walls = Bodies.newRoom(new Vector3(0,0,5), 30, 30, 10);
	
	// add the bodies to a list
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
```


