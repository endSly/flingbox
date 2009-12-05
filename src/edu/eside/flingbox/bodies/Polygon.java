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

package edu.eside.flingbox.bodies;

import java.util.Random;

import android.util.Log;

import edu.eside.flingbox.graphics.RenderPolygon;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicPolygon;
import edu.eside.flingbox.physics.PhysicBody.OnMovementListener;

/**
 * Polygon is a general class which handles the physics
 * and render instances of a polygonal Body.
 */
public final class Polygon extends Body implements OnMovementListener {
	private final Vector2D[] mPoints;
	private final short mPointsCount;

	/**
	 * Constructor for a Polygon
	 * @param polygonPoints Array of Polygon's point, this is stored and modified
	 * @throws IllegalArgumentException If not enough points
	 */
	public Polygon(Vector2D[] polygonPoints) throws IllegalArgumentException {
		super(null, null);
		
		if (polygonPoints.length < 3) {
			Log.e("Flingbox", "Trying to build a polygon with an insuficient number of points");
			throw new IllegalArgumentException("Not enough points to build a polygon.");
		}
		
		/* 
		 * Set points in Clock-wise order 
		 */
		float polygonArea = PolygonUtils.polygonArea(polygonPoints);
		if (polygonArea > 0) {
			/* If points are in anti-Clock-wise order the
			 * returned area will be positive, else, it 
			 * will be negative.
			 */
			Vector2D temp;
			for (int i = 0, j = polygonPoints.length - 1; i<j; --j, ++i) {
				temp = polygonPoints[i];  // Just swap polygon order
				polygonPoints[i] = polygonPoints[j];
				polygonPoints[j] = temp;
			}	
		} else 
			polygonArea = -polygonArea;

		/* 
		 * Relocate polygon to set the centroid at point (0, 0) 
		 */
		Vector2D centroid = PolygonUtils.polygonCentroid(polygonPoints);
		for (Vector2D p : polygonPoints) 
			p.sub(centroid);

		mPoints = polygonPoints;
		mPointsCount = (short) (polygonPoints.length); 
		mRender = new RenderPolygon(polygonPoints);
		mPhysics = new PhysicPolygon(polygonPoints, polygonArea, centroid, this);
	}
	
	/**
	 * @return Polygon points
	 */
	public Vector2D[] getPoints() {
		return mPoints;
	}

	/**
	 * @return Polygons total points. 
	 */
	public short getPointsCount() {
		return mPointsCount;
	}
	
	/**
	 * Sets random color to polygon
	 */
	public void setRandomColor() {
		Random rnd = new Random();
		((RenderPolygon) mRender).setColor(rnd.nextFloat() ,rnd.nextFloat() ,rnd.nextFloat() , 1.0f);
	}

	/**
	 * Called when movement occurs.
	 */
	public void onMovement(Vector2D position, float rotation) {
		((RenderPolygon) mRender).setPosition(position, rotation);
	}

}
