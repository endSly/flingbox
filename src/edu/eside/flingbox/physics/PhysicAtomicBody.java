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

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider;

/**
 * Handles methods for an atomic body that can't be rotated
 *
 */
abstract class PhysicAtomicBody {
	/** Objects with INFINITE_MASS should be impossible to move */
	public final static float INFINITE_MASS = Float.POSITIVE_INFINITY;
	
	/** Sets if objects should interactue */
	protected boolean mIsEnabled = true;
	/** Sets if objects can be moved */
	protected boolean mIsMoveable = true;

	
	/** friction to be applied when body is moving */
	protected float mDynamicFrictionCoeficient;
	/** friction to be applied when body isn's moving */
	protected float mStaticFrictionCoeficient;
	
	/** Restitution Coeficient is a fractional value representing the ratio 
	 * of velocities before and after an impact */
	protected float mRestitutionCoeficient;
	
	/** Object's current position */
	protected final Vector2D mPosition = new Vector2D();
	
	/** Body's mass */
	protected float mMass = 0f;
	/** Body's velocity */
	protected final Vector2D mVelocity = new Vector2D();
	
	protected final Vector2D mAcomulatedImpulse = new Vector2D();
	
	/** Collider for this body */
	protected Collider mCollider;

	
	/**
	 * Called to refresh object's position
	 * @param time in seconds since last update
	 */
	public void onUpdateBody(float time) {
		if (!mIsEnabled || !mIsMoveable) // Nothing to do
			return;
		
		mVelocity.add(new Vector2D(mAcomulatedImpulse).mul(1f / mMass));
		mPosition.add(new Vector2D(mVelocity).mul(time));
		
		mAcomulatedImpulse.set(0f, 0f);
	}
	
	/**
	 * Applies force to the object
	 * 
	 * @param force Force
	 * @param dt time period while force is applied
	 */
	public void applyImpulse(Vector2D impulse) {
		mAcomulatedImpulse.add(impulse);
	}
	
	/**
	 * Check if point is contained by the polygon
	 * 
	 * @param p point to check
	 * @return true if is contained
	 */
	public boolean contains(Vector2D p) {
		return false; // Atomic body cannot be contained
	}
	
	public Vector2D getImpulse() {
		return new Vector2D(mVelocity).mul(mMass);
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
	 * @return Body's mass
	 */
	public float getBodyMass() {
		return mMass;
	}
	
	public float getDynamicFrictionCoeficient() {
		return mDynamicFrictionCoeficient;
	}
	
	public void setDynamicFrictionCoeficient(float f) {
		mDynamicFrictionCoeficient = f;
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
	
}
