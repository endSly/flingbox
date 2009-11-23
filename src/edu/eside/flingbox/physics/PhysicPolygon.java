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

import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider;
import edu.eside.flingbox.physics.collisions.ColliderPolygon;

/**
 * Implements physics properties for polygols.
 *
 */
public class PhysicPolygon extends PhysicBody {
	/** Polygon's Contour */
	private final Vector2D[] mPolygonContour;

	private final Vector2D[] mRotatedPolygonContour;
	
	/**
	 * Constructor physics for default polygon.
	 * 
	 * @param points polygon's points
	 * @param bodyMass polygon's mass
	 * @param position Polygon's start position
	 * @param listener Lister to be called when movement occurs
	 */
	public PhysicPolygon(final Vector2D[] points, final float bodyMass, 
			final Vector2D position, final OnMovementListener listener) {
		super(bodyMass, position);
		
		final int pointsCount = points.length;
		final Vector2D[] polygonVectors = new Vector2D[pointsCount];
		final Vector2D[] rotatedPolygonContour = new Vector2D[pointsCount];
		
		
		// Stroes points into Vector array.
		for (int i = 0; i < pointsCount; i++) {
			polygonVectors[i] = new Vector2D(points[i].i, points[i].j);
			rotatedPolygonContour[i] = new Vector2D(polygonVectors[i]);
		}
			
		// Sets polygon's properties
		mPolygonContour = polygonVectors;
		mRotatedPolygonContour = rotatedPolygonContour;
		
		mListener = listener;
		mCollider = new ColliderPolygon(mRotatedPolygonContour, this);
		
		mListener.onMovement(mPosition, 0f);
		mCollider.onMovement(mPosition, 0f);

		mAngularMass = computeAngularMass(bodyMass, mCollider);

	}
	
	/**
	 * TODO: Computes Angular mass for current polygon
	 * 
	 * @param mass Body's mass
	 * @param collider Body's Collider
	 * @return Polygon's angular mass
	 */
	private static float computeAngularMass(float mass, Collider collider) {
		final float radius = collider.getBoundingCircle();
		return 0.8f * mass * radius * radius;
	}
	
	/**
	 * Check if point is contained by the polygon
	 * 
	 * @param p point to check
	 * @return true if is containded
	 */
	public boolean contains(Vector2D p) {
		return PolygonUtils.polygonConatinsPoint(mPolygonContour, new Vector2D(p.i - mPosition.i, p.j - mPosition.j));
	}
	

	@Override
	public void applyForce(Vector2D force, Vector2D applicationPoint, float dt) {
		if (!mIsEnabled)
			return;
		
		if (mIsMoveable) 
			// Sets velocity and position
			mVelocity.add((new Vector2D(force)).mul(dt / mMass));
		
		if (mIsRotable) 
			// Sets angular velocity and rotation
			mAngularVelocity += force.crossProduct(applicationPoint) * dt / mAngularMass;
		
	}

	@Override
	public void applyForce(Vector2D force, float dt) {
		if (!mIsEnabled)
			return;
		
		if (mIsMoveable) {
			// Sets velocity and position
			mVelocity.add((new Vector2D(force)).mul(dt / mMass));
		}
	}
	
	@Override
	public void setDensity(float density) {
		super.setDensity(density);
		mAngularMass = computeAngularMass(mMass, mCollider);
	}
	
	/**
	 * Called when object has been updated
	 */
	public synchronized void onUpdateBody(float time) {
		mPosition.add((new Vector2D(mVelocity)).mul(time));
		mAngle += mAngularVelocity * time;

		mCollider.onMovement(mPosition, mAngle);
		mListener.onMovement(mPosition, mAngle);
	}
	
}
