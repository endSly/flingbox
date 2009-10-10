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

import java.nio.FloatBuffer;

public class PolygonUtils {
	
	/**
	 * The Douglas-Peucker algorithm is an algorithm for reducing the number 
	 * of points in a curve that is approximated by a series of points.
	 * At the end of this function is a good moment for call to GarbageCollector
	 * @param points	Array of polygon's points (x0, y0, x1, y1, x2...) 
	 * @param epsilon	Max distance to ignore a point
	 * @return			New array with optimized points
	 */
	public static float[] douglasPeuckerReducer(float[] points, float epsilon) {
		final int pointsCount = points.length / 2;
		if (pointsCount < 4 || epsilon <= 0.0f)
			return points;	// No reduction possible
		
		// I think that FloatBuffer will be faster than ArrayList<Float>
		FloatBuffer reducedPolygon = FloatBuffer.allocate(points.length);
		
		reducedPolygon.put(points, 0, 2);	// First point will not be include
		// Call recursively to algorithm
		douglasPeucker(points, epsilon, 0, pointsCount - 1, reducedPolygon);
		reducedPolygon.put(points, points.length - 2, 2); // Last point neither
		
		// Put buffer into float array
		float[] reducedPoints = new float[reducedPolygon.position()];
		reducedPolygon.position(0);
		reducedPolygon.get(reducedPoints);

		return reducedPoints;
	}
	
	/**
	 * Recursively Calculation of DouglasPeucker Algorithm
	 */
	private static void douglasPeucker(float[] points, final float epsilon, int first, int last, 
			FloatBuffer resultPoints) {
		float maxDistance = 0.0f;
		int maxDistanceIndex = 0;
		for (int i = first + 1; i < last ; i++) {
			float distance = distanceFromLineToPoint(
					points[first * 2], points[first * 2 + 1], 
					points[last * 2], points[last * 2 + 1],
					points[i * 2], points[i * 2 + 1]);
			if (distance > maxDistance) {
				maxDistance = distance;
				maxDistanceIndex = i;
			}
		}
		
		if (maxDistance > epsilon) {
			// Find in previous segment
			if ((maxDistanceIndex - first) > 1)
				douglasPeucker(points, epsilon, first, maxDistanceIndex, resultPoints);
			// Put point in buffer(2 coords)
			resultPoints.put(points, maxDistanceIndex * 2, 2);	
			// Continue searching important points
			if ((last - maxDistanceIndex) > 1)
				douglasPeucker(points, epsilon, maxDistanceIndex, last, resultPoints);
		}
	}
	
	
	/**
	 * Computes the triangulation of a polygon(tesellation) with ear-clipping 
	 * algorithm. 
	 * @param points	Array of polygon's points
	 * @return			Will return n-2 group of 3 points, for n sides polygon
	 * 					or null if not enough points
	 */
	public static short[] triangulatePolygon(float[] points) {
		final int pointsCount = points.length / 2;
		if (pointsCount < 3)
			return null;
		
		// n-2 group of 3 points, for n sides polygon 
		short[] triangules = new short[3 * (pointsCount - 2)];
		boolean[] included = new boolean [pointsCount];

		triangulatePolygon(points, triangules, included, pointsCount, 0);
		
		return triangules;
	}
	
	/**
	 * Recursively compute triangulation
	 */
	private static void triangulatePolygon(final float[] points, short[] indexes, 
			boolean[] included, final int pointsCount, int trianglesCount) {
		int topPointIndex = 0;
		float topPoint = -0.0f;
		boolean didFoundPoint = false;
		
		// Find top point to find triangle
		for (int i = 0; i < pointsCount; i++) {
			// initialize topPoint
			if (!didFoundPoint && !included[i]) {
				topPoint = points[i * 2];
				topPointIndex = i;
				didFoundPoint = true;
			}
			// Find top point
			if (!included[i] && (points[i * 2] > topPoint)) {
				topPoint = points[i * 2];
				topPointIndex = i;
			}
		}
		assert (didFoundPoint);
		
		// Exclude point for next iteration
		included[topPointIndex] = true;
		
		// Save triangle
		int prevPoint = topPointIndex; // Find previous point
		do {
			if (--prevPoint < 0)
				prevPoint = pointsCount - 1;
		} while (included[prevPoint]);
		
		int nextPoint = topPointIndex; // Find next point
		do {
			if (++nextPoint >= pointsCount)
				nextPoint = 0;
		} while (included[nextPoint]);
		
		indexes[trianglesCount * 3] = (short)prevPoint;	// Store into array
		indexes[trianglesCount * 3 + 1] = (short)topPointIndex;
		indexes[trianglesCount * 3 + 2] = (short)nextPoint;
		trianglesCount++;
		
		if (trianglesCount < (pointsCount - 2))
			triangulatePolygon(points, indexes, included, pointsCount, trianglesCount);
	}
	
	/**
	 * Computes minimum distance from line to point
	 */
	private static float distanceFromLineToPoint(
			float x0, float y0, float x1, float y1, 
			float xp, float yp) {
		float area = (x0 * y1 + x1 * yp + xp * y0 - x1 * y0 - xp * y1 - x0 * yp) / 2f;
		float base = (float)Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
		return (float)Math.abs(2f * area / base);
	}
	
}
