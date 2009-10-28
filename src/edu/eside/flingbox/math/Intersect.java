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

/**
 * Handles functions to compute and storage intersections between 
 * two or more Vectors.
 *
 */
public class Intersect {
	
	// Stores point of the intersection
	public final Vector2D intersectionPoint;
	
	// Stores first point of segments that intersect
	public Vector2D intersectionASegment;
	public Vector2D intersectionBSegment;
	
	public boolean isIngoingIntersection;
	
	/**
	 * Local constructor. Use {@link intersectionOfSegments} or
	 * {@link intersectionOfTrace} to create an intersection.
	 * 
	 * @param collisionPoint
	 * @hide
	 */
	private Intersect(Vector2D collisionPoint) {
		this.intersectionPoint = collisionPoint;
	}
	
	/**
	 * computes intersection of two segments
	 * 
	 * @param segA0 First segment start point
	 * @param segA1 First segment end point
	 * @param segB0 Second segment start point
	 * @param segB1 Second segment end point
	 * @return intersection between segments or null if no intersection.
	 */
	public static Intersect intersectionOfSegments(Vector2D segA0, Vector2D segA1, 
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
		
		// We have an intersect
		Intersect intersect =  new Intersect(new Vector2D(
				a0x + uA * (a1x - a0x), a0y + uA * (a1y - a0y)));
		
		intersect.intersectionASegment = segA0;
		intersect.intersectionBSegment = segB0;
		
		return intersect;
	}
	/**
	 * Computes intersection between two traces.
	 * TODO: this migth crash if there are more intersections than segments. yes,
	 *  it can be possible if floats overflows.
	 *
	 * @param traceA first trace array
	 * @param a0 first point to check
	 * @param a1 last point to check NOTE: if b0 = b1 all segments will be checked
	 * @param traceB second trace array
	 * @param b0 first point to check
	 * @param b1 last point to check NOTE: if b0 = b1 all segments will be checked
	 * @return Array with Intersects. Some positions can be null
	 */
	public static Intersect[] intersectionsOfTrace(Vector2D[] traceA, int a0 , int a1, 
			Vector2D[] traceB, int b0, int b1) { 
		final int aLen = traceA.length, bLen = traceB.length;
		Intersect[] intersects = new Intersect[aLen > bLen ? aLen : bLen];
		int intersecitonsCount = 0;
		 // We are going to probe each segment.
		int i = a0, j;
		do {
			j = b0;
			do {
				Intersect intersect = intersectionOfSegments(
						traceA[i], traceA[(i + 1) % aLen], 
						traceB[j], traceB[(j + 1) % bLen]);
				if (intersect != null) {
					intersect.isIngoingIntersection = (intersecitonsCount % 2) == 0;
					intersects[intersecitonsCount] = intersect;
					intersecitonsCount++;
				}
				j = (j + 1) % bLen;
			} while (j != b1);
			
			i = (i + 1) % aLen;
		} while (i != a1);
		
		return intersects;
	}
	
	
	/**
	 * Computes intersection between two traces.
	 * 
	 * @param traceA First trace
	 * @param traceB Second trace
	 * @return Array with Intersects. Some positions can be null
	 */
	public static Intersect[] intersectionsOfTrace(Vector2D[] traceA, Vector2D[] traceB) {
		return intersectionsOfTrace(traceA, 0 , 0, traceB, 0, 0);
	}
	
	
}
