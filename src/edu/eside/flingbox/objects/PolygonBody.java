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

import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.math.PolygonUtils;

public abstract class PolygonBody extends AtomicBody implements Renderizable {
	protected final float[] mPoints;
	protected final short mPointsCount;
	
	protected final short[] mTriangulationIndexes;
	protected final short mTrianglesCount;
	
	/**
	 * Default Constructor for a Polygon
	 * @param points	Array of float with 2D polygon points {x0, y0, x1, y1, ...}
	 * @throws IllegalArgumentException		If not enough points
	 */
	public PolygonBody(float[] points) throws IllegalArgumentException {
		final short pointsCount = (short) (points.length / 2);
		
		if (pointsCount < 3)
			throw new IllegalArgumentException("Not points enough to build a polygon.");
		
		mPoints = PolygonUtils.douglasPeuckerReducer(points, 4.0f);
		mTriangulationIndexes = PolygonUtils.triangulatePolygon(mPoints);
		
		mPointsCount = (short) (mPoints.length / 2);;
		mTrianglesCount = (short) (mPointsCount - 2);
	}
	
	public int getPointsCount() {
		return mPointsCount;
	}

}
