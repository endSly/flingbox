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
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.collisions.Collider.Collision;

/**
 * Collider for a polygon. it handles all functions needed by 
 * collition system
 * NOTE This class should only be created by {@link PhisicPolygon}.
 *
 */
public class ColliderPolygon extends Collider {
	
	// needed to discartd quickly collisions
	private final float mRadius; 
	private final Box2D mBoundingBox;
	
	private final Vector2D[] mPolygonNormals;
	
	/** Handled in Physics, only pointer */
	private final Vector2D[] mPolygonContour;
	
	/**
	 * Default constructor for a polygon collider.
	 * 
	 * @param contour Polygon's points. It handles pointer in order to 
	 * preserve recurses, so mPolygonContour should be automaticaly rotated.
	 * NOTE It has to be in counterclockwise ordering!!!
	 * 
	 * @return
	 */
	public ColliderPolygon(final Vector2D[] contour) {
		mPolygonContour = contour;
		mRadius = computeBoundingCircleRadius(contour);
		mBoundingBox = computeBoundingBox(contour);
		mPolygonNormals = computePolygonNormals(contour);
	}
	
	/**
	 * Checks if the point is inside Polygon's bounding box.
	 * 
	 * @param p Point to check.
	 * @return true if is inside,else false.
	 */
	public boolean isOverallPointInside(Point p) {
		return mBoundingBox.isPointInside(p);
	}
	
	/**
	 * Checks if this collides with other collider.
	 * 
	 * @param otherCollider
	 * @return 
	 */
	public Collision collidesTo(Collider otherCollider) {
		return new Collision();
	}
	
	/**
	 * Computes Polygon normals.
	 * 
	 * @param contour Counterclockwise polygon points
	 * @return Polygon's normals
	 */
	private static Vector2D[] computePolygonNormals(final Vector2D[] contour) {
		final int pointsCount = contour.length;
		Vector2D[] normals = new Vector2D[pointsCount];
		
		for (int i = 0; i < pointsCount; i++) {
			final Vector2D p0 = contour[i], p1 = contour[i == pointsCount - 1 ? 0 : i ]; 
			normals[i] = new Vector2D((p1.j - p0.j), (p0.i - p1.i));//.normalize();
		}
		
		return normals;
	}
	
	/**
	 * Computes horizontal bounding box 
	 * 
	 * @param contour Polygons contour
	 * @return Bounding box
	 */
	private static Box2D computeBoundingBox(final Vector2D[] contour) {
		float leftBound = contour[0].i;
		float rightBound = leftBound;
		float topBound = contour[0].j;
		float bottomBound = topBound;
		
		for (int i = contour.length - 1; i >= 0; i--) {
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
		
		Box2D boundingBox = new Box2D();
		boundingBox.topLeft.set(leftBound, topBound);
		boundingBox.bottomRight.set(rightBound, bottomBound);
		return boundingBox;
	}
	
	/**
	 * Computes bounding circle with center in point (0, 0)
	 * 
	 * @param contour Polygon's contour
	 * @return Circles radius
	 */
	private static float computeBoundingCircleRadius(final Vector2D[] contour) {
		float radiusSquare = 0.0f;
		for (Vector2D v : contour) {
			float vRadSquare = (v.i * v.i) + (v.j * v.i);
			if (vRadSquare > radiusSquare)
				radiusSquare = vRadSquare;
		}
		return (float) Math.sqrt(radiusSquare);
	}
	
	
}
