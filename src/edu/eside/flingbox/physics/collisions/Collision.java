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

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;

/**
 * Class to handle collision.
 */
public final class Collision {
	
	public PhysicBody collidingBody;
	
	/** Collision's relative poition */
	public final Vector2D position;
	/** Collision's sense */
	public final Vector2D sense;
	
	/**
	 * Default constructor for a collsion
	 * 
	 * @param position relative position
	 * @param sense collisio's sense
	 */
	public Collision(Vector2D position, Vector2D sense) {
		this.position = new Vector2D(position);
		this.sense = new Vector2D(sense);
		this.collidingBody = null;
	}
	
	/**
	 * Default constructor for a collsion
	 * 
	 * @param position relative position
	 * @param sense collisio's sense
	 * @param collidingBody body wich is colliding
	 */
	public Collision(Vector2D position, Vector2D sense, PhysicBody collidingBody) {
		this.position = new Vector2D(position);
		this.sense = new Vector2D(sense);
		this.collidingBody = collidingBody;
	}
	
}