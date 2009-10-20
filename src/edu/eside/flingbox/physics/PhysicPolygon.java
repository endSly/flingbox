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
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.ColliderPolygon;
import edu.eside.flingbox.physics.collisions.Collision;
import edu.eside.flingbox.physics.collisions.Collider.OnCollideListener;

public class PhysicPolygon extends PhysicObject implements OnCollideListener {

	// Some physical values needed
	private final Vector2D[] mPolygonContour;
	
	public PhysicPolygon(final Point[] points, final float bodyMass, 
			final Point position, final OnMovementListener listener) {
		super(bodyMass, position);
		
		final int pointsCount = points.length;
		final Vector2D[] polygonVectors = new Vector2D[pointsCount];
		
		
		// Stroes points into Vector array.
		for (int i = 0; i < pointsCount; i++) 
			polygonVectors[i] = new Vector2D(points[i].x, points[i].y);
		
		mPolygonContour = polygonVectors;
		
		mListener = listener;
		mCollider = new ColliderPolygon(mPolygonContour, this);
		
		mListener.onMovement(mPosition, 0f);
		mCollider.setPosition(mPosition);
			// TODO
		mRotationalMoment = 0.33f * mBodyMass * mCollider.getBoundingCircle() 
		* mCollider.getBoundingCircle();

	}
	
	public synchronized void onUpdateBody(float time) {
		super.onUpdateBody(time);
		mAngularVelocity += mAppliedMoment * time / 1000f;
		float angleToRotate = mAngularVelocity * time / 1000f;
		Matrix22 rotationMatrix = Matrix22.rotationMatrix(angleToRotate);
		for (Vector2D vertex : mPolygonContour)
			vertex.mul(rotationMatrix);
		
		mRotation += angleToRotate;
		while (mRotation > 2 * Math.PI)
			mRotation -= mRotation;
		while (mRotation < - 2 * Math.PI)
			mRotation += mRotation;
		
		mAppliedMoment = 0f;
		
		mListener.onMovement(mPosition, mRotation);
	}

	@Override
	public void onCollide(Collision collide) {
		this.applyForce(collide.sense, collide.position);
		return;
		
	}
	
	



}
