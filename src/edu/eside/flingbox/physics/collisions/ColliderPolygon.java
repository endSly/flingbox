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
import edu.eside.flingbox.math.Matrix22;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.physics.PhysicBody.OnMovementListener;

/**
 * Collider for a polygon. it handles all functions needed by 
 * Collision system
 * NOTE This class should only be created by {@link PhisicPolygon}.
 * 
 * How collision detector optimizer works:
 * - When created collider check each polygon's vertex angle and 
 * stores them.
 * - When bounding circle collides Collider checks only segments 
 * in other circle region.
 *
 */
public class ColliderPolygon extends Collider implements OnMovementListener {
	
	//private float mRotationAngle;
	
	/** Handled in Physics, only pointer */
	private final Vector2D[] mPolygonContour;
	
	private final float[] mVertexAngle;
	
	/**
	 * Default constructor for a polygon collider.
	 * 
	 * @param contour Polygon's points. It handles pointer in order to 
	 * preserve recurses, so mPolygonContour should be automatically rotated.
	 * NOTE It has to be in counterclockwise ordering!!!
	 * 
	 * @return
	 */
	public ColliderPolygon(final Vector2D[] contour, PhysicBody thisPhysic) {
		super(thisPhysic);
		final int pointsCount = contour.length;
		mPolygonContour = contour;
		mRadius = computeBoundingCircleRadius(contour);
		
		// Stores all point's angles
		mVertexAngle = new float[pointsCount];
		for (int i = 0 ; i < pointsCount; i++) 
			mVertexAngle[i] = (float) Math.atan(contour[i].j / contour[i].i);

	}

	/**
	 * Computes bounding circle with center in point (0, 0)
	 * 
	 * @param contour Polygon's contour
	 * @return Circles radius
	 */
	private static float computeBoundingCircleRadius(final Vector2D[] contour) {
		float radiusSquare = 0.0f;
		for (int i = contour.length - 1; i >= 0; i--) {	// Do not use fast enumaration since performance issues
			final Vector2D v = contour[i];
			float vRadSquare = (v.i * v.i) + (v.j * v.j);
			if (vRadSquare > radiusSquare)
				radiusSquare = vRadSquare;
		}
		return (float) Math.sqrt(radiusSquare);
	}
	
	/**
	 * Moves polygon to determinate point and rotates it
	 * @return New translated polygon
	 */
	private static Vector2D[] translateAndRotatePolygon(Vector2D[] polygon, Vector2D position, float angle) {
		final int pointsCount = polygon.length;
		final Vector2D[] locatedPolygon = new Vector2D[pointsCount];
		
		final Matrix22 rotationMatrix = Matrix22.rotationMatrix(angle);
		
		for (int i = pointsCount - 1; i >= 0; i--) 
			locatedPolygon[i] = Vector2D.mul(polygon[i], rotationMatrix).add(position);
		
		return locatedPolygon;
	}
	
	/**
	 * TODO
	 */
	public boolean checkCollision(Collider collider) {
		if (!super.checkCollision(collider)) 
			return false;
		
		final Vector2D[] polygon = translateAndRotatePolygon(mPolygonContour, mPosition, mAngle);
		final Vector2D[] otherPolygon = translateAndRotatePolygon(((ColliderPolygon) collider).mPolygonContour, 
				((ColliderPolygon) collider).mPosition, ((ColliderPolygon) collider).mAngle);
		
		/*
		 * Find intersections
		 * TODO Optimize this!!
		 */
		Intersect[] intersections = Intersect.intersectPolygons(polygon, otherPolygon);
		
		/* Compute detected intersections */
		for (Intersect intersect : intersections) {
			Contact contact = new Contact(this.mPhysicBody, collider.mPhysicBody, intersect);
			ContactSolver.solveContact(contact);
		}
		return intersections.length > 0;
	}
	
}
