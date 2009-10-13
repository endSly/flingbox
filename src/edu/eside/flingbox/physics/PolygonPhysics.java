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

import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.Vector2D;

public abstract class PolygonPhysics extends Physics {

	// Some physical values needed
	private final float mBodyMass;
	private final Vector2D[] mPolygonVectors;
	private final Point mPosition;
	
	public PolygonPhysics(final Point[] points, final short[] triangulationIndexes) 
	throws IllegalArgumentException {
		super();

		float bodyMass = 0.0f;
		
		// Overal polygon mass center
		Point massCenter = new Point();
		final int trianglesCount = triangulationIndexes.length / 3;
		/* 
		 * We have to set Center of mass of the polygon to point (0, 0).
		 * For this, we need to compute center of mass and mass for each
		 * sub-triangle. 
		 */
		for (int i = 0; i < trianglesCount; i++) {
			final Point p0 = points[triangulationIndexes[i * 3]];
			final Point p1 = points[triangulationIndexes[i * 3 + 1]];
			final Point p2 = points[triangulationIndexes[i * 3 + 2]];
			
			final Point centerOfTriangle = triangleCenterOfMass(p0, p1, p2);
			final float massOfTriangle = trinagleArea(p0, p1, p2);
			
			massCenter.x = ((massCenter.x * bodyMass) + (centerOfTriangle.x * massOfTriangle))
				/ (bodyMass + massOfTriangle);
			massCenter.y = ((massCenter.y * bodyMass) + (centerOfTriangle.y * massOfTriangle))
				/ (bodyMass + massOfTriangle);
			bodyMass += massOfTriangle;
		}
		
		mBodyMass = bodyMass;
		
		final int pointsCount = points.length;
		Vector2D[] polygonVectors = new Vector2D[pointsCount];
		
		// Set mass center to point (0, 0) by moving all points
		for (int i = 0; i < pointsCount; i++) 
			polygonVectors[i] = new Vector2D(points[i].x - massCenter.x, points[i].y - massCenter.y);
		
		mPosition = massCenter;
		mPolygonVectors = polygonVectors;
		// Set position to old mass center
		//mPosition = massCenter;

	}
	
	public float getBodyMass() {
		return mBodyMass;
	}
	
	private Point triangleCenterOfMass(Point p0, Point p1, Point p2) {
		return new Point((p0.x + p1.x + p2.x) / 3f, (p0.y + p1.y + p2.y) / 3f);
	}
	
	private float trinagleArea(Point p0, Point p1, Point p2) {
		return (p0.x * p1.y + p1.x * p2.y + p2.x * p0.y 
				- p1.x * p0.y - p2.x * p1.y - p0.x * p2.y) / 2f;
	}
	

}
