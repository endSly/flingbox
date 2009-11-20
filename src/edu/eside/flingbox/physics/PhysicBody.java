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

import edu.eside.flingbox.Preferences;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider;

/**
 * Abstract class with handles all properties that any 
 * physical body should have.
 */
public abstract class PhysicBody {
	
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
	public final static float INFINITE_MASS = Float.POSITIVE_INFINITY;
	/** Objects with INFINITE_ANGULAR_MASS should be imposible to rotate */
	public final static float INFINITE_ANGULAR_MASS = Float.POSITIVE_INFINITY;
	
	/** Sets if objects can be moved */
	protected boolean mIsMoveable = true;
	/** Sets if objects can be rotated */
	protected boolean mIsRotable = true;
	/** Sets if objects should interactue */
	protected boolean mIsEnabled = true;
	
	/** Restitution Coeficient is a fractional value representing the ratio 
	 * of velocities before and after an impact */
	protected float mRestitutionCoeficient;
	
	/** body's mass by square unit */
	protected float mDensity;
	/** friction to be applied when body is moving */
	protected float mDinamicFrictionCoeficient = 0.25f;
	/** friction to be applied when body isn's moving */
	protected float mStaticFrictionCoeficient = 0.33f;
	
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
	protected PhysicBody(final float bodyMass, final Vector2D position) {
		mMass = bodyMass;
		mRestitutionCoeficient = Preferences.defaultRestitutionCoeficient;
		mDensity = Preferences.defaultDensity;
		mPosition = new Vector2D(position);
		mDinamicFrictionCoeficient = Preferences.defaultDinamicFrictionCoeficient;
		mStaticFrictionCoeficient = Preferences.defaultStaticFrictionCoeficient;
	}
	
	/**
	 * Called to refresh object's position
	 * @param time time in ms since las update
	 */
	public abstract void onUpdateBody(float time);
	
	/**
	 * Applies force to the object
	 * 
	 * @param force Force
	 * @param dt time period while force is applied
	 */
	public abstract void applyForce(Vector2D force, float dt);
	
	/**
	 * Applies force to the object
	 * 
	 * @param force Force
	 * @param applicationPoint relative point in wich force is applied
	 * @param dt time period while force is applied
	 */
	public abstract void applyForce(Vector2D force, Vector2D applicationPoint, float dt);
	
	/**
	 * Check if point is contained by the polygon
	 * 
	 * @param p point to check
	 * @return true if is containded
	 */
	public boolean contains(Vector2D p) {
		return false; // Atomic body cannot be contained
	}
	
	/** Fixs body, making imposible to move  */
	public void setBodyFixed() {
		mIsMoveable = false;
		mIsRotable = false;
		
		// Stop current object
		mVelocity.set(0f, 0f);
		mAngularVelocity = 0f;
		
		//This will make object fixed
		mMass = INFINITE_MASS;
		mAngularMass = INFINITE_ANGULAR_MASS;
		
	}
	
	/** Body can be moved  */
	public void setBodyMoveable() {
		mIsMoveable = true;
		mIsRotable = true;
	}
	
	/** @return true if body can is fixed */
	public boolean isFixed() {
		return !(mIsMoveable || mIsRotable);
	}
	
	/** @return true if is enabled */
	public boolean isEnabled() {
		return mIsEnabled;
	}
	
	/** Sets if object is enabled or not */
	public void setEnabled(boolean doEnable) {
		mIsEnabled = doEnable;
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
	
	public float getDinamicFrictionCoeficient() {
		return mDinamicFrictionCoeficient;
	}
	
	public void setDinamicFrictionCoeficient(float f) {
		mDinamicFrictionCoeficient = f;
	}
	
	public float getStaticFrictionCoeficient() {
		return mStaticFrictionCoeficient;
	}
	
	public void setStaticFrictionCoeficient(float f) {
		mStaticFrictionCoeficient = f;
	}
	
	/**
	 * @return body's Restitution Coeficient
	 */
	public float getRestitutionCoeficient() {
		return mRestitutionCoeficient;
	}
	
	/**
	 * Sets body's Restitution Coeficient
	 */
	public void setRestitutionCoeficient(float restCoef) {
		mRestitutionCoeficient = restCoef;
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
	 * @return Body's angular velocity
	 */
	public float getAngularVelocity() {
		return mAngularVelocity;
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
	 * @param position new postion of the object
	 */
	public void setPosition(Vector2D v) {
		mPosition.set(v);
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
		onUpdateBody(0);
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
