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

import edu.eside.flingbox.math.Box2D;
import edu.eside.flingbox.math.Point;

public abstract class Collider {
	public interface OnCollideListener {
		
		public void onCollide(Collision collide);
	}
	
	// needed to discartd quickly collisions
	protected float mRadius; 
	protected Box2D mBoundingBox;
	
	protected final OnCollideListener mCollisionListener;
	
	protected final Point mPosition;
	
	public Collider(OnCollideListener listener) {
		mPosition = new Point();
		mCollisionListener = listener;
	}
	
	public boolean checkCollision(Collider collider) {
		/*
		 *  Start checking bounding circle
		 */
		final float radiusLength = mRadius + collider.mRadius;
		final float thisX = mPosition.x, thisY = mPosition.y, 
			otherX = collider.mPosition.x, otherY = collider.mPosition.y;
		final float distanceSqr = ((thisX - otherX) * (thisX - otherX) 
				+ (thisY - otherY) * (thisY - otherY));
		
		if (distanceSqr > (radiusLength * radiusLength))
			return false;	// No collision
		
		/*
		 * Check bounding Boxes.
		 * Check only the box's sides that we need
		 */
		
		
		return true;
		
	}
	
	public boolean isPointOver(Point p) {
		final float thisX = mPosition.x, thisY = mPosition.y, 
			pointX = p.x, pointY = p.y;
		final float distanceSqr = ((thisX - pointX) * (thisX - pointX) 
				+ (thisY - pointY) * (thisY - pointY));
		
		return (distanceSqr < (mRadius * mRadius));
	}
	
	public void setPosition(Point position) {
		mPosition.set(position.x, position.y);
	}
	
}
