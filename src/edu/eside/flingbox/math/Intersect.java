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
	/** Just pointer to recognize polygon side */
	public final Vector2D[] polygonA;
	/** Just pointer to recognize polygon side */
	public final Vector2D[] polygonB;
	
	/** Stores array with intersect contour of first polygon */
	public final Vector2D[] contourA; 
	/** Stores array with intersect contour of second polygon */
	public final Vector2D[] contourB; 
	
	/** Ingoing point, it's also in contour[0] */
	public final Vector2D ingoingPoint;
	/** Outgoing point, also in contour*/
	public final Vector2D outgoingPoint;

	
	/**
	 * Local constructor for an intersection. Computes intersectionContour
	 * 
	 * @param polygonA first Polygon
	 * @param polygonB second polygon
	 * @param ingoing first intersecting point
	 * @param outgoing last intersection point
	 * @param pAIn index of the point after ingoing point
	 * @param pBIn index of the point after ingoing point
	 * @param pAOut index of the point after outgoing point
	 * @param pBOut index of the point after outgoing point
	 */
	private Intersect(Vector2D[] polygonA, Vector2D[] polygonB, 
			Vector2D ingoing, Vector2D outgoing,
			int pAIn, int pBIn, int pAOut, int pBOut) throws IllegalArgumentException {
		final int pointsCountA = polygonA.length;
		final int pointsCountB = polygonB.length;
		
		/* Calculate total points that will be stored */
		final int intContourALen = ((pAOut - pAIn + pointsCountA) % pointsCountA);	// For polygon A intersect
		final int intContourBLen = ((pBIn - pBOut + pointsCountB) % pointsCountB);	// For polygon B intersect
		if (intContourALen <= 0 && intContourBLen <= 0)
			throw new IllegalArgumentException("Intersection is a line. No contour passed.");
		
		/* Copy intersecting contour from A */
		Vector2D[] contourA = new Vector2D[intContourALen];
		for (int i = 0; i < intContourALen; i++)
			contourA[i] = polygonA[(pAIn + i) % pointsCountA];
		
		/* Copy intersecting contour from B, B order is reverse */
		Vector2D[] contourB = new Vector2D[intContourBLen];
		for (int i = 0; i < intContourBLen ; i++)
			contourB[i] = polygonB[(pBOut + i) % pointsCountB];
		
		this.polygonA = polygonA;
		this.polygonB = polygonB;
		this.ingoingPoint = ingoing;
		this.outgoingPoint = outgoing;
		this.contourA = contourA;
		this.contourB = contourB;
	}
	
	/**
	 * Computes all intersects between two polygons
	 * 
	 * @param polygonA first polygon
	 * @param polygonB second polygon
	 * @return an array with all intersect. if no intersects, an empty array returned
	 */
	public static Intersect[] intersectPolygons(Vector2D[] polygonA, Vector2D[] polygonB) {
		final ArrayList<Intersect> intersections = new ArrayList<Intersect>();
		
		final int pointsCountA = polygonA.length;
		final int pointsCountB = polygonB.length;
		
		/* We will need to storage line's intersections */
		Vector2D lastIngoingIntersect = null, lastOutgoingIntersect = null;
		int lastIngoingPointA = 0, lastIngoingPointB = 0;
		int lastOutgoingPointA = 0, lastOutgoingPointB = 0;
		
		Vector2D intersect = new Vector2D();
		for (int i = 0; i < pointsCountA; i++) 
			for (int j = 0; j < pointsCountB; j++) {
				/* Check each point */
				int intersectType = computeIntersectionOfSegments(polygonA[i], polygonA[(i + 1) % pointsCountA], 
						polygonB[j], polygonB[(j + 1) % pointsCountB], intersect);
				if (intersectType == 0) // No intersect 
					continue;
				
				if (intersectType > 0) { // Ingoing Intersect
					lastIngoingIntersect = new Vector2D(intersect);
					lastIngoingPointA = (i + 1) % pointsCountA;
					lastIngoingPointB = (j + 1) % pointsCountB;
				} else  // Outgoing Intersect
					if (lastIngoingIntersect != null) { // We have a complete intersecion
						intersections.add(
								new Intersect(polygonA, polygonB, lastIngoingIntersect, new Vector2D(intersect), 
										lastIngoingPointA, lastIngoingPointB, 
										(i + 1) % pointsCountA, (j + 1) % pointsCountB));
						lastIngoingIntersect = null; // wait for another intersection
					} else {
						lastOutgoingIntersect = new Vector2D(intersect);
						lastOutgoingPointA = (i + 1) % pointsCountA;
						lastOutgoingPointB = (j + 1) % pointsCountB;
					}
			}
		if (lastIngoingIntersect != null && lastOutgoingIntersect != null)
			intersections.add(
					new Intersect(polygonA, polygonB, 
							lastIngoingIntersect, lastOutgoingIntersect, 
							lastIngoingPointA, lastIngoingPointB, 
							lastOutgoingPointA, lastOutgoingPointB));
		
		return intersections.toArray(new Intersect[0]);
	}


	/**
	 * Computes intersect between two segments
	 * 
	 * @param segA0 first segment point
	 * @param segA1 first segment point
	 * @param segB0 second segment point
	 * @param segB1 second segment point
	 * @param intersectionPoint Vector to storage intersection point
	 * @return [-1, 0, 1]; 0 no intersection; 1 ingoing intersection; -1 outgoing Intersection 
	 */
	private static int computeIntersectionOfSegments(final Vector2D segA0, final Vector2D segA1, 
			final Vector2D segB0, final Vector2D segB1, 
			Vector2D intersectionPoint) {
		/* Get components to local variables. Just for performance */
		final float a0x = segA0.i, a0y = segA0.j, a1x = segA1.i, a1y = segA1.j, 
			b0x = segB0.i, b0y = segB0.j, b1x = segB1.i, b1y = segB1.j;
		
		final float d = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);
		
		if (d == 0.0f)
			return 0;	// Parallel lines
		
		final float uA = ((b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x)) / d;
		final float uB = ((a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x)) / d;

		if (uA < 0 || uA > 1 || uB < 0 || uB > 1) 
			return 0; 	// lines can't intersect

		intersectionPoint.set(a0x + uA * (a1x - a0x), a0y + uA * (a1y - a0y));
		
		/* Compute cross product */
		float crossAB = (a0x - b0x) * (b1y - b0y) - (b1x - b0x) * (a0y - b0y);
		
		if (crossAB > 0f)
			return -1; // is ingoing
		return +1; // is outgoing
	}
	
	/**
	 * Computes intersection penetration.
	 * 
	 * @return penetration distance
	 */
	public float getIntersectionDepth() {
		final Vector2D ingoing = this.ingoingPoint;
		final Vector2D outgoing = this.outgoingPoint;
		final Vector2D[] contourA = this.contourA;
		final Vector2D[] contourB = this.contourB;
		
		float penetrationByA = 0f;
		for (Vector2D p : contourA) {
			float pointsPenetration = PolygonUtils.distanceFromLineToPoint(ingoing, outgoing, p);
			if (pointsPenetration > penetrationByA)
				penetrationByA = pointsPenetration;
		}
		
		float penetrationByB = 0f;
		for (Vector2D p : contourB) {
			float pointsPenetration = PolygonUtils.distanceFromLineToPoint(ingoing, outgoing, p);
			if (pointsPenetration > penetrationByB)
				penetrationByB = pointsPenetration;
		}

		return penetrationByA + penetrationByB;
	}
	
}
