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

import java.util.Random;

import edu.eside.flingbox.graphics.PolygonRender;
import edu.eside.flingbox.graphics.Render;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.physics.PhysicObject;
import edu.eside.flingbox.physics.PhysicPolygon;
import edu.eside.flingbox.physics.PhysicObject.OnMovementListener;

public final class Polygon extends AtomicBody implements OnMovementListener {
	private final static float DEFAULT_REDUCER_EPSILON = 5.0f;
	
	private final Point[] mPoints;
	private final short mPointsCount;

	private final PolygonRender mPolygonRender;
	private final PhysicPolygon mPolygonPhysics;
	
	/**
	 * Default Constructor for a Polygon
	 * @param points	Array of Polygon's point
	 * @throws IllegalArgumentException		If not enough points
	 */
	public Polygon(final Point[] points) throws IllegalArgumentException {
		super();
		
		// Get passed points count
		final int pointsCount = points.length;
		
		// If not points enough to build a polygon.
		if (pointsCount < 3)
			throw new IllegalArgumentException("Not points enough to build a polygon.");
		
		// Optimize points by Douglas-Peucker algorithm 
		Point[] polygonPoints = PolygonUtils.douglasPeuckerReducer(points, DEFAULT_REDUCER_EPSILON);
		// Triangulate polygon. This will be needed by Physics and Render
		short[] triangulationIndexes = PolygonUtils.triangulatePolygon(polygonPoints);
		
		Point centroid = new Point();
		
		float polygonArea = PolygonUtils.normalizePolygon(polygonPoints, triangulationIndexes, centroid);
		
		mPoints = polygonPoints;
		mPointsCount = (short) (polygonPoints.length);
		
		mPolygonRender = new PolygonRender(mPoints, triangulationIndexes);
		mPolygonPhysics = new PhysicPolygon(mPoints, polygonArea, centroid, this);
	}
	
	/**
	 * @return Polygons total points. 
	 * 		@warning THIS COULD NOT MATCH WITH points IN CONSTRUCTOR!!
	 */
	public int getPointsCount() {
		return mPointsCount;
	}
	
	public Render getRender() {
		return mPolygonRender;
	}
	
	public PhysicObject getPhysics() {
		return mPolygonPhysics;
	}
	
	/**
	 * Sets random color to polygon
	 */
	public void setRandomColor() {
		Random rnd = new Random();
		mPolygonRender.setColor(rnd.nextFloat() ,rnd.nextFloat() ,rnd.nextFloat() , 1.0f);
	}
	
	/**
	 * Estimates if point is inside polygon by bounding box
	 * @param p		Point to check
	 * @return		true if point is inside, else false.
	 */
	public boolean isPointInside(Point p) {
		
		return false;
	}

	/**
	 * Called when movement occurs.
	 */
	@Override
	public void onMovement(Point deplazament, float rotation) {
		if (mPolygonRender != null)
			mPolygonRender.setPosition(deplazament, rotation);
	}

}
