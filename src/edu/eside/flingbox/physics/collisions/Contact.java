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
import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.physics.PhysicPolygon;

/**
 * Class to handle contact description.
 */
public class Contact {
	/** Description of intersection */
	public final Intersect intersect;
	/** First body in contact */
	public final PhysicBody bodyInContactA;
	/** Second body in contact */
	public final PhysicBody bodyInContactB;
	
	/** Contact's absolute position */
	public final Vector2D position;
	/** Contact's sense. This is a normalized vector */
	public final Vector2D sense;
	/** Contact's normal. This is a normalized vector */
	public final Vector2D normal;
	
	
	public Contact(PhysicBody bodyA, PhysicBody bodyB, Intersect intersect) {
		this.intersect = intersect;
		this.bodyInContactA = bodyA;
		this.bodyInContactB = bodyB;
		this.position = new Vector2D(intersect.ingoingPoint)
			.add(intersect.outgoingPoint).mul(0.5f);
		this.sense = new Vector2D(intersect.outgoingPoint)
			.sub(intersect.ingoingPoint).normalize();
		this.normal = Vector2D.normalVector(sense);
	}
	
	public Vector2D getBodysSide(PhysicBody body) {
		if (body instanceof PhysicPolygon) {
			Vector2D[] polygonContour = ((ColliderPolygon) body.getCollider()).getPolygonContour();
			return intersect.polygonsSide(polygonContour);
		}
		
		return null;
	}
	
}