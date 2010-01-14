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
import edu.eside.flingbox.utils.PositionComparator;

/**
 * Class to handle contact between two bodies.
 */
public class Contact implements PositionComparator.Positionable {
	/** First body in contact */
	public final PhysicBody collidingBody;
	/** Second body in contact */
	public final PhysicBody collidedBody;
	
	/** Contact's absolute position */
	public final Vector2D position;
	/** Contact's sense. This is a normalized vector */
	public final Vector2D sense;
	/** Contact's normal. This is a normalized vector */
	public final Vector2D normal;
	
	/** Contact's relative velocity */
	private final Vector2D mRelativeVelocity = new Vector2D();
	
	/** When false contact is not really a collision */
	private boolean mIsCollision;
	
	
	public Contact(PhysicBody bodyA, PhysicBody bodyB, Vector2D position, Vector2D sense) {
		if (!bodyA.isFixed()) {
			this.collidingBody = bodyA; // A is colliding
			this.collidedBody = bodyB;
		} else {
			this.collidingBody = bodyB; // B is colliding
			this.collidedBody = bodyA;
		} 
		this.position = position;
		this.sense = sense.normalize();
		this.normal = Vector2D.normalVector(sense);
		
		processRelativeVelocity();
	}

	public Vector2D getBodysSide(PhysicBody body) {

		return null;
	}
	
	public boolean concerns(PhysicBody body) {
		return (this.collidingBody == body) || (this.collidedBody == body);
	}
	
	public PhysicBody otherBody(PhysicBody body) {
		if (!concerns(body))
			return null;
		return (this.collidingBody == body) ? this.collidedBody : this.collidingBody;
	}

	@Override
	public Vector2D getPosition() {
		return this.position;
	}
	
	public Vector2D getRelativeVelocity() {
		return mRelativeVelocity;
	}
	
	public boolean isCollision() {
		return mIsCollision;
	}

	private void processRelativeVelocity() {
		final PhysicBody bodyA = this.collidingBody;
		final PhysicBody bodyB = this.collidedBody;
		final Vector2D contactPointA = new Vector2D(this.position).sub(bodyA.getPosition());
		final Vector2D contactPointB = new Vector2D(this.position).sub(bodyB.getPosition());
		
		final Vector2D relativeVel = new Vector2D();
		relativeVel.add(bodyB.getVelocity());
		relativeVel.sub(bodyA.getVelocity());
		
		final Vector2D relativePosition = new Vector2D();
		relativePosition.add(contactPointB);
		relativePosition.sub(contactPointA);
		
		mIsCollision = relativeVel.isAtSameSide(relativePosition);

		relativeVel.add(Vector2D.normalVector(contactPointB).mul(bodyB.getAngularVelocity()));
		relativeVel.sub(Vector2D.normalVector(contactPointA).mul(bodyA.getAngularVelocity()));
		
		mRelativeVelocity.set(relativeVel);
	}

	
	
}