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

import edu.eside.flingbox.math.Intersect;
import edu.eside.flingbox.math.Vector2D;

/**
 * Collider for a polygon. it handles all functions needed by 
 * collition system
 * NOTE This class should only be created by {@link PhisicPolygon}.
 *
 */
public class ColliderPolygon extends Collider {
	
	//private float mRotationAngle;
	
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
	public ColliderPolygon(final Vector2D[] contour, OnCollideListener listener) {
		super(listener);
		mPolygonContour = contour;
		mRadius = computeBoundingCircleRadius(contour);
		mPolygonNormals = computePolygonNormals(contour);
	}
	
	/**
	 * Computes Polygon normals. 
	 * Needed to descart polygon's segments quickly.
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
	
	/**
	 * TODO
	 */
	public boolean checkCollision(Collider collider) {
		if (super.checkCollision(collider)) {
			//final Vector2D[] normals = mPolygonNormals;
			
			// We are going to rotate normals
			//Matrix22 rotationMatrix = Matrix22.rotationMatrix(mRotationAngle);
			
			//Vector2D collisionVector = new Vector2D(collider.mPosition.x - mPosition.x, 
			//		collider.mPosition.y - mPosition.y);
			
			// Translate this polygon
			final Vector2D[] polygonContour = mPolygonContour;
			final int pointsCount = polygonContour.length;
			final Vector2D[] locatedPolygon = new Vector2D[pointsCount];
			final Vector2D position = mPosition;
			for (int i = 0; i < pointsCount; i++) 
				locatedPolygon[i] = new Vector2D(polygonContour[i]).add(position);
			
			// Translate other polygon
			final Vector2D[] otherPolygonContour = ((ColliderPolygon) collider).mPolygonContour;
			final int otherPointsCount = otherPolygonContour.length;
			final Vector2D[] otherLocatedPolygon = new Vector2D[otherPointsCount];
			final Vector2D otherPosition = new Vector2D(
					((ColliderPolygon) collider).mPosition);
			
			for (int i = 0; i < otherPointsCount; i++)
				otherLocatedPolygon[i] = new Vector2D(otherPolygonContour[i])
					.add(otherPosition);

			
			Intersect[] intersections = Intersect.intersectionsOfTrace(
					locatedPolygon, otherLocatedPolygon);
			
			final int intersctionsCount = intersections.length;
			for (int i = 0
					; i < intersctionsCount - 1 && intersections[i + 1] != null
					; i += 2) {
				final Vector2D ingoingIntersect = intersections[i].intersectionPoint, 
					outgoingIntersect = intersections[i + 1].intersectionPoint;
				Vector2D sense = new Vector2D(outgoingIntersect);
				sense.sub(ingoingIntersect);
				sense = sense.normalVector().mul(1E6f);
				
				Vector2D collisonPosition = new Vector2D(ingoingIntersect)
					.add(outgoingIntersect)
					.mul(0.5f);
				
				Vector2D bodysSide = (new Vector2D(position)).sub(collisonPosition);
				boolean polygonSide = bodysSide.dotProduct(sense) >= 0;
				
				Collision collisionA = new Collision(
						new Vector2D(collisonPosition).sub(position), 
						polygonSide ? sense : new Vector2D(sense).negate());
				
				Collision collisionB = new Collision(
						new Vector2D(collisonPosition).sub(otherPosition), 
						!polygonSide ? sense : new Vector2D(sense).negate());
				
				mCollisionListener.onCollide(collisionA);
				collider.mCollisionListener.onCollide(collisionB);
			}
			
		}
		return false;
	}
	
}
