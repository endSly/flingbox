package edu.eside.flingbox.physics;

import java.util.ArrayList;

import edu.eside.flingbox.physics.collisions.SceneCollider;
import edu.eside.flingbox.physics.force.Force;

public final class ScenePhysics implements Runnable {
	public static final Force GRAVITY_EARTH = new Force();
	
	private final ArrayList<PhysicObject> mOnSceneBodys;
	private final SceneCollider mCollider;
	
	private Force mGravity;
	
	private final Thread mSimulationThread;
	private boolean mDoKill;
	
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
		
		mSimulationThread.start();
		mDoKill = false;
	}
	
	public void stopSimulation() {
		mDoKill = true;
	}

	@Override
	public void run() {
		for (; !mDoKill; ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mDoKill = false;
		
	}
	
}
