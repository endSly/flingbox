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
	/** Stores array with intersect contour of first polygon */
	public final Vector2D[] contourA; 
	/** Stores array with intersect contour of second polygon */
	public final Vector2D[] contourB; 
	
	/** Ingoing point, it's also in contour[0] */
	public Vector2D ingoingPoint;
	/** Outgoing point, also in contour*/
	public Vector2D outgoingPoint;
	
	/**
	 * Local constructor for an intersection. Computes intersectionContour
	 * 
	 * @param polygonA first Polygon
	 * @param polygonB second polygon
	 * @param ingoing first intersecting point
	 * @param outgoing last intersectiong point
	 * @param pAIn index of the point after ingoing point
	 * @param pBIn index of the point after ingoing point
	 * @param pAOut index of the point after outgoing point
	 * @param pBOut index of the point after outgoing point
	 */
	private Intersect(Vector2D[] polygonA, Vector2D[] polygonB, 
			Vector2D ingoing, Vector2D outgoing,
			int pAIn, int pBIn, int pAOut, int pBOut) {
		final int pointsCountA = polygonA.length;
		final int pointsCountB = polygonB.length;
		
		/* Calculate total points that will be stored */
		final int intContourALen = ((pAOut - pAIn + pointsCountA) % pointsCountA) + 1;	/* For polygon A intersect */
		final int intContourBLen = ((pBOut - pBIn + pointsCountB) % pointsCountB) + 1;	/* For polygon B intersect */
		
		/* Copy intersecting contour from A */
		Vector2D[] contourA = new Vector2D[intContourALen];
		for (int i = 0; i < intContourALen; i++)
			contourA[i] = polygonA[(pAIn + i) % pointsCountA];
		
		/* Copy intersecting contour from B */
		Vector2D[] contourB = new Vector2D[intContourBLen];
		for (int i = 0; i < intContourBLen; i++)
			contourB[i] = polygonB[(pBIn + i) % pointsCountB];
		
		this.contourA = contourA;
		this.contourB = contourB;
		this.ingoingPoint = ingoing;
		this.outgoingPoint = outgoing;
	}
	
	/**
	 * Computes all intersects between two polygons
	 * 
	 * @param polygonA first polygon
	 * @param polygonB second polygon
	 * @return an array with all intersect. if no intersects, an empty arra returned
	 */
	public static Intersect[] intersectPolygons(Vector2D[] polygonA, Vector2D[] polygonB) {
		final ArrayList<Intersect> intersections = new ArrayList<Intersect>();
		
		final int pointsCountA = polygonA.length;
		final int pointsCountB = polygonB.length;
		
		/* We are waiting for an ingoing intersection */
		Vector2D ingoingIntersect = null;
		int ingoingPointA = 0;
		int ingoingPointB = 0;
		
		for (int i = 0; i < pointsCountA; i++) 
			for (int j = 0; j < pointsCountB; j++) {
				/* Check each point */
				Vector2D intersect = computeIntersectionOfSegments(polygonA[i], polygonA[(i + 1) % pointsCountA], 
						polygonB[j], polygonB[(j + 1) % pointsCountB]);
				if (intersect == null) // No intersect 
					continue;
				
				if (ingoingIntersect == null) { 	// We have an in-going intersection 
					ingoingIntersect = intersect;
					ingoingPointA = (i + 1) % pointsCountA;
					ingoingPointB = (j + 1) % pointsCountB;
				} else { 	// We have an outgoing Intersection 
					intersections.add(new Intersect(polygonA, polygonB, 
							ingoingIntersect, intersect, ingoingPointA, ingoingPointB, 
							(i + 1) % pointsCountA, (j + 1) % pointsCountB));
					ingoingIntersect = null; // wait for another intersection
				}
			}

		return intersections.toArray(new Intersect[0]);
	}
	
	/**
	 * Computes intersect between two segments
	 * 
	 * @param segA0 first segment point
	 * @param segA1 first segment point
	 * @param segB0 second segment point
	 * @param segB1 second segment point
	 * @return
	 */
	private static Vector2D computeIntersectionOfSegments(Vector2D segA0, Vector2D segA1, 
			Vector2D segB0, Vector2D segB1) {
		/* Get components to local variables. Just for performance */
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
