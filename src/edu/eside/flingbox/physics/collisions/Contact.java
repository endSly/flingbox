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

package edu.eside.flingbox.physics.collisions;

import edu.eside.flingbox.math.Intersect;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.objects.Body;

/**
 * Class to handle collision.
 */
public final class Contact {
	
	public Intersect intersection;
	public Body[] bodysInContact;
	
	/** Collision's absolute position */
	public final Vector2D position;
	/** Collision's sense. This is a normalized vector */
	public final Vector2D sense;
	/** Collision's normal. This is a normalized vector */
	public final Vector2D normal;
	
	public final Vector2D[] intersectionContour;
	
	/**
	 * Default constructor for a collision
	 * 
	 * @param position relative position
	 * @param sense collisio's sense
	 */
	public Contact(Vector2D position, Vector2D sense) {
		this.position = new Vector2D(position);
		this.sense = new Vector2D(sense).normalize();
		this.normal = Vector2D.normalVector(this.sense);
		this.intersectionContour = null;
	}
	
}