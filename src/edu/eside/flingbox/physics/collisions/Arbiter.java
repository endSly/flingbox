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
import java.util.ConcurrentModificationException;

import android.util.Log;

import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.utils.PositionComparator;

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
		Collections.sort(contactsToSolve, PositionComparator.UPPER_COMPARATOR);

		final ArrayList<Contact> isolatedContactTree = new ArrayList<Contact>();

		while (!contactsToSolve.isEmpty()) {
			isolatedContactTree.clear();
			Contact treeRoot = contactsToSolve.get(0); // get top contact
			contactsToSolve.remove(0);			
			isolatedContactTree.add(treeRoot);
			
			getIsolatedContactsTree(treeRoot.collidedBody, contactsToSolve, isolatedContactTree);
			getIsolatedContactsTree(treeRoot.collidingBody, contactsToSolve, isolatedContactTree);
			
			solveIsolatedContactTree(isolatedContactTree);
		}

	}
	
	private void solveIsolatedContactTree(ArrayList<Contact> isolatedContacts) {
		//Vector2D totalImpulse = new Vector2D();
		for (Contact contact : isolatedContacts) {
			ContactSolver.solveCollision(contact);
			ContactSolver.solvePenetration(contact);
		}
		for (int i = isolatedContacts.size() - 2; i >= 0; i--) {
			ContactSolver.solveCollision(isolatedContacts.get(i));
			ContactSolver.solvePenetration(isolatedContacts.get(i));
		}
		
	}
	
	private void getIsolatedContactsTree(final PhysicBody rootBody, final ArrayList<Contact> contacts, 
			ArrayList<Contact> isolatedContacts) {
		try {
			for (int i = 0; i < contacts.size(); i++) {
				Contact contact = contacts.get(i);
				if (contact.concerns(rootBody)) {
					contacts.remove(contact);
					i--;
					isolatedContacts.add(contact);
					getIsolatedContactsTree(contact.otherBody(rootBody), contacts, isolatedContacts);
				}
			}
		} catch (ConcurrentModificationException ex) {
			Log.e("flingbox", "ConcurrentModificationException getting isolated contacts tree", ex);
		}
			
	}
}
