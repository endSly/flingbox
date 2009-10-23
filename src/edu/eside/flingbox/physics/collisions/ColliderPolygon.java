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
import edu.eside.flingbox.physics.PhysicBody;

/**
 * Collider for a polygon. it handles all functions needed by 
 * collition system
 * NOTE This class should only be created by {@link PhisicPolygon}.
 * 
 * How collision detector optimizer works:
 * - When created collider check each polygon's vertex angle and 
 * stores them.
 * - When bounding circle collides Collider checks only segments 
 * in other circle region.
 *
 */
public class ColliderPolygon extends Collider {
	
	//private float mRotationAngle;
	
	@SuppressWarnings("unused")
	private final Vector2D[] mPolygonNormals;
	
	/** Handled in Physics, only pointer */
	private final Vector2D[] mPolygonContour;
	
	private final float[] mVertexAngle;
	
	/**
	 * Default constructor for a polygon collider.
	 * 
	 * @param contour Polygon's points. It handles pointer in order to 
	 * preserve recurses, so mPolygonContour should be automaticaly rotated.
	 * NOTE It has to be in counterclockwise ordering!!!
	 * 
	 * @return
	 */
	public ColliderPolygon(final Vector2D[] contour, PhysicBody thisPhysic) {
		super(thisPhysic);
		final int pointsCount = contour.length;
		mPolygonContour = contour;
		mRadius = computeBoundingCircleRadius(contour);
		mPolygonNormals = computePolygonNormals(contour);
		
		// Stores all point's angles
		mVertexAngle = new float[pointsCount];
		for (int i = 0 ; i < pointsCount; i++) 
			mVertexAngle[i] = (float) Math.atan(contour[i].j / contour[i].i);

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
	 * Moves polygon to determinate point
	 * @return New translated polygon
	 */
	private static Vector2D[] translatePolygon(Vector2D[] polygon, Vector2D position) {
		final int pointsCount = polygon.length;
		final Vector2D[] locatedPolygon = new Vector2D[pointsCount];
		for (int i = 0; i < pointsCount; i++) 
			locatedPolygon[i] = new Vector2D(polygon[i]).add(position);
		return locatedPolygon;
	}
	
	/**
	 * TODO
	 */
	public boolean checkCollision(Collider collider) {
		if (!super.checkCollision(collider)) 
			return false;
		
		boolean doCollide = false;
		final Vector2D[] polygon = translatePolygon(mPolygonContour, mPosition);
		final Vector2D[] otherPolygon = translatePolygon(((ColliderPolygon) collider).mPolygonContour, ((ColliderPolygon) collider).mPosition);
		
		/*
		 * Find intersections
		 * TODO Optimize this!!
		 */
		Intersect[] intersections = Intersect.intersectionsOfTrace(polygon, otherPolygon);
		
		/*
		 * Compute detected intersections
		 */
		final int intersctionsCount = intersections.length;
		for (int i = 0
				; i < intersctionsCount - 1 && intersections[i + 1] != null
				; i += 2) {
			doCollide = true;
			
			final Vector2D ingoingIntersect = intersections[i].intersectionPoint, 
				outgoingIntersect = intersections[i + 1].intersectionPoint;
			
			Vector2D sense = new Vector2D(outgoingIntersect)
				.sub(ingoingIntersect)
				.normalVector()
				.mul(1E7f);
			
			Vector2D collisonPosition = new Vector2D(ingoingIntersect)
				.add(outgoingIntersect)
				.mul(0.5f);
			
			Vector2D bodysSide = (new Vector2D(mPosition)).sub(collisonPosition);
			boolean polygonSide = bodysSide.dotProduct(sense) >= 0;
			
			Collision collisionA = new Collision(
					new Vector2D(collisonPosition).sub(mPosition), 
					polygonSide ? sense : new Vector2D(sense).negate());
			collisionA.collidingBody = collider.mPhysicBody;
			
			Collision collisionB = new Collision(
					new Vector2D(collisonPosition).sub(((ColliderPolygon) collider).mPosition), 
					!polygonSide ? sense : new Vector2D(sense).negate());
			collisionB.collidingBody = mPhysicBody;
			
			collisionA.otherBodyCollisionPoint = collisionB.position;
			collisionB.otherBodyCollisionPoint = collisionA.position;
			
			mCollisionListener.onCollide(collisionA);
			collider.mCollisionListener.onCollide(collisionB);
		}
		return doCollide;
	}
	
}
