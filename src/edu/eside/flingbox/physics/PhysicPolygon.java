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

import edu.eside.flingbox.math.Box2D;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.ColliderPolygon;

public class PhysicPolygon extends PhysicObject {

	// Some physical values needed
	private final Vector2D[] mPolygonContour;
	
	private OnMovementListener mListener;
	
	private ColliderPolygon mCollider;
	
	private final Box2D mBoundingBox;
	
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
		mListener.onMovement(mPosition, 0f);
		
		mBoundingBox = new Box2D();
		computeBoundingBox();
		
		mCollider = new ColliderPolygon(mPolygonContour);

	}
	
	private void computeBoundingBox() {
		final Vector2D[] contour = mPolygonContour;
		float leftBound = contour[0].i;
		float rightBound = leftBound;
		float topBound = contour[0].j;
		float bottomBound = topBound;
		
		for (int i = contour.length; i > 0; i--) {
			final float x = contour[i].i;
			final float y = contour[i].j;
			if (x < leftBound)
				leftBound = x;
			else if (x > rightBound)
				rightBound = x;
			if (y < bottomBound)
				bottomBound = y;
			else if (y > topBound)
				topBound = y;
		}
		
		mBoundingBox.topLeft.set(leftBound, topBound);
		mBoundingBox.bottomRight.set(rightBound, bottomBound);
		
		
	}

}
