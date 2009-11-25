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
 * Implements some utilities for polygon.
 */
public final class PolygonUtils {
	/**
	 * Prevent instance creation
	 */
	private PolygonUtils() { }
	
	/**
	 * The Douglas-Peucker algorithm is an algorithm for reducing the number 
	 * of points in a curve that is approximated by a series of points.
	 * The end of this function is a good moment to call the GarbageCollector
	 * 
	 * @param points	Array of the polygon's points 
	 * @param epsilon	Max distance to ignore a point
	 * @return			New array with optimized points
	 */
	public static Vector2D[] douglasPeuckerReducer(final Vector2D[] points, final float epsilon) {
		final int lastPoint = points.length -1;
		if (lastPoint < 3 || epsilon <= 0.0f)
			return points;	// No reduction possible
		
		final ArrayList<Vector2D> reducedPolygon = new ArrayList<Vector2D>(lastPoint + 1);
		
		reducedPolygon.add(points[0]);
		douglasPeucker(points, epsilon, 0, lastPoint, reducedPolygon);
		if (points[0].distanceToPoint(points[lastPoint]) > epsilon)
			reducedPolygon.add(points[lastPoint]);

		return (Vector2D[]) reducedPolygon.toArray(new Vector2D[0]);
	}
	
	/**
	 * Recursively Calculation of DouglasPeucker Algorithm
	 */
	private static void douglasPeucker(final Vector2D[] points, final float epsilon, 
			final int first, final int last, final ArrayList<Vector2D> resultPoints) {
		
		float maxDistance = 0.0f;
		int maxDistanceIndex = 0;
		
		// Find maximum distance point.  
		for (int i = first + 1; i < last ; i++) {
			float distance = distanceFromLineToPoint(points[first], points[last], points[i]);
			if (distance > maxDistance) {	// Store point
				maxDistance = distance;
				maxDistanceIndex = i;
			}
		}
		
		/* 
		 * If point distance is more than epsilon then split points array in 
		 * two parts and iterate for each. 
		 */
		if (maxDistance > epsilon) {
			// Find in previous segment
			if ((maxDistanceIndex - first) > 1)
				douglasPeucker(points, epsilon, first, maxDistanceIndex, resultPoints);
			// Put point in buffer(2 coords)
			resultPoints.add(points[maxDistanceIndex]);	
			// Continue searching important points
			if ((last - maxDistanceIndex) > 1)
				douglasPeucker(points, epsilon, maxDistanceIndex, last, resultPoints);
		}
	}
	
	
	/**
	 * Computes the triangulation of a polygon(tesellation) with ear-clipping 
	 * algorithm. 
	 * @param Vector2Ds	Array of polygon's points
	 * @return			Will return n-2 group of 3 points, for n sides polygon
	 * 					or null if not enough points
	 */
	public static short[] triangulatePolygon(final Vector2D[] Vector2Ds) {
		final int Vector2DsCount = Vector2Ds.length;
		if (Vector2DsCount < 3)
			return null;
		
		// n-2 group of 3 Vector2Ds, for n sides polygon 
		short[] triangles = new short[3 * (Vector2DsCount - 2)];
		boolean[] included = new boolean[Vector2DsCount];

		triangulatePolygon(Vector2Ds, triangles, included, Vector2DsCount, 0);
		
		return triangles;
	}
	
	/**
	 * Recursively compute triangulation
	 */
	private static void triangulatePolygon(final Vector2D[] Vector2Ds, short[] indexes, 
			boolean[] included, final int Vector2DsCount, final int trianglesCount) {
		int topVector2DIndex = 0;
		float topVector2D = Float.NEGATIVE_INFINITY;
		
		// Find top Vector2D to find triangle
		for (int i = 0; i < Vector2DsCount; i++) {
			// Find top Vector2D
			if (!included[i] && (Vector2Ds[i].i > topVector2D)) {
				topVector2D = Vector2Ds[i].i;
				topVector2DIndex = i;
			}
		}
		
		// Exclude Vector2D for next iteration
		included[topVector2DIndex] = true;
		
		// Save triangle
		int prevVector2D = topVector2DIndex; // Find previous Vector2D
		do {
			if (--prevVector2D < 0)
				prevVector2D = Vector2DsCount - 1;
		} while (included[prevVector2D]);
		
		int nextVector2D = topVector2DIndex; // Find next Vector2D
		do {
			if (++nextVector2D >= Vector2DsCount)
				nextVector2D = 0;
		} while (included[nextVector2D]);
		
		// Store triangle
		indexes[trianglesCount * 3] = (short)prevVector2D;	// Store into array
		indexes[trianglesCount * 3 + 1] = (short)topVector2DIndex;
		indexes[trianglesCount * 3 + 2] = (short)nextVector2D;

		// If there are more triangles iterate one more time
		if (trianglesCount < (Vector2DsCount - 3))
			triangulatePolygon(Vector2Ds, indexes, included, Vector2DsCount, trianglesCount + 1);
	}
	
	/**
	 * Checks if a Vector2D is contained by a polygon.
	 * It's based on Winding number algorithm.
	 * More info at {@link http://en.wikipedia.org/wiki/Winding_number}
	 * 
	 * @param polygon polygon's Vector2Ds
	 * @param Vector2D Vector2D to be checked
	 */
	public static boolean polygonConatinsPoint(Vector2D[] polygon, Vector2D Vector2D) {
		final int Vector2DsCount = polygon.length;
		final float px = Vector2D.i, py = Vector2D.j;
		int c = 0;
		for (int i = 0; i < Vector2DsCount; i++) {
			Vector2D v1 = polygon[i];
			Vector2D v2 = polygon[(i + 1) % Vector2DsCount] ;
			if ((v1.j < py) && (v2.j > py)) {
				if (v1.i > px || v2.i > px)
					c++;
				// Check if Vector2D is at the left or the right side of the object
				//final float segmentAtX = ((v2.i - v1.i) / (v2.j - v1.j)) * (py - v1.j) + v1.i;
				//if (segmentAtX > px) 
				//	c++;
				//else if (segmentAtX == px)
				//	return true; // is over the bounder
			} else if ((v1.j > py) && (v2.j < py)) {
				if (v1.i > px || v2.i > px)
					c--;
				// Check if Vector2D is at the left or the right side of the object
				//final float segmentAtX = ((v2.i - v1.i) / (v2.j - v1.j)) * (py - v1.j) + v1.i;
				//if (segmentAtX > px) 
				//	c--;
				//else if (segmentAtX == px)
				//	return true; // is over the bounder
			}
		}
		return c != 0;
	}
	
	/**
	 * Computes the area of the polygon.
	 * @param Vector2Ds Polygon's Vector2Ds
	 * @return Polygon's area. if Vector2Ds are counter-clockwise the 
	 * result will be positive, else it'll be negative
	 */
	public static float polygonArea(final Vector2D[] Vector2Ds) {
		final int lastVector2D = Vector2Ds.length - 1;
		
		float area = Vector2Ds[lastVector2D].i * Vector2Ds[0].j 
			- Vector2Ds[0].i * Vector2Ds[lastVector2D].j;
		
		Vector2D Vector2D, nextVector2D;
		for (int i = 0; i < lastVector2D; i++) {
			Vector2D = Vector2Ds[i];
			nextVector2D = Vector2Ds[i + 1];
			area += Vector2D.i * nextVector2D.j - nextVector2D.i * Vector2D.j;
		}
		return area / 2f;
	}
	
	/**
	 * Computes the polygon's centroid
	 * 
	 * @param Vector2Ds	polygon
	 * @return centroid Vector2D
	 */
	public static Vector2D polygonCentroid(final Vector2D[] Vector2Ds) {
		final int Vector2DsCount = Vector2Ds.length;
		float cx = 0f, cy = 0f;
		/*
		float cnx = 0f, cny = 0f;
		for (Vector2D p : Vector2Ds) {
			cnx += p.x;
			cny += p.y;
		}
		cnx /= Vector2DsCount;
		cny /= Vector2DsCount;
		*/
		
		float p0x, p1x, p0y, p1y, k;
		for (int i = 0; i < Vector2DsCount; i++) {
			p0x = Vector2Ds[i].i;
			p1x = Vector2Ds[(i + 1) % Vector2DsCount].i;
			p0y = Vector2Ds[i].j;
			p1y = Vector2Ds[(i + 1) % Vector2DsCount].j;
			k = (p0x * p1y - p1x * p0y);
			cx += (p0x + p1x) * k;
			cy += (p0y + p1y) * k;
		}
		final float d = 6f * polygonArea(Vector2Ds);
		cx /= d;
		cy /= d;

		return new Vector2D(cx, cy);
	}
	
	
	/**
	 * Computes the polygon's normals.  
	 * 
	 * @param contour Counterclockwise polygon points
	 * @return Polygon's normals
	 */
	public static Vector2D[] computePolygonNormals(final Vector2D[] contour) {
		final int Vector2DsCount = contour.length;
		Vector2D[] normals = new Vector2D[Vector2DsCount];
		
		Vector2D p0, p1;
		for (int i = 0; i < Vector2DsCount; i++) {
			p0 = contour[i];
			p1 = contour[i == Vector2DsCount - 1 ? 0 : i ]; 
			normals[i] = new Vector2D((p1.j - p0.j), (p0.i - p1.i));//.normalize();
		}
		
		return normals;
	}
	
	/**
	 * Computes minimum distance from line to point
	 */
	public static float distanceFromLineToPoint(final Vector2D p0, final Vector2D p1, final Vector2D p) {
		final float area = (p0.i * p1.j + p1.i * p.j + p.i * p0.j 
					- p1.i * p0.j - p.i * p1.j - p0.i * p.j) / 2f;
		final float base = (float) Math.sqrt((p1.i - p0.i) * (p1.i - p0.i) 
					+ (p1.j - p0.j) * (p1.j - p0.j));
		return (float) Math.abs(2f * area / base);
	}
	
}
