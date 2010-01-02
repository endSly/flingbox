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
	/** Handled in Physics, only pointer */
	private final Vector2D[] mPolygonContour;
	/** Located and rotated contour */
	private final Vector2D[] mLocatedContour;
	
	//private final float[] mVertexAngle;
	
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
		mLocatedContour = new Vector2D[pointsCount];
		
		// Stores all point's angles
		//mVertexAngle = new float[pointsCount];
		//for (int i = 0 ; i < pointsCount; i++) 
		//	mVertexAngle[i] = (float) Math.atan(contour[i].j / contour[i].i);

	}
	
	/**
	 * 
	 */
	public Contact[] checkContacts(final Collider collider) {
		if (!super.canContact(collider)) 
			return new Contact[0];
		
		final Vector2D[] polygon = updateLocatedPolygon();
		final Vector2D[] otherPolygon = ((ColliderPolygon) collider).updateLocatedPolygon();
		
		/* Find intersections */
		Intersect[] intersections = Intersect.intersectPolygons(polygon, otherPolygon);
		
		/* Compute detected intersections */
		Contact[] contacts = new Contact[intersections.length];
		for (int i = intersections.length - 1; i >= 0; i--) {
			Intersect intersect = intersections[i];
			Vector2D position = new Vector2D(intersect.outgoingPoint).add(intersect.ingoingPoint).mul(0.5f);
			Vector2D sense = new Vector2D(intersect.outgoingPoint).sub(intersect.ingoingPoint);
			
			contacts[i] = new Contact(this.mPhysicBody, collider.mPhysicBody, position, sense);
		}
		return contacts;
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
	private Vector2D[] updateLocatedPolygon() {
		final Vector2D[] polygon = mPolygonContour;
		final Vector2D[] locatedPolygon = mLocatedContour;
		final Vector2D position = mPosition;
		final float angle =  mAngle;
		
		final int pointsCount = polygon.length;
		
		final Matrix22 rotationMatrix = new Matrix22(angle);

		for (int i = pointsCount - 1; i >= 0; i--) 
			locatedPolygon[i] = Vector2D.mul(polygon[i], rotationMatrix).add(position);
		
		return locatedPolygon;
	}

}
