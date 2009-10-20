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

import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider;
import edu.eside.flingbox.physics.collisions.Collider.OnCollideListener;

/**
 * Abstract class with handles all properties that any 
 * physical body should have.
 */
public abstract class PhysicBody implements OnCollideListener {
	
	/**
	 * Implements on move callback.
	 */
	public interface OnMovementListener {
		/**
		 * Called when movement occurs.
		 * @param position new body's position
		 * @param rotation new body's angle
		 */
		public void onMovement(Vector2D position, float angle);
	}
	
	/** Object with INFINITE_MASS should be impossible to move */
	protected final static float INFINITE_MASS = Float.POSITIVE_INFINITY;
	protected final static float INFINITE_ANGULAR_MASS = Float.POSITIVE_INFINITY;
	
	protected boolean mIgnoreGravity = false;
	
	// Store body's position with rotational angle
	protected final Vector2D mPosition;
	protected float mAngle = 0f;
	
	// Store body's mass and angular mass
	protected float mMass;
	protected float mAngularMass = 0f;
	
	// Store Variables for movement
	protected final Vector2D mVelocity = new Vector2D();
	protected float mAngularVelocity = 0.0f;
	
	protected Vector2D mAppliedForce = new Vector2D();
	protected float mAppliedMoment = 0.0f;
	
	// Body's collider
	protected Collider mCollider;
	
	// OnMovement callback listener
	protected OnMovementListener mListener;
	
	/**
	 * Local constructor for any abstract body
	 * @param bodyMass body's mass
	 * @param position body's position
	 */
	protected PhysicBody(final float bodyMass, final Point position) {
		mMass = bodyMass;
		mAngularMass = 0.0f;
		
		mPosition = new Vector2D(position.x, position.y);
	}
	
	/**
	 * Called to refresh object's position
	 * @param time time in ms since las update
	 */
	public abstract void onUpdateBody(float time);
	
	/**
	 * Fixs body, making imposible to move it
	 */
	public void fixObject() {
		mIgnoreGravity = true;
		mMass = INFINITE_MASS;
		mAngularMass = INFINITE_ANGULAR_MASS;
		
	}
	 /**
	  * Adds force of gravity.
	  * @param gravityForce
	  */
	public synchronized void applyGravity(Vector2D gravityForce) {
		if (!mIgnoreGravity)
			mAppliedForce.add(gravityForce);
	}
	
	/**
	 * Applies force to the object
	 * @param force Force
	 * @param applicationPoint relative point in wich force is applied
	 */
	public synchronized void applyForce(Vector2D force, Vector2D applicationPoint) {
		// Acomulate force
		mAppliedForce.add(force);
		
		// Calculate moment
		final float distance = force.distanceToPoint(applicationPoint);
		// Acomulate moment
		mAppliedMoment += (force.length() * distance);
	}
	
	/**
	 * @return Body's mass
	 */
	public float getBodyMass() {
		return mMass;
	}
	
	/**
	 * @return Body's angular mass
	 */
	public float getAngularMass() {
		return mAngularMass;
	}
	
	/**
	 * @return Body's absolute position
	 */
	public Vector2D getPosition() {
		return mPosition;
	}
	
	/**
	 * @return Body's angle
	 */
	public float getAngle() {
		return mAngle;
	}
	
	/**
	 * @return the Collider
	 */
	public Collider getCollider() {
		return mCollider;
	}
	
	


}
