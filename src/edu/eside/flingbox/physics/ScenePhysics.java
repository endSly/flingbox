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

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.SceneCollider;
import edu.eside.flingbox.physics.gravity.GravitySource;

/**
 * Stores all physic object in scene and make those 
 * interact.
 * ScenePhysics manage thread for update objects 
 */
public class ScenePhysics implements Runnable {
	public GravitySource mGravity;
	
	// List of physical objects on scene
	private final ArrayList<PhysicBody> mOnSceneBodys;
	// Collision manager for current scene
	private final SceneCollider mCollider;

	// Thread for simulation
	private Thread mSimulationThread;
	// Flags for Stopping simulation
	private boolean mDoKill = false;
	private boolean mIsSimulating = false;
	
	/**
	 * Initializes an empty 
	 */
	public ScenePhysics(GravitySource gravity) {
		mOnSceneBodys = new ArrayList<PhysicBody>();
		mCollider = new SceneCollider();
		mGravity = gravity;
	}
	
	/**
	 * Intializes scene with one object 
	 * @param object first object
	 */
	public ScenePhysics(GravitySource gravity, PhysicBody object) {
		mOnSceneBodys = new ArrayList<PhysicBody>();
		mCollider = new SceneCollider();
		mGravity = gravity;
		this.add(object);
	}
	
	/**
	 * Intializes scene with array of objects 
	 * @param objects array of objects
	 */
	public ScenePhysics(GravitySource gravity, PhysicBody[] objects) {
		mOnSceneBodys = new ArrayList<PhysicBody>();
		mCollider = new SceneCollider();
		
		mGravity = gravity;
		this.add(objects);
	}
	
	/**
	 * Adds physical object
	 * @param object object to be added
	 */
	public void add(PhysicBody object) {
		mOnSceneBodys.add(object);
		mCollider.add(object.getCollider());
	}
	
	/**
	 * Adds an array of objects
	 * @param objects array of objects to be added
	 */
	public void add(PhysicBody[] objects) {
		for (PhysicBody object : objects) {
			mOnSceneBodys.add(object);
			mCollider.add(object.getCollider());
		}
	}
	
	/**
	 * Starts simulation
	 */
	public void startSimulation() {
		System.gc();
		mSimulationThread = new Thread(this);

		mDoKill = false;
		if (mIsSimulating) 
			return;
		
		mIsSimulating = true;
		mSimulationThread.start();
	}
	
	/**
	 * Sends message to kill and waits
	 */
	public void stopSimulation() {
		if (!mIsSimulating)
			return;
		mDoKill = true;
		
		System.gc(); // Good moment to call to GC
		while (mIsSimulating) { }	// Wait until thread ends.
		
		mSimulationThread = null;
	}
	
	/**
	 * @return true if simulating
	 */
	public boolean isSimulating() {
		return mIsSimulating;
	}
	
	/**
	 * Thread for simulation
	 */
	@Override
	public void run() {
		final ArrayList<PhysicBody> bodys = mOnSceneBodys;
		long lastTime = System.currentTimeMillis();
		long time;
		final Vector2D force = new Vector2D();
		for (; !mDoKill; ) {
			// Compute time
			time = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();
			/* first apply gravity */
			for (PhysicBody body : bodys) {
				force.set(mGravity);
				body.applyForce(force.mul(body.getBodyMass()),
								(float) time / 1000f);
			}
			
			/* Then apply collisions forces */
			mCollider.checkCollisions();
			
			/* Last update body */
			for (PhysicBody body : bodys)
				body.onUpdateBody((float) time / 1000f);
			
			try {
				if (time < 20)
					Thread.sleep(20 - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mDoKill = false;
		mIsSimulating = false;
	}
	
}
