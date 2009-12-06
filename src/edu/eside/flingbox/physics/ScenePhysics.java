/*
 *  Flingbox - An OpenSource physics sandbox for Google's Android
 *  Copyright (C) 2009  Jon Ander Peñalba & Endika Gutiérrez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.eside.flingbox.physics;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Arbiter;
import edu.eside.flingbox.physics.collisions.Contact;
import edu.eside.flingbox.physics.gravity.GravitySource;

/**
 * Stores all physic object in scene and make those 
 * interact.
 * ScenePhysics manage thread for update objects 
 */
public class ScenePhysics implements Runnable {
	public GravitySource mGravity;
	
	/** List of physical bodys on scene */
	private final ArrayList<PhysicBody> mOnSceneBodies = new ArrayList<PhysicBody>();
	/** Collision manager for current scene */
	private final Arbiter mArbiter = new Arbiter();
	
	/** Semaphore for lock writing on mOnSceneBodys */
	private Semaphore mLockOnSceneBodys = new Semaphore(1);

	/** Thread for simulation */
	private Thread mSimulationThread;
	/** Flag for kill simulation */
	private boolean mDoKill = false;
	/** Flag indicating if thread is running */
	private Semaphore mSimulationMutex = new Semaphore(1, true);
	
	/**
	 * Initializes an empty scene
	 */
	public ScenePhysics(GravitySource gravity) {
		mGravity = gravity;
		Contact.UPPER_POSITION_COMPARATOR.setGravity(gravity);
	}
	
	/**
	 * Adds physical object
	 * @param object object to be added
	 */
	public void add(PhysicBody object) {
		try {
			mLockOnSceneBodys.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mOnSceneBodies.add(object);
		mArbiter.add(object.getCollider());
		mLockOnSceneBodys.release();
	}
	
	/**
	 * Starts simulation
	 */
	public void startSimulation() {
		if (mSimulationThread != null && mSimulationThread.isAlive())
			return;
			
		mDoKill = false;
		mSimulationThread = new Thread(this);
		mSimulationThread.start();
	}
	
	/**
	 * Sends message to kill and waits
	 */
	public void stopSimulation() {
		mDoKill = true;
		try {
			mSimulationMutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mSimulationThread = null;
		mSimulationMutex.release();
		System.gc(); // Good moment to call to GC
	}
	
	/**
	 * @return true if simulating
	 */
	public boolean isSimulating() {
		return mSimulationThread != null ? mSimulationThread.isAlive() : false;
	}
	
	/**
	 * Thread for simulation
	 */
	@Override
	public void run() {
		final ArrayList<PhysicBody> bodies = mOnSceneBodies;
		long lastTime = System.currentTimeMillis();
		long time;
		final Vector2D force = new Vector2D();
		for (; !mDoKill; ) {
			try {
				mSimulationMutex.acquire();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			
			/* Compute time */
			time = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();
			
			/* We need a semaphore here */
			try {
				mLockOnSceneBodys.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			/* Apply gravity impulse */
			for (PhysicBody body : bodies) {
				force.set(mGravity);
				body.applyImpulse(force.mul(body.getBodyMass() * (float) time / 1000f));
			}
			mLockOnSceneBodys.release();
			
			/* Then apply collisions forces */
			mArbiter.checkCollisions();
			
			try {
				mLockOnSceneBodys.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			/* Last update body */
			for (PhysicBody body : bodies)
				body.onUpdateBody((float) time / 1000f);
			
			mLockOnSceneBodys.release();
			/* Keep max frame-rate */
			try {
				if (time < 20)
					Thread.sleep(20 - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			mSimulationMutex.release();
		}
		mDoKill = false;
	}
	
}
