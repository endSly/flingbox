package edu.eside.flingbox.physics.collisions;

import java.util.ArrayList;

public class SceneCollider {
	private final ArrayList<Collider> mInCollisionObjects;
	
	public SceneCollider() {
		mInCollisionObjects = new ArrayList<Collider>();
	}
	
	public SceneCollider(Collider collider) {
		mInCollisionObjects = new ArrayList<Collider>();
		mInCollisionObjects.add(collider);
	}
	
	public SceneCollider(Collider[] colliders) {
		mInCollisionObjects = new ArrayList<Collider>();
		for (Collider c : colliders)
			mInCollisionObjects.add(c);
	}
	
	public void add(Collider collider) {
		mInCollisionObjects.add(collider);
	}
	
	public void add(Collider[] colliders) {
		for (Collider c : colliders)
			mInCollisionObjects.add(c);
	}
	
	public boolean remove(Collider collider) {
		return mInCollisionObjects.remove(collider);
	}
	
	public int checkCollisions() {
		final int objectsCount = mInCollisionObjects.size();
		int collisionsCount = 0;
		for (int i = 0 ; i < objectsCount; i++) 
			for (int j = i + 1; j < objectsCount; j++) {
				if (mInCollisionObjects.get(i).checkCollision(mInCollisionObjects.get(j)))
					collisionsCount++;
			}
		return collisionsCount;

	}
}
