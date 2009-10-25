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
	 * At the end of this function is a good moment for call to GarbageCollector
	 * 
	 * @param points	Array of polygon's points 
	 * @param epsilon	Max distance to ignore a point
	 * @return			New array with optimized points
	 */
	public static Point[] douglasPeuckerReducer(final Point[] points, final float epsilon) {
		final int pointsCount = points.length;
		if (pointsCount < 4 || epsilon <= 0.0f)
			return points;	// No reduction possible
		
		final ArrayList<Point> reducedPolygon = new ArrayList<Point>(pointsCount);
		
		reducedPolygon.add(points[0]);	// First point will not be include
		// Call recursively to algorithm
		douglasPeucker(points, epsilon, 0, pointsCount - 1, reducedPolygon);
		if (points[0].distanceToPoint(points[pointsCount - 1]) > epsilon)
			reducedPolygon.add(points[pointsCount - 1]); // Last point neither

		return (Point[]) reducedPolygon.toArray(new Point[0]);
	}
	
	/**
	 * Recursively Calculation of DouglasPeucker Algorithm
	 */
	private static void douglasPeucker(final Point[] points, final float epsilon, 
			final int first, final int last, final ArrayList<Point> resultPoints) {
		
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
	 * @param points	Array of polygon's points
	 * @return			Will return n-2 group of 3 points, for n sides polygon
	 * 					or null if not enough points
	 */
	public static short[] triangulatePolygon(final Point[] points) {
		final int pointsCount = points.length;
		if (pointsCount < 3)
			return null;
		
		// n-2 group of 3 points, for n sides polygon 
		short[] triangules = new short[3 * (pointsCount - 2)];
		boolean[] included = new boolean [pointsCount];

		// Call to recursive function witch will calculate triangulation
		triangulatePolygon(points, triangules, included, pointsCount, 0);
		
		return triangules;
	}
	
	/**
	 * Recursively compute triangulation
	 */
	private static void triangulatePolygon(final Point[] points, short[] indexes, 
			boolean[] included, final int pointsCount, final int trianglesCount) {
		int topPointIndex = 0;
		float topPoint = Float.NEGATIVE_INFINITY;
		
		// Find top point to find triangle
		for (int i = 0; i < pointsCount; i++) {
			// Find top point
			if (!included[i] && (points[i].x > topPoint)) {
				topPoint = points[i].x;
				topPointIndex = i;
			}
		}
		
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
		
		// Store triangle
		indexes[trianglesCount * 3] = (short)prevPoint;	// Store into array
		indexes[trianglesCount * 3 + 1] = (short)topPointIndex;
		indexes[trianglesCount * 3 + 2] = (short)nextPoint;

		// If there are more triangles iterate one more time
		if (trianglesCount < (pointsCount - 3))
			triangulatePolygon(points, indexes, included, pointsCount, trianglesCount + 1);
	}
	
	/**
	 * Checks if a Point is contained by a polygon. It is bassed on 
	 * Winding number algorithm.
	 * More info at {@link http://en.wikipedia.org/wiki/Winding_number}
	 * 
	 * @param polygon polygon's points
	 * @param point point to be checked
	 */
	public static boolean polygonConatinsPoint(Vector2D[] polygon, Point point) {
		final int pointsCount = polygon.length;
		final float px = point.x, py = point.y;
		int c = 0;
		for (int i = 0; i < pointsCount; i++) {
			Vector2D v1 = polygon[i];
			Vector2D v2 = polygon[(i + 1) % pointsCount] ;
			if ((v1.j < py) && (v2.j > py)) {  
				if (v1.i > px || v2.i > px)
					c++;
				// Check if point is at left or at rigth of the object
				//final float segmentAtX = ((v2.i - v1.i) / (v2.j - v1.j)) * (py - v1.j) + v1.i;
				//if (segmentAtX > px) 
				//	c++;
				//else if (segmentAtX == px)
				//	return true; // is over the bounder
			} else if ((v1.j > py) && (v2.j < py)) {
				if (v1.i > px || v2.i > px)
					c--;
				// Check if point is at left or at rigth of the object
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
	 * Computes area of polygon.
	 * @param points Polygon's points
	 * @return Polygon's area. if points are counter-clockwise the 
	 * result will be positive, else it'll be negative
	 */
	public static float polygonArea(final Point[] points) {
		final int lastPoint = points.length - 1;
		
		float area = points[lastPoint].x * points[0].y 
			- points[0].x * points[lastPoint].y;
		
		for (int i = 0; i < lastPoint; i++) {
			Point point = points[i];
			Point nextPoint = points[i + 1];
			area += point.x * nextPoint.y - nextPoint.x * point.y;
		}
		return area / 2f;
	}
	
	/**
	 * Computes polygon centroid
	 * 
	 * @param points Polygon's points
	 * @return centroid point
	 */
	public static Point polygonCentroid(final Point[] points) {
		final int pointsCount = points.length;
		float cx = 0f, cy = 0f;
		/*
		float cnx = 0f, cny = 0f;
		for (Point p : points) {
			cnx += p.x;
			cny += p.y;
		}
		cnx /= pointsCount;
		cny /= pointsCount;
		*/
		
		for (int i = 0; i < pointsCount; i++) {
			float p0x = points[i].x, p1x = points[(i + 1) % pointsCount].x;
			float p0y = points[i].y, p1y = points[(i + 1) % pointsCount].y;
			final float k = (p0x * p1y - p1x * p0y);
			cx += (p0x + p1x) * k;
			cy += (p0y + p1y) * k;
		}
		final float d = 6f * polygonArea(points);
		cx /= d;
		cy /= d;

		return new Point(cx, cy);
	}
	
	/**
	 * Computes minimum distance from line to point
	 */
	private static float distanceFromLineToPoint(final Point p0, final Point p1, final Point p) {
		float area = (p0.x * p1.y + p1.x * p.y + p.x * p0.y 
				- p1.x * p0.y - p.x * p1.y - p0.x * p.y) / 2f;
		float base = (float) Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) 
				+ (p1.y - p0.y) * (p1.y - p0.y));
		return (float) Math.abs(2f * area / base);
	}
	
}
