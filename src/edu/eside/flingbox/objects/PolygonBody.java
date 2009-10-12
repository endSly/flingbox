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

package edu.eside.flingbox.objects;

import edu.eside.flingbox.graphics.SceneRenderer.Renderizable;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.PolygonUtils;

public abstract class PolygonBody extends AtomicBody implements Renderizable {
	private final static float DEFAULT_REDUCER_EPSILON = 5.0f;
	
	protected final Point[] mPoints;
	protected final short mPointsCount;
	
	protected final short[] mTriangulationIndexes;
	protected final short mTrianglesCount;
	
	/**
	 * Default Constructor for a Polygon
	 * @param points	Array of float with 2D polygon points {x0, y0, x1, y1, ...}
	 * @throws IllegalArgumentException		If not enough points
	 */
	public PolygonBody(final Point[] points) throws IllegalArgumentException {
		super();
		
		// Get passed points count
		final short pointsCount = (short) points.length;
		
		// If not points enough to build a polygon.
		if (pointsCount < 3)
			throw new IllegalArgumentException("Not points enough to build a polygon.");
		
		// Optimize points by Douglas-Peucker algorithm 
		mPoints = PolygonUtils.douglasPeuckerReducer(points, DEFAULT_REDUCER_EPSILON);
		// Triangulate polygon. This will be needed by Physics and Render
		mTriangulationIndexes = PolygonUtils.triangulatePolygon(mPoints);
		
		mPointsCount = (short) (mPoints.length);
		mTrianglesCount = (short) (mPointsCount - 2);
	}
	
	/**
	 * @return Polygons total points. 
	 * 		@warning THIS COULD NOT MATCH WITH points IN CONSTRUCTOR!!
	 */
	public int getPointsCount() {
		return mPointsCount;
	}

}
