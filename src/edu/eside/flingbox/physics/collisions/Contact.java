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

import java.util.Comparator;

import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;

/**
 * Class to handle contact between two bodies.
 */
public class Contact {
	/**
	 * Compares contacts to arrange it.
	 *
	 */
	public final static class UpperPositionComparator implements Comparator<Contact> {
		private Vector2D mGravity = new Vector2D(0f, -1.0f);
		
		/**
		 * return positive for an upper contact
		 */
		@Override
		public int compare(Contact contact1, Contact contact2) {
			if (contact1 == contact2)
				return 0;
			float verticalPos1 = contact1.position.projectOver(mGravity);
			float verticalPos2 = contact2.position.projectOver(mGravity);
			return verticalPos1 > verticalPos2 ? 1 : -1;
		}
		
		public void setGravity(Vector2D gravity) {
			mGravity = gravity;
		}
		
	}

	public static final UpperPositionComparator UPPER_POSITION_COMPARATOR = new UpperPositionComparator();
	
	/** First body in contact */
	public final PhysicBody bodyInContactA;
	/** Second body in contact */
	public final PhysicBody bodyInContactB;
	
	/**  */
	public Vector2D bodyASense;
	/**  */
	public Vector2D bodyBSense;
	
	/** Contact's absolute position */
	public final Vector2D position;
	/** Contact's sense. This is a normalized vector */
	public final Vector2D sense;
	/** Contact's normal. This is a normalized vector */
	public final Vector2D normal;
	
	public Contact(PhysicBody bodyA, PhysicBody bodyB, Vector2D position, Vector2D sense) {
		this.bodyInContactA = bodyA;
		this.bodyInContactB = bodyB;
		this.position = position;
		this.sense = sense.normalize();
		this.normal = Vector2D.normalVector(sense);
	}

	public Vector2D getBodysSide(PhysicBody body) {
		if (body == bodyInContactA)
			return bodyASense;
		if (body == bodyInContactB)
			return bodyBSense;
		return null;
	}
	
}