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
import edu.eside.flingbox.graphics.SceneRenderer.Renderizable;
import edu.eside.flingbox.math.Point;

/**
 * Final interface for a polygon. Physics and Render operations
 * are done by parents classes.
 */
public final class Polygon extends PolygonRender implements Renderizable {

	/**
	 * Generates a polygon for passed points. 
	 * It implements physical and graphical interfaces
	 * @param points	Polygon's points. {x0, y0, x1, y1, x2...}
	 * @throws IllegalArgumentException		If not points enough
	 */
	public Polygon(final Point[] points) throws IllegalArgumentException {
		super(points);
		
	}
	
	/**
	 * Sets random color to polygon
	 */
	public void setRandomColor() {
		Random rnd = new Random();
		setColor(rnd.nextFloat() ,rnd.nextFloat() ,rnd.nextFloat() , 1.0f);
	}
	
	/**
	 * Estimates if point is inside polygon by bounding box
	 * @param p		Point to check
	 * @return		true if point is inside, else false.
	 */
	public boolean isPointInside(Point p) {
		
		return false;
	}

}
