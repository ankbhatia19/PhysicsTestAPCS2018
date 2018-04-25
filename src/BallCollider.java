import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepAdapter;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactAdapter;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class BallCollider extends SimulationFrame
{

	private static final long serialVersionUID = -3161261903733001222L;
	private static final Object FLOOR_BODY = new Object();
	Wheel wheel1;
	Wheel wheel2;
	
	public BallCollider() {
		
		super("Simple Platformer", 32.0);
		
		KeyListener listener = new CustomKeyListener(wheel1, wheel2);
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
		
		
		
	}

	@Override
	protected void initializeWorld() {
		
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(50.0, 0.2));
		floor.setMass(MassType.INFINITE);
		floor.translate(0, -3);
		floor.setUserData(FLOOR_BODY);
		this.world.addBody(floor);


		// some bounding shapes
		SimulationBody right = new SimulationBody();
		right.addFixture(Geometry.createRectangle(0.2, 20));
		right.setMass(MassType.INFINITE);
		right.translate(10, 7);
		this.world.addBody(right);

		SimulationBody left = new SimulationBody();
		left.addFixture(Geometry.createRectangle(0.2, 20));
		left.setMass(MassType.INFINITE);
		left.translate(-10, 7);
		this.world.addBody(left);

		//THE HILL
		SimulationBody mid = new SimulationBody();
		mid.addFixture(Geometry.createRectangle(3, 3));
		mid.setMass(MassType.INFINITE);
		mid.setUserData(FLOOR_BODY);
		mid.translate(0,-1.30);
		this.world.addBody(mid);

		SimulationBody leftMid = new SimulationBody();
		leftMid.addFixture(Geometry.createRightTriangle(3, 3));
		leftMid.setMass(MassType.INFINITE);
		leftMid.setUserData(FLOOR_BODY);
		leftMid.translate(2.5, -1.8);
		this.world.addBody(leftMid);

		SimulationBody rightMid = new SimulationBody();
		rightMid.addFixture(Geometry.createRightTriangle(3, 3));
		rightMid.setMass(MassType.INFINITE);
		rightMid.setUserData(FLOOR_BODY);
		rightMid.rotate(Math.PI / 2);
		rightMid.translate(-2.5, -1.8);
		this.world.addBody(rightMid);
		World thisWorld = this.world;
		wheel1 = new Wheel(thisWorld, 5, 0, Color.red, Color.blue, floor);
		wheel2 = new Wheel(thisWorld, 0, 0, Color.green, Color.orange, floor);
		
		wheel1.checkBallColissions();
		wheel1.checkFloorColissions();
		wheel1.persistingFloorColissions();
		
		wheel2.checkBallColissions();
		wheel2.checkFloorColissions();
		wheel2.persistingFloorColissions();
	}
	
	protected void update(Graphics2D g, double elapsedTime) {
		// apply a torque based on key input
		wheel1.updateBall(wheel2);
		wheel2.updateBall(wheel1);
		super.update(g, elapsedTime);
	}

	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		BallCollider simulation = new BallCollider();
		simulation.run();
	}
}
