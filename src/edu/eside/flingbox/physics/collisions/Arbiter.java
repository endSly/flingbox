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

public class Arbiter {
	private final ArrayList<Collider> mCollisionableBodys = new ArrayList<Collider>();
	
	public void add(final Collider collider) {
		mCollisionableBodys.add(collider);
	}
	
	public boolean remove(Collider collider) {
		return mCollisionableBodys.remove(collider);
	}
	
	public int checkCollisions() {
		final int objectsCount = mCollisionableBodys.size();
		int collisionsCount = 0;
		for (int i = 0 ; i < objectsCount; i++) 
			for (int j = i + 1; j < objectsCount; j++) {
				if (mCollisionableBodys.get(i).checkCollision(mCollisionableBodys.get(j)))
					collisionsCount++;
			}
		return collisionsCount;

	}
}
