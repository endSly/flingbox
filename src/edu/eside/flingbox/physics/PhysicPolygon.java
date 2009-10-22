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

import edu.eside.flingbox.math.Matrix22;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider;
import edu.eside.flingbox.physics.collisions.ColliderPolygon;
import edu.eside.flingbox.physics.collisions.Collision;
import edu.eside.flingbox.physics.collisions.Collider.OnCollideListener;

/**
 * Implements physics properties for polygols.
 *
 */
public class PhysicPolygon extends PhysicBody implements OnCollideListener {

	// Some physical values needed
	private final Vector2D[] mPolygonContour;
	
	//private float mDensity = 1.0f;
	
	/**
	 * Constructor physics for default polygon.
	 * 
	 * @param points polygon's points
	 * @param bodyMass polygon's mass
	 * @param position Polygon's start position
	 * @param listener Lister to be called when movement occurs
	 */
	public PhysicPolygon(final Point[] points, final float bodyMass, 
			final Point position, final OnMovementListener listener) {
		super(bodyMass, position);
		
		final int pointsCount = points.length;
		final Vector2D[] polygonVectors = new Vector2D[pointsCount];
		
		
		// Stroes points into Vector array.
		for (int i = 0; i < pointsCount; i++) 
			polygonVectors[i] = new Vector2D(points[i].x, points[i].y);
		
		// Sets polygon's properties
		mPolygonContour = polygonVectors;
		
		mListener = listener;
		mCollider = new ColliderPolygon(mPolygonContour, this);
		
		mListener.onMovement(mPosition, 0f);
		mCollider.setPosition(mPosition);

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
		return 0.4f * mass * radius * radius;
	}
	
	/**
	 * Check if point is contained by the polygon
	 * 
	 * @param p point to check
	 * @return true if is containded
	 */
	public boolean contains(Point p) {
		return PolygonUtils.polygonConatinsPoint(mPolygonContour, new Point(p.x - mPosition.i, p.y - mPosition.j));
	}
	
	/**
	 * Called when object has been updated
	 */
	public synchronized void onUpdateBody(float time) {
		// Sets velocity and position
		mVelocity.add((new Vector2D(mAppliedForce)).mul((time / 1000f) / mMass));
		mPosition.add((new Vector2D(mVelocity)).mul(time / 1000f));
		
		// Sets angular velocity and rotation
		mAngularVelocity += mAppliedMoment * (time / 1000f) / mAngularMass;
		float angleToRotate = mAngularVelocity * time / 1000f;
		Matrix22 rotationMatrix = Matrix22.rotationMatrix(angleToRotate);
		for (Vector2D vertex : mPolygonContour)
			vertex.mul(rotationMatrix);
		
		mAngle += angleToRotate;
		while (mAngle > 2 * Math.PI)
			mAngle -= 2 * Math.PI;
		while (mAngle < 0)
			mAngle += 2 * Math.PI;
		
		// Updates positions
		mCollider.setPosition(mPosition);
		
		mAppliedForce.set(0f, 0f);
		mAppliedMoment = 0f;
		
		mListener.onMovement(mPosition, mAngle);
	}

	/**
	 * Called when collision occurs
	 */
	@Override
	public void onCollide(Collision collide) {
		
		this.applyForce(collide.sense, collide.position);
		return;
		
	}
	
}
