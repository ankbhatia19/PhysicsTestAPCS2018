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


public class Wheel extends SimulationBody
{
	private final AtomicBoolean leftPressed;
	private final AtomicBoolean rightPressed;
	private final AtomicBoolean upPressed;
	private final AtomicBoolean collided;
	private final AtomicBoolean isOnGround;

	private SimulationBody wheel;
	private Object thisFloor;

	private World thisWorld;

	private static Color WHEEL_OFF_COLOR;
	private static Color WHEEL_ON_COLOR;

	public Wheel(World world, int x, int y, Color color1, Color color2, Object floor)
	{
		thisWorld = world;
		thisFloor = floor;

		leftPressed = new AtomicBoolean(false);
		rightPressed = new AtomicBoolean(false);
		upPressed = new AtomicBoolean(false);
		collided = new AtomicBoolean(false);
		isOnGround = new AtomicBoolean(false);

		WHEEL_OFF_COLOR = color1;
		WHEEL_ON_COLOR = color2;

		// the wheel
		wheel = new SimulationBody(WHEEL_OFF_COLOR);
		// NOTE: lots of friction to simulate a sticky tire
		wheel.addFixture(Geometry.createCircle(0.5), 1.0, 20.0, 0.1);
		wheel.setMass(MassType.NORMAL);
		thisWorld.addBody(wheel);
		wheel.translate(x, y);
		

	}

	public void setLeft(boolean set)
	{
		leftPressed.set(set);
	}
	public void setRight(boolean set)
	{
		rightPressed.set(set);
	}
	public void setUp(boolean set)
	{
		upPressed.set(set);
	}

	public void checkFloorColissions()
	{
	thisWorld.addListener(new StepAdapter() {
		@Override
		public void begin(Step step, World world) {
			// at the beginning of each world step, check if the body is in
			// contact with any of the floor bodies
			boolean isGround = false;

			List<Body> bodies =  wheel.getInContactBodies(false);

			for (int i = 0; i < bodies.size(); i++) {
				if (bodies.get(i).getUserData() == thisFloor) 
				{
					isGround = true;
					break;
				}

			}

			if (!isGround) {
				// if not, then set the flag, and update the color
				isOnGround.set(false);					
			}
		}
	});
	}
	
	public void persistingFloorColissions()
	{
		thisWorld.addListener(new ContactAdapter() {
			private boolean isContactWithFloor(ContactPoint point) {
				if ((point.getBody1() == wheel || point.getBody2() == wheel) &&
						(point.getBody1().getUserData() == thisFloor || point.getBody2().getUserData() == thisFloor)) {
					return true;
				}
				return false;
			}


			@Override
			public boolean persist(PersistedContactPoint point) {
				if (isContactWithFloor(point)) {
					isOnGround.set(true);
				}
				return super.persist(point);
			}

			@Override
			public boolean begin(ContactPoint point) {
				if (isContactWithFloor(point)) {
					isOnGround.set(true);
				}
				return super.begin(point);
			}
		});
	}
	
	public void checkBallColissions()
	{
		thisWorld.addListener(new StepAdapter() {
			@Override
			public void begin(Step step, World world) {
				// at the beginning of each world step, check if the body is in
				// contact with any of the floor bodies
				collided.set(false);
				List<Body> bodies2 = wheel.getInContactBodies(false);

				for (int i = 0; i < bodies2.size(); i++) {
					if (bodies2.get(i).equals(wheel)) {
						collided.set(true);
						break;
					}

				}
			}
		});
	}
	
	public void updateBall(Wheel wheel2)
	{
		double sideSpeed = 10;
		double jumpSpeed = 2;

		if (this.leftPressed.get() && wheel.getLinearVelocity().getXComponent().x > -sideSpeed) {
			wheel.applyForce(new Vector2(-sideSpeed, 0));
		}
		if (this.rightPressed.get() && wheel.getLinearVelocity().getXComponent().x < sideSpeed) {
			wheel.applyForce(new Vector2(sideSpeed, 0));
		}
		if (this.upPressed.get() && this.isOnGround.get())
		{
			Vector2 force = new Vector2(0, jumpSpeed);
			wheel.applyImpulse(force);
		}

		if (collided.get())
		{
			Vector2 currentForce = wheel.getAccumulatedForce();
			currentForce.x = currentForce.x;
			currentForce.y = -currentForce.y;
			wheel2.clearAccumulatedForce();
			wheel2.clearForce();
			wheel2.applyImpulse(currentForce);
		}

		if (this.isOnGround.get()) {
			wheel.setColor(WHEEL_ON_COLOR);
		} else {
			wheel.setColor(WHEEL_OFF_COLOR);
		}
	}

}

