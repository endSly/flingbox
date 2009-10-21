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

/**
 * Stores all physic object in scene and make those 
 * interact.
 * ScenePhysics manage thread for update objects 
 */
public class ScenePhysics implements Runnable {
	public static final Vector2D GRAVITY_EARTH = new Vector2D(0f, -9.81f * 30f);
	public static final Vector2D GRAVITY_MOON = new Vector2D(0f, -1.63f * 530f);
	
	// List of physical objects on scene
	private final ArrayList<PhysicBody> mOnSceneBodys;
	// Collision manager for current scene
	private final SceneCollider mCollider;

	// Thread for simulation
	private final Thread mSimulationThread;
	// Flags for Stopping simulation
	private boolean mDoKill = false;
	private boolean mIsSimulating = false;
	
	/**
	 * Initializes an empty 
	 */
	public ScenePhysics() {
		mOnSceneBodys = new ArrayList<PhysicBody>();
		mCollider = new SceneCollider();
		mSimulationThread = new Thread(this);
	}
	
	/**
	 * Intializes scene with one object 
	 * @param object first object
	 */
	public ScenePhysics(PhysicBody object) {
		this();
		mOnSceneBodys.add(object);
		mCollider.add(object.getCollider());
	}
	
	/**
	 * Intializes scene with array of objects 
	 * @param objects array of objects
	 */
	public ScenePhysics(PhysicBody[] objects) {
		this();
		for (PhysicBody object : objects) {
			mOnSceneBodys.add(object);
			mCollider.add(object.getCollider());
		}
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

		mDoKill = false;
		if (mIsSimulating) 
			return;
		
		mSimulationThread.start();
		mIsSimulating = true;
	}
	
	/**
	 * Sends message to kill and waits
	 */
	public void stopSimulation() {
		if (!mIsSimulating)
			return;
		mDoKill = true;
		while (mIsSimulating) { }	// Wait until thread ends.
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
		long time = System.currentTimeMillis();
		for (; !mDoKill; ) {
			// Compute time
			time = System.currentTimeMillis() - time;
			for (PhysicBody object : mOnSceneBodys) {
				object.applyGravity(new Vector2D(GRAVITY_EARTH).mul(object.getBodyMass()));
				object.onUpdateBody(time);
			}
			mCollider.checkCollisions();
			time = System.currentTimeMillis();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mDoKill = false;
		mIsSimulating = false;
	}
	
}
