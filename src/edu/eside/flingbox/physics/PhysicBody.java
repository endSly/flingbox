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
import edu.eside.flingbox.math.PolygonUtils;
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
	
	/** Objects with INFINITE_MASS should be impossible to move */
	protected final static float INFINITE_MASS = Float.POSITIVE_INFINITY;
	/** Objects with INFINITE_ANGULAR_MASS should be imposible to rotate */
	protected final static float INFINITE_ANGULAR_MASS = Float.POSITIVE_INFINITY;
	
	/** Sets if objects can be moved */
	protected boolean mIsMoveable = true;
	/** Sets if objects can be rotated */
	protected boolean mIsRotable = true;
	/** Sets if objects should interactue */
	protected boolean mIsEnabled = true;
	
	/** Restitution Coeficient is a fractional value representing the ratio 
	 * of velocities before and after an impact */
	protected float mRestitutionCoeficient = 1.0f;
	
	/** Object's current position */
	protected final Vector2D mPosition;
	/** Object's current rotated angle */
	protected float mAngle = 0f;
	
	/** Body's mass */
	protected float mMass;
	/** Body's angular mass */
	protected float mAngularMass = 0f;
	/** Body's current velocity */
	protected final Vector2D mVelocity = new Vector2D();
	/** Body's current angular Velocity */
	protected float mAngularVelocity = 0.0f;
	/** Current applied force to the body */
	protected Vector2D mAppliedForce = new Vector2D();
	/** Current applied moment to he body*/
	protected float mAppliedMoment = 0.0f;
	
	/** Collider for this body */
	protected Collider mCollider;
	
	/**OnMovement callback listener */
	protected OnMovementListener mListener;
	
	/**
	 * Local constructor for any abstract body
	 * 
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
	 * Fixs body, making imposible to move mVelocityit
	 */
	public void fixObject() {
		mIsMoveable = false;
		mIsRotable = false;
		
		// Stop current object
		mVelocity.set(0f, 0f);
		mAngularVelocity = 0f;
		
		//This will make object fixed
		mMass = INFINITE_MASS;
		mAngularMass = INFINITE_ANGULAR_MASS;
		
	}
	 /**
	  * Adds force of gravity.
	  * @param gravityForce
	  */
	public synchronized void applyGravity(Vector2D gravityForce) {
		if (mIsMoveable)
			mAppliedForce.add(gravityForce);
	}
	
	/**
	 * Applies force to the object
	 * 
	 * @param force Force
	 * @param applicationPoint relative point in wich force is applied
	 */
	public synchronized void applyForce(Vector2D force, Vector2D applicationPoint) {
		// Acomulate force
		mAppliedForce.add(force);
		
		// Calculate moment
		mAppliedMoment += applicationPoint.crossProduct(force);
	}
	
	/**
	 * Check if point is contained by the polygon
	 * 
	 * @param p point to check
	 * @return true if is containded
	 */
	public boolean contains(Point p) {
		return false; // Atomic body cannot be contained
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
	 * @return velocity
	 */
	public Vector2D getVelocity() {
		return mVelocity;
	}
	
	/**
	 * Sets velocity components
	 * 
	 * @param vx
	 * @param vy
	 */
	public void setVelocity(float vx, float vy) {
		mVelocity.set(vx, vy);
	}
	
	/**
	 * @return Body's absolute position
	 */
	public Vector2D getPosition() {
		return mPosition;
	}
	
	/**
	 * @param position new postion of the object
	 */
	public void setPosition(float x, float y) {
		mPosition.set(x, y);
		onUpdateBody(0);
	}
	
	/**
	 * @return Body's angle
	 */
	public float getAngle() {
		return mAngle;
	}
	
	/**
	 * @param angle new object's angle
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}
	
	/**
	 * Computes current object's energy
	 *  
	 * @return energy in juls
	 */
	public float getEnergy() {
		final float velocity = mVelocity.length() ;
		final float kineticEnergy = 0.5f * mMass * velocity * velocity;
		final float kineticRotationalEnergy = 0.5f * mAngularMass * mAngularVelocity * mAngularVelocity;
		return kineticEnergy + kineticRotationalEnergy;
	}
	
	/**
	 * @return the Collider
	 */
	public Collider getCollider() {
		return mCollider;
	}

}
