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
public abstract class PhysicBody extends PhysicAtomicBody{
	/** Implements on move callback. */
	public interface OnMovementListener {
		/**
		 * Called when movement occurs.
		 * @param position new body's position
		 * @param rotation new body's angle
		 */
		public void onMovement(Vector2D position, float angle);
	}
	
	
	/** Objects with INFINITE_ANGULAR_MASS should be imposible to rotate */
	public final static float INFINITE_ANGULAR_MASS = Float.POSITIVE_INFINITY;
	
	/** Sets if objects can be rotated */
	protected boolean mIsRotable = true;

	/** Object's current rotated angle */
	protected float mAngle = 0f;
	
	/** body's mass by square unit */
	protected float mDensity;
	/** body's volume */
	protected final float mVolume;

	/** Body's angular mass */
	protected float mAngularMass = 0f;
	/** Body's current velocity */
	
	
	/** Body's current angular Velocity */
	protected float mAngularVelocity = 0.0f;
	
	protected float mAcomulatedRotationalImpulse = 0f;
	
	/**OnMovement callback listener */
	protected OnMovementListener mListener;

	
	
	/**
	 * Local constructor for any abstract body
	 * 
	 * @param bodyMass body's mass
	 * @param position body's position
	 */
	protected PhysicBody(final float bodyVolume, final Vector2D position) {
		mVolume = bodyVolume;
		mRestitutionCoeficient = Preferences.defaultRestitutionCoeficient;
		mDensity = Preferences.defaultDensity;
		mDynamicFrictionCoeficient = Preferences.defaultDynamicFrictionCoeficient;
		mStaticFrictionCoeficient = Preferences.defaultStaticFrictionCoeficient;
		mPosition.set(position);
		mMass = bodyVolume * mDensity;
	}

	/**
	 * Called to refresh object's position
	 * @param time in seconds since last update
	 */
	public void onUpdateBody(float time) {
		if (!mIsEnabled)
			return;

		super.onUpdateBody(time);
		
		if (mIsRotable) {
			mAngularVelocity += mAcomulatedRotationalImpulse / mAngularMass;
			mAngle += mAngularVelocity * time;
			mAcomulatedRotationalImpulse = 0f;
		}

		if (mIsMoveable || mIsRotable) {
			mCollider.onMovement(mPosition, mAngle);
			mListener.onMovement(mPosition, mAngle);
		}
	}
	
	/**
	 * Applies force to the object
	 * 
	 * @param force Force
	 * @param applicationPoint relative point in wich force is applied
	 * @param dt time period while force is applied
	 */
	public void applyImpulse(Vector2D impulse, Vector2D applicationPoint) {
		mAcomulatedImpulse.add(impulse);
		
		mAcomulatedRotationalImpulse  += impulse.crossProduct(applicationPoint);
	}
	

	
	/** Fixs body, making impossible to move  */
	public void setBodyFixed(boolean fixed) {
		mIsMoveable = !fixed;
		mIsRotable = !fixed;

		if (fixed) {
			// Stop current object
			mVelocity.set(0f, 0f);
			mAngularVelocity = 0f;
			//This will make object fixed
		} 
		
	}
	
	/** @return true if body can is fixed */
	public boolean isFixed() {
		return !(mIsMoveable || mIsRotable);
	}
	
	/**
	 * @return Body's angular mass
	 */
	public float getAngularMass() {
		return mAngularMass;
	}
	
	public float getDensity() {
		return mDensity;
	}
	
	public void setDensity(float density) {
		mDensity = density;
		mMass = mVolume * density;
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
	
	/** @return Body's angular velocity */
	public float getAngularVelocity() {
		return mAngularVelocity;
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
