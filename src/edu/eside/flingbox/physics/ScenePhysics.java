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

import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.SceneCollider;
import edu.eside.flingbox.physics.force.Force;

public final class ScenePhysics implements Runnable {
	public static final Vector2D GRAVITY_EARTH = new Vector2D(0f, -9.81f * 630f);
	
	private final ArrayList<PhysicObject> mOnSceneBodys;
	private final SceneCollider mCollider;
	
	private Force mGravity;
	
	private final Thread mSimulationThread;
	private boolean mDoKill = false;
	private boolean mIsSimulating = false;
	
	public ScenePhysics() {
		mOnSceneBodys = new ArrayList<PhysicObject>();
		mSimulationThread = new Thread(this);
		mCollider = new SceneCollider();
	}
	
	public ScenePhysics(PhysicObject object) {
		mOnSceneBodys = new ArrayList<PhysicObject>();
		mCollider = new SceneCollider(object.getCollider());
		mCollider.add(object.getCollider());
		mSimulationThread = new Thread(this);
		
	}
	
	public ScenePhysics(PhysicObject[] objects) {
		mOnSceneBodys = new ArrayList<PhysicObject>();
		mCollider = new SceneCollider();
		for (PhysicObject object : objects) {
			mOnSceneBodys.add(object);
			mCollider.add(object.getCollider());
		}
		mSimulationThread = new Thread(this);
	}
	
	public void add(PhysicObject object) {
		mOnSceneBodys.add(object);
		mCollider.add(object.getCollider());
	}
	
	public void add(PhysicObject[] objects) {
		for (PhysicObject object : objects) {
			mOnSceneBodys.add(object);
			mCollider.add(object.getCollider());
		}
	}
	
	public void startSimulation() {
		System.gc();
		
		mDoKill = false;
		mSimulationThread.start();
		mIsSimulating = true;
	}
	
	public void stopSimulation() {
		mDoKill = true;
		while (mDoKill) { }	// Wait until thread ends.
	}
	
	public boolean isSimulating() {
		return mIsSimulating;
	}
	
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		for (; !mDoKill; ) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time = System.currentTimeMillis() - time;
			for (PhysicObject object : mOnSceneBodys) {
				object.applyForce(GRAVITY_EARTH);
				object.onUpdateBody(time);
			}
			mCollider.checkCollisions();
			time = System.currentTimeMillis();
		}
		mDoKill = false;
		mIsSimulating = false;
		
	}
	
}
