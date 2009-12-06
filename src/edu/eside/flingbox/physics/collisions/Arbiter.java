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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Arbiter manages the collisions between bodies
 */
public class Arbiter {
	
	
	/** List with all colliders on the scene */
	private final ArrayList<Collider> mCollisionableBodies = new ArrayList<Collider>();
	
	/** Adds new collider to the arbiter */
	public void add(final Collider collider) {
		mCollisionableBodies.add(collider);
	}
	
	/** Removes a collider from the arbiter */
	public boolean remove(Collider collider) {
		return mCollisionableBodies.remove(collider);
	}
	
	/**
	 * Checks collision between bodies that are managed by the arbiter.
	 * 
	 * @return number of collisions
	 */
	public void checkCollisions() {
		final ArrayList<Collider> bodies = mCollisionableBodies;
		final ArrayList<Contact> contactsToSolve = new ArrayList<Contact>();
		final int bodiesCount = bodies.size();
		
		/* Start checking movable objects */
		for (int i = bodiesCount - 1 ; i >= 0; i--) 
			for (int j = i - 1; j >= 0; j--) {
				Contact[] contacts = bodies.get(i).checkContacts(bodies.get(j));
				for (Contact contact : contacts)
					contactsToSolve.add(contact);
			}
		/* Sort contacts to solve those */
		Collections.sort(contactsToSolve, Contact.UPPER_POSITION_COMPARATOR);
		for (Contact contact : contactsToSolve)
			ContactSolver.solveContact(contact);
		
		for (int i = contactsToSolve.size() - 1; i >= 0; i--) 
			ContactSolver.solveContact(contactsToSolve.get(i));
	}
}
