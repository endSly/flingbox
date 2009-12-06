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

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.physics.PhysicBody.OnMovementListener;

/**
 * Abstract collision manager for any object.
 */
public abstract class Collider implements OnMovementListener {
	
	/** bounding circle radius, needed to discartd quickly collisions */
	protected float mRadius; 

	/** Objects position. needs to be updated */
	protected final Vector2D mPosition;
	protected float mAngle = 0f;
	 
	protected final PhysicBody mPhysicBody;
	
	/**
	 * Local constructor for any collider.
	 * 
	 * @param listener Collision listener
	 */
	public Collider(final PhysicBody physicBody) {
		mPosition = new Vector2D();
		mPhysicBody = physicBody;
	}
	
	/**
	 * Checks if objects can collide
	 * 
	 * @param collider other objects collider.
	 * @return true if objects can collide
	 */
	public abstract Contact[] checkContacts(final Collider collider);
	
	/**
	 * @return true if there are a chance of collision
	 */
	public boolean canContact(final Collider collider) {
		/* Start checking bounding circle */
		final float radiusLength = mRadius + collider.mRadius;
		final float thisX = mPosition.i, thisY = mPosition.j, 
			otherX = collider.mPosition.i, otherY = collider.mPosition.j;
		final float distanceSqr = (thisX - otherX) * (thisX - otherX) 
				+ (thisY - otherY) * (thisY - otherY);
		
		if (distanceSqr > (radiusLength * radiusLength))
			return false; // No collision possible

		return true;
		
	}
	
	/**
	 * Called when a movement occurs
	 */
	public void onMovement(final Vector2D newPosition, float newAngle) {
		mPosition.set(newPosition);
		mAngle = newAngle;
	}
	
	/**
	 * @return Bounding circle's radius
	 */
	public float getBoundingCircle() {
		return mRadius;
	}
	
	/**
	 * @return associated PhysicBody
	 */
	public PhysicBody getAssociatedBody() {
		return mPhysicBody;
	}
}
