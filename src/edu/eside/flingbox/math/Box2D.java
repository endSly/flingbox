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
 * Class for a basic Box.
 *
 */
public final class Box2D {
	// Box coordinates
	public final Point topLeft;
	public final Point bottomRight;
	
	public Box2D() {
		this.topLeft = new Point();
		this.bottomRight = new Point();
	}

	/**
	 * Default Constructor for a Box
	 * @param topLeft
	 * @param bottomRight
	 */
	public Box2D(Point topLeft, Point bottomRight) {
		this.topLeft = new Point(topLeft);
		this.bottomRight = new Point(bottomRight);
	}
	
	/**
	 * Copy constructor
	 * @param box box to me copied
	 */
	public Box2D(Box2D box) {
		this.topLeft = new Point(box.topLeft);
		this.bottomRight = new Point(box.bottomRight);
	}
	
	public boolean isPointInside(Point p) {
		return (p.x > this.topLeft.x) && (p.x < this.bottomRight.x) &&
			(p.y > this.bottomRight.y) && (p.y < this.topLeft.y);
	}
	
}
