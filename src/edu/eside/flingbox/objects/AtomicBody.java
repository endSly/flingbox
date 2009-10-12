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

/**
 * An AtomicBody is a general abstraction witch handles 
 * basic data that any object should have.
 * Any physical object on scene should inherit from
 * {@link AtomicBody}.
 * 
 * Also Bodys witch will be rendered should inherit from
 * AtomicBody
 */
public abstract class AtomicBody implements Renderizable {
	
	/**
	 * Defines generic class of 2D Point and some
	 * basic functions.
	 */
	public final class Point {
		public float x, y;
		
		/**
		 * Dafult constructor for a point
		 * @param x		X
		 * @param y		Y
		 */
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Constructor for zero point
		 */
		public Point() {
			this.x = 0f;
			this.y = 0f;
		}

		/**
		 * Computes distance to p
		 * @param p		Point
		 * @return		Distance
		 */
		public float distanceToPoint(final Point p) {
			return (float) Math.abs(Math.sqrt(
					(this.x - p.x) * (this.x - p.x) +
					(this.y - p.y) * (this.y - p.y)));
		}
		
	}
	
	// Position of the object on scene
	protected Point mPosition;
	protected float mAngle;

}
