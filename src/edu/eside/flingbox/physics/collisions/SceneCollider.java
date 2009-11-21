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

package edu.eside.flingbox.physics.collisions;

import java.util.ArrayList;

public class SceneCollider {
	private final ArrayList<Collider> mInCollisionObjects;
	
	public SceneCollider() {
		mInCollisionObjects = new ArrayList<Collider>();
	}
	
	public SceneCollider(Collider collider) {
		mInCollisionObjects = new ArrayList<Collider>();
		this.add(collider);
	}
	
	public SceneCollider(Collider[] colliders) {
		mInCollisionObjects = new ArrayList<Collider>();
		this.add(colliders);
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
