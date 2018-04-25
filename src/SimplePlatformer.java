
/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

/**
 * A simple scene of a circle that is controlled by the left and
 * right arrow keys that is moved by applying torques and forces.
 * <p>
 * Also illustrated here is how to track whether the body is in
 * contact with the "ground."
 * <p>
 * Always keep in mind that this is just an example, production
 * code should be more robust and better organized.
 * @author William Bittle
 * @since 3.2.5
 * @version 3.2.0
 */
public class SimplePlatformer extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -313391186714427055L;

	/**
	 * Default constructor for the window
	 */
	public SimplePlatformer() {
		super("Simple Platformer", 32.0);

		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
	}

	private SimulationBody wheel;
	private SimulationBody wheel2;

	private final AtomicBoolean leftPressed = new AtomicBoolean(false);
	private final AtomicBoolean rightPressed = new AtomicBoolean(false);
	private final AtomicBoolean isOnGround = new AtomicBoolean(false);
	private final AtomicBoolean upPressed = new AtomicBoolean(false);
	private final AtomicBoolean leftPressed2 = new AtomicBoolean(false);
	private final AtomicBoolean rightPressed2 = new AtomicBoolean(false);
	private final AtomicBoolean isOnGround2 = new AtomicBoolean(false);
	private final AtomicBoolean upPressed2 = new AtomicBoolean(false);
	private final AtomicBoolean ballsCollided = new AtomicBoolean(false);

	private static final Color WHEEL_OFF_COLOR = Color.MAGENTA;
	private static final Color WHEEL_ON_COLOR = Color.GREEN;
	private static final Color WHEEL2_OFF_COLOR = Color.ORANGE;
	private static final Color WHEEL2_ON_COLOR = Color.CYAN;

	private static final Object FLOOR_BODY = new Object();

	/**
	 * Custom key adapter to listen for key events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				leftPressed.set(true);
				break;
			case KeyEvent.VK_RIGHT:
				rightPressed.set(true);
				break;
			case KeyEvent.VK_UP:
				upPressed.set(true);
				break;
			case KeyEvent.VK_W:
				upPressed2.set(true);
				break;
			case KeyEvent.VK_A:
				leftPressed2.set(true);
				break;
			case KeyEvent.VK_D:
				rightPressed2.set(true);
				break;

			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				leftPressed.set(false);
				break;
			case KeyEvent.VK_RIGHT:
				rightPressed.set(false);
				break;
			case KeyEvent.VK_UP:
				upPressed.set(false);
				break;
			case KeyEvent.VK_W:
				upPressed2.set(false);
				break;
			case KeyEvent.VK_A:
				leftPressed2.set(false);
				break;
			case KeyEvent.VK_D:
				rightPressed2.set(false);
				break;
			}
		}
	}

	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// the floor
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

		// the wheel
		wheel = new SimulationBody(WHEEL_OFF_COLOR);
		// NOTE: lots of friction to simulate a sticky tire
		wheel.addFixture(Geometry.createPolygonalCapsule(4, 0.5, 0.5), 1.0, 20.0, 0.1);
		wheel.setMass(MassType.NORMAL);
		this.world.addBody(wheel);
		wheel.shift(new Vector2(5, 0));

		//second wheel- made by ankit 
		wheel2 = new SimulationBody(WHEEL2_OFF_COLOR);

		wheel2.addFixture(Geometry.createPolygonalCapsule(4, 0.5, 0.5), 1.0, 20.0, 0.1);

		wheel2.setMass(MassType.NORMAL);

		this.world.addBody(wheel2);

		this.world.addListener(new StepAdapter() {
			@Override
			public void begin(Step step, World world) {
				// at the beginning of each world step, check if the body is in
				// contact with any of the floor bodies
				boolean isGround = false;

				List<Body> bodies =  wheel.getInContactBodies(false);

				for (int i = 0; i < bodies.size(); i++) {
					if (bodies.get(i).getUserData() == FLOOR_BODY || 
							bodies.get(i).equals(mid) || bodies.get(i).equals(rightMid)
							|| bodies.get(i).equals(leftMid)) {
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


		this.world.addListener(new StepAdapter() {
			@Override
			public void begin(Step step, World world) {
				// at the beginning of each world step, check if the body is in
				// contact with any of the floor bodies
				boolean isGround2 = false;

				List<Body> bodies2 =  wheel2.getInContactBodies(false);

				for (int i = 0; i < bodies2.size(); i++) {
					if (bodies2.get(i).getUserData() == FLOOR_BODY || 
							bodies2.get(i).equals(mid) || bodies2.get(i).equals(rightMid)
							|| bodies2.get(i).equals(leftMid)) {
						isGround2 = true;
						break;
					}

				}

				if (!isGround2) {
					// if not, then set the flag, and update the color
					isOnGround2.set(false);					
				}
			}
		});

		this.world.addListener(new StepAdapter() {
			@Override
			public void begin(Step step, World world) {
				// at the beginning of each world step, check if the body is in
				// contact with any of the floor bodies
				ballsCollided.set(false);
				List<Body> bodies2 = wheel2.getInContactBodies(false);

				for (int i = 0; i < bodies2.size(); i++) {
					if (bodies2.get(i).equals(wheel)) {
						ballsCollided.set(true);
						break;
					}

				}
			}
		});

		// then, when a contact is created between two bodies, check if the bodies
		// are floor and wheel, if so, then set the color and flag
		this.world.addListener(new ContactAdapter() {
			private boolean isContactWithFloor(ContactPoint point) {
				if ((point.getBody1() == wheel || point.getBody2() == wheel) &&
						(point.getBody1().getUserData() == FLOOR_BODY || point.getBody2().getUserData() == FLOOR_BODY)) {
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

		this.world.addListener(new ContactAdapter() {
			private boolean isContactWithFloor(ContactPoint point) {
				if ((point.getBody1() == wheel2 || point.getBody2() == wheel2) &&
						(point.getBody1().getUserData() == FLOOR_BODY || point.getBody2().getUserData() == FLOOR_BODY))
						
					{
						return true;
					
					}
					return false;
						}


				@Override
				public boolean persist(PersistedContactPoint point) {
					if (isContactWithFloor(point)) {
						
						isOnGround2.set(true);
					}
					return super.persist(point);
				}

				@Override
				public boolean begin(ContactPoint point) {
					if (isContactWithFloor(point)) {
						isOnGround2.set(true);
					}
					return super.begin(point);
				}
			});
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
		 */


		private void updateBall1()
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

			if (this.ballsCollided.get())
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

		private void updateBall2()
		{
			double sideSpeed = 10;
			double jumpSpeed = 2;

			if (this.leftPressed2.get() && wheel2.getLinearVelocity().getXComponent().x > -sideSpeed) {
				wheel2.applyForce(new Vector2(-sideSpeed, 0));
			}
			if (this.rightPressed2.get() && wheel2.getLinearVelocity().getXComponent().x < sideSpeed) {
				wheel2.applyForce(new Vector2(sideSpeed, 0));
			}
			if (this.upPressed2.get() &&this.isOnGround2.get())
			{
				Vector2 force = new Vector2(0, jumpSpeed);
				wheel2.applyImpulse(force);

			}

			if (this.ballsCollided.get())
			{
				Vector2 currentForce = wheel2.getAccumulatedForce();
				currentForce.x = currentForce.x;
				currentForce.y = -currentForce.y;
				wheel.clearAccumulatedForce();
				wheel.clearForce();
				wheel.applyImpulse(currentForce);
			}

			if (this.isOnGround2.get()) {
				wheel2.setColor(WHEEL2_ON_COLOR);
			} else {
				wheel2.setColor(WHEEL2_OFF_COLOR);
			}
		}
		protected void update(Graphics2D g, double elapsedTime) {
			// apply a torque based on key input
			updateBall1();
			updateBall2();
			super.update(g, elapsedTime);
		}

		/**
		 * Entry point for the example application.
		 * @param args command line arguments
		 */
		public static void main(String[] args) {
			SimplePlatformer simulation = new SimplePlatformer();
			simulation.run();
		}
	}

