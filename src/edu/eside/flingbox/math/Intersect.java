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

package edu.eside.flingbox.math;

import java.util.ArrayList;

/**
 * Handles functions to compute and storage intersections between 
 * two traces.
 *
 */
public class Intersect {
	
	// Stores point of the intersection
	/**
	 * @deprecated
	 */
	public Vector2D intersectionPoint;
	
	// Stores first point of segments that intersect
	/**
	 * @deprecated
	 */
	public Vector2D intersectionASegment;
	/**
	 * @deprecated
	 */
	public Vector2D intersectionBSegment;
	
	
	
	/**
	 * @deprecated
	 */
	public boolean isIngoingIntersection;
	
	public final Vector2D[] intersectionContour; 
	
	public Vector2D ingoingPoint;
	public Vector2D outgoingPoint;
	

	/**
	 * TODO!!!!
	 */
	private Intersect() {
		this.intersectionPoint = new Vector2D();
		this.intersectionContour = new Vector2D[2];
		ingoingPoint = new Vector2D();
		outgoingPoint = new Vector2D();
	}
	
	/**
	 * Local constructor. Use {@link intersectionOfSegments} or
	 * {@link intersectionOfTrace} to create an intersection.
	 * 
	 * @param collisionPoint
	 * @hide
	 */
	private Intersect(Vector2D collisionPoint) {
		this.intersectionPoint = collisionPoint;
		this.intersectionContour = new Vector2D[2];
		intersectionContour[0] = new Vector2D();
		intersectionContour[1] = new Vector2D();
	}
	
	private Intersect(Vector2D[] polygonA, Vector2D[] polygonB, Vector2D ingoing, Vector2D outgoing,
			int pAIn, int pBIn, int pAOut, int pBOut) {
		final int totalPointsCount = 2 
			+ ((pAOut - pAIn + polygonA.length) % polygonA.length) + 1
			+ ((pBOut - pBIn + polygonB.length) % polygonB.length) + 1;
		
		Vector2D[] contour = new Vector2D[totalPointsCount];
		
		contour[0] = ingoing;
		int contourIndex = 1;
		for (int i = pAIn; i != pAOut + 1; i = (i + 1) % polygonA.length)
			contour[contourIndex++] = polygonA[i];
		
		contour[contourIndex++] = outgoing;
		for (int i = pBIn; i != pBOut + 1; i = (i + 1) % polygonB.length)
			contour[contourIndex++] = polygonB[i];
		
		this.intersectionContour = contour;
		this.ingoingPoint = ingoing;
		this.outgoingPoint = outgoing;
	}
	
	public static Intersect[] intersectPolygons(Vector2D[] polygonA, Vector2D[] polygonB) {
		ArrayList<Intersect> intersections = new ArrayList<Intersect>();
		
		int pointsCountA = polygonA.length;
		int pointsCountB = polygonB.length;
		
		Vector2D ingoingIntersect = null;
		int ingoingPointA = 0;
		int ingoingPointB = 0;
		
		for (int i = 0; i < pointsCountA; i++) 
			for (int j = 0; j < pointsCountB; j++) {
				Vector2D intersect = computeIntersectionOfSegments(polygonA[i], polygonA[(i + 1) % pointsCountA], 
						polygonB[j], polygonB[(j + 1) % pointsCountB]);
				if (intersect == null)
					continue;
				if (ingoingIntersect == null) {
					/* We have an in-going intersection */
					ingoingIntersect = intersect;
					ingoingPointA = (i + 1) % pointsCountA;
					ingoingPointB = (j + 1) % pointsCountB;
				} else {
					/* We have an outgoing Intersection */
					intersections.add(new Intersect(polygonA, polygonB, 
							ingoingIntersect, intersect, ingoingPointA, ingoingPointB, 
							(i + 1) % pointsCountA, (j + 1) % pointsCountB));
					ingoingIntersect = null;
				}
			}

		return intersections.toArray(new Intersect[0]);
	}
	
	private static Vector2D computeIntersectionOfSegments(Vector2D segA0, Vector2D segA1, 
			Vector2D segB0, Vector2D segB1) {
		// Get components to local variables. Just for performance
		final float a0x = segA0.i, a0y = segA0.j, a1x = segA1.i, a1y = segA1.j, 
			b0x = segB0.i, b0y = segB0.j, b1x = segB1.i, b1y = segB1.j;
		
		final float d = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);
		
		if (d == 0.0f)
			return null;	// Parallel lines
		
		final float uA = ((b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x)) / d;
		final float uB = ((a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x)) / d;

		if (uA < 0 || uA > 1 || uB < 0 || uB > 1) 
			return null; 	// lines can't intersect
		
		return new Vector2D(a0x + uA * (a1x - a0x), a0y + uA * (a1y - a0y));
	}
	
	
}
