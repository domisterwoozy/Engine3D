package com.jacobschneider.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jacobschneider.engine.framework.Drawable;
import com.jacobschneider.engine.framework.InputListener;
import com.jacobschneider.engine.input.InputHandler;
import com.jacobschneider.engine.input.InputHandler.EngineAction;
import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.physics.Universe;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * A convenience class to display a simulation. Handles creating a window, displaying
 * the graphics of a universe, handling input, and incrementally updating a universe's physics.
 * All you have to do is create a universe object and pass it into a static factory method.
 * This class also automatically creates a very basic {@link InputHandler} with controls that map to WASD (translation) and the arrow keys (rotation).
 * You can notify other objects of these inputs by implementing {@link InputListener} and adding it to {@link #addListener(InputListener)}.
 * A call {@link #startSim()} will begin the simulation.
 * 
 * @author Jacob
 *
 */
public final class Simulation implements GLEventListener {
	// physics
	private Universe uni;
	private float timeScale = 1.0f;
	
	// graphics
	private Drawable drawable;
	private GLU glu;	
	private Color backgroundColor = new Color((213.0f/256.0f), (215.0f/256.0f), (242.0f/256.0f), 1.0f);
	
	// camera
	private Vector3 camPos = new Vector3(10,10,10);
	private Vector3 upDir = Vector3.k;
	private Vector3 lookAtPos = Vector3.zero;
	private float fov = 65.0f;
	private float zNear = 0.1f;
	private float zFar = 100.0f;
	private float aspect; // set by reshape
	
	// settings
	private boolean showFrameData = false;
	private int height;
	private int width;
	private int fps;
	
	// input handling
	private InputHandler myHandler = new InputHandler();
	
	/**
	 * Entry to point to the simulation. Creates a 1920x1080 60 Hz simulation.
	 * Create a Universe object and pass it in here to create the simulation.
	 * 
	 * @param uni The universe that will be simulated
	 * @return The simulation object that is created.
	 */
	public static Simulation createSimulation(Universe uni) {
		return new Simulation(uni, 1920, 1080, 60);
	}
	
	/**
	 * Entry to point to the simulation.
	 * Create a Universe object and pass it in here to create the simulation.
	 * 
	 * @param uni the universe that will be simulated
	 * @param width width of simulation window in pixels
	 * @param height height of simulation window in pixels
	 * @param fps refresh rate of the simulation window in hz
	 * @return The simulation object that is created
	 */
	public static Simulation createSimulation(Universe uni, int width, int height, int fps) {
		return new Simulation(uni, width, height, fps);
	}
	
	private Simulation(Universe uni, int width, int height, int fps) {
		if (uni instanceof Drawable) {
			this.drawable = (Drawable) uni;
		}
		this.uni = uni;
		this.width = width;
		this.height = height;
		this.fps = fps;		
	}
	
	/**
	 * Begins the simulation. Launches a window, starts the physics, and starts the graphics.
	 */
	public final void startSim() {
		//GLProfile.initSingleton();
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);			
		
		Frame frame = new Frame("Simulation");
		frame.setSize(width, height);
		frame.add(canvas);
		frame.setVisible(true);
		// so you can close window
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});			
		canvas.addGLEventListener(this); // adds this object to the listener queue so that the JOGL methods in this class are called by the canvas
		
		FPSAnimator animator = new FPSAnimator(canvas, fps);
		startPhysics(); // starts the physics
		mapKeys(canvas); // map keys and start listening for input
		animator.start(); // starts the graphics
	}
	
	
	/**
	 * Adds an {@link InputListener} to the simulation. Any listener
	 * added here will be delegated input commands based on the order
	 * they are added.
	 * @param listener The listener to add
	 */
	public void addListener(InputListener listener) {
		myHandler.addListener(listener);
	}
	
	private void mapKeys(Canvas canvas) {		
		myHandler.mapAction(87, EngineAction.TRANSLATE_FORWARD); // w
		myHandler.mapAction(65, EngineAction.TRANSLATE_LEFT); // a
		myHandler.mapAction(83, EngineAction.TRANSLATE_BACKWARD); // s
		myHandler.mapAction(68, EngineAction.TRANSLATE_RIGHT); // d
		myHandler.mapAction(81, EngineAction.TRANSLATE_UP); // q
		myHandler.mapAction(69, EngineAction.TRANSLATE_DOWN); // e
		myHandler.mapAction(100, EngineAction.YAW_LEFT); // num pad 4
		myHandler.mapAction(104, EngineAction.PITCH_UP); // num pad 8
		myHandler.mapAction(102, EngineAction.YAW_RIGHT); // num pad 6
		myHandler.mapAction(101, EngineAction.PITCH_DOWN); // num pad 5		
		myHandler.mapAction(103, EngineAction.ROLL_LEFT); // num pad 7
		myHandler.mapAction(105, EngineAction.ROLL_RIGHT); // num pad 9	
		canvas.addKeyListener(myHandler);
	}
	
	
	
	private void startPhysics() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.execute(new PhysicsLoop());
	}
	
	private class PhysicsLoop implements Runnable {		
		@Override
		public void run() {
			long count = 0;
			long lastTime = System.nanoTime();
			long lastCountTime = System.nanoTime();
			float deltaTime = 0;
			while (true) {
				count++;
				deltaTime = (timeScale) * (System.nanoTime() - lastTime) / 1000000000.0f;
				lastTime = System.nanoTime();	
				if (count % 10000 == 0) {
					//System.out.println("FPS: " + (1 / deltaTime));
					if (showFrameData) {
						System.out.println("Time for 10000 frames: " + (System.nanoTime() - lastCountTime) / 1000000000.0f);
					}
					lastCountTime = System.nanoTime();
				}
				uni.update(deltaTime);	
			}			
		}
	}

	// called every frame. Clears graphics and calls draw method in universe.
	@Override
	public void display(GLAutoDrawable drawable) {	
		GL2 gl = drawable.getGL().getGL2();			
		//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		resetView(gl);
		
		this.drawable.draw(drawable);
	}	

	/**
	 * This is called by the JOGL framework and you should not touch this.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub		
	}

	/**
	 * This is called by the JOGL framework and you should not touch this.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();	
		glu = GLU.createGLU(); // gl utilities
		// color when glClear is called
		gl.glClearColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());
		// enable smooth shading which blends colors nicely and smoothes out shading
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		// setup depth buffer
		gl.glClearDepth(1.0f); // clears the z-buffer to the farthest
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // lol wut (do the best perspective correction)		
	}
	
	/**
	 * Sets the background color for the simulation.
	 * 
	 * @param color The color for the background
	 */
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	
	/**
	 * Sets the time scale for the simulation.
	 * A time scale of 1.0 is real time.
	 * 
	 * @param timeScale Number of seconds that occurs in the simulation proportional to the number of seconds that occur in the real world.
	 */
	public void setTimeScale(float timeScale) {
		this.timeScale = timeScale;
	}
	
	/**
	 * Sets the state of the camera.
	 * @param camPos Position vector in space
	 * @param lookAtPos Position the camera is pointing towards
	 * @param up The up direction of the camera
	 */
	public void setCamera(Vector3 camPos, Vector3 lookAtPos, Vector3 up) {
		this.camPos = camPos;
		this.lookAtPos = lookAtPos;
		this.upDir = up;
	}
	
	/**
	 * Sets the attributes of the camera
	 * @param fieldOfView Camera's field of view in degrees.
	 * @param minDist Minimum distance the camera will render.
	 * @param maxDist Maximum distance the camera will render.
	 */
	public void setCameraAttrib(float fieldOfView, float minDist, float maxDist) {
		this.fov = fieldOfView;
		this.zNear = minDist;
		this.zFar = maxDist;
	}

	/**
	 * This is called by the JOGL framework and you should not call this.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
	      
	    height = (height == 0) ? 1 : height;  // Prevent divide by zero
	    aspect = (float)width / height; // Compute aspect ratio
	      
	    resetView(gl);		
	}
	
	private void resetView(GL2 gl) {
		// Set view port to cover full screen 
	    gl.glViewport(0, 0, width, height);
	      
	    // Set up the projection matrix - choose perspective view
	    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);  
	    gl.glLoadIdentity();    // reset

	    // camera position/orientation and attributes
	    glu.gluPerspective(fov, aspect, zNear, zFar); // fovy, aspect, zNear, zFar
		glu.gluLookAt(camPos.x, camPos.y, camPos.z, lookAtPos.x, lookAtPos.y, lookAtPos.z, upDir.x, upDir.y, upDir.z);
	      
	    // Switch to the model-view transform
	    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	    gl.glLoadIdentity();    // reset		
	}

	/**
	 * Console prints the time it takes to iterate through 10,000 physics frames as the simulation is running.
	 * 
	 * @param showFrameData Whether frame information should be printed to console.
	 */
	public void showFrameData(boolean showFrameData) {
		this.showFrameData = showFrameData;
	}


	
	public Vector3 getCamPos() {
		return camPos;
	}
	
	public Vector3 getCamLookAt() {
		return lookAtPos;
	}
	
	public Vector3 getCamUp() {
		return upDir;
	}
	
	
}
