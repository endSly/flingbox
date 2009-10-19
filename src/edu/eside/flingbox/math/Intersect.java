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

public class Intersect {
	public final Vector2D collisionPoint;
	
	public Vector2D polygonASegment;
	public Vector2D polygonBSegment;
	
	public boolean isIngoingIntersection;
	
	private Intersect(Vector2D collisionPoint) {
		this.collisionPoint = collisionPoint;
	}
	
	public static Intersect intersectionOfSegments(Vector2D segA0, Vector2D segA1, 
			Vector2D segB0, Vector2D segB1) {
		
		final float a0x = segA0.i, a0y = segA0.j, a1x = segA1.i, a1y = segA1.j, 
			b0x = segB0.i, b0y = segB0.j, b1x = segB1.i, b1y = segB1.j;
		
		final float d = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);
		
		if (d == 0.0f)
			return null;	// Parallel lines
		
		final float uA = ((b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x)) / d;
		final float uB = ((a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x)) / d;

		if (uA < 0 || uA > 1 || uB < 0 || uB > 1) 
			return null; 	// lines can't intersect
		
		Intersect intersect =  new Intersect(new Vector2D(
				a0x + uA * (a1x - a0x), a0y + uA * (a1y - a0y)));
		
		return intersect;
	}
	
	public static Intersect[] intersectionsOfTrace(Vector2D[] traceA, Vector2D[] traceB) {
		final int aLen = traceA.length, bLen = traceB.length;
		
		Intersect[] intersects = new Intersect[aLen > bLen ? aLen : bLen];
		int intersecitonsCount = 0;
		
		for (int i = 0; i < aLen; i++)
			for (int j = 0; j < bLen; j++) {
				Intersect intersect = intersectionOfSegments(
						traceA[i], traceA[(i + 1) % aLen], 
						traceB[j], traceB[(j + 1) % bLen]);
				if (intersect != null) {
					intersect.isIngoingIntersection = (intersecitonsCount % 2) == 0;
					intersects[intersecitonsCount] = intersect;
					intersecitonsCount++;
				}
			}
		return intersects;
	}
	
	
}
