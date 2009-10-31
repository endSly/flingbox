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

public class CollisionSolver {
	
	private final static float DIFFERENTIAL_TIME = 10.0f;
	
	/**
	 * Computes collisions efects to body's
	 * 
	 * @param collision collision descriptor
	 * @param bodyA first colliding body
	 * @param bodyB second colliding body
	 */
	public static void solveCollision(final Collision collision, final PhysicBody bodyA, final PhysicBody bodyB) {
		final Vector2D collisionSense = collision.sense;
		final Vector2D collisionPosition = collision.position;
		
		final float restit = (bodyA.getRestitutionCoeficient() + bodyB.getRestitutionCoeficient()) / 2;
		final float mA = bodyA.getBodyMass();
		final float mB = bodyB.getBodyMass();
		
		if (mA >= Float.MAX_VALUE) { // Body A can't be moved
			Vector2D velB = getVelocityIntoCollisionAxis(collision, bodyB);
			final float vFinalB = -velB.i * restit;
			final float forceToApply = (vFinalB - velB.i) * mB * 1000  / DIFFERENTIAL_TIME;
			
			bodyB.applyForce(new Vector2D(collisionSense).mul(forceToApply), 
					new Vector2D(bodyB.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
		} else if (mB >= Float.MAX_VALUE) { // Body B can't be moved
			Vector2D velA = getVelocityIntoCollisionAxis(collision, bodyA);
			final float vFinalA = -velA.i * restit;
			final float forceToApply = (vFinalA - velA.i) * mA * 1000  / DIFFERENTIAL_TIME;
			
			bodyA.applyForce(new Vector2D(collisionSense).mul(forceToApply), 
					new Vector2D(bodyA.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
		} else  {
			Vector2D velA = getVelocityIntoCollisionAxis(collision, bodyA);
			Vector2D velB = getVelocityIntoCollisionAxis(collision, bodyB);
			
			final float vFinalA = ((1 + restit) * mB * velB.i + velA.i * (mA + restit * mB)) / (mA + mB);
			final float vFinalB = ((1 + restit) * mA * velA.i + velB.i * (mB + restit * mA)) / (mA + mB);
			
			final float forceToApplyA = (vFinalA - velA.i) * mA * (1000f  / DIFFERENTIAL_TIME);
			final float forceToApplyB = (vFinalB - velB.i) * mB * (1000f  / DIFFERENTIAL_TIME);
			
			bodyA.applyForce(new Vector2D(collisionSense).mul(forceToApplyA), 
					new Vector2D(bodyA.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
			bodyB.applyForce(new Vector2D(collisionSense).mul(forceToApplyB), 
					new Vector2D(bodyB.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
		}
		
	}
	
	public static void solveFrictions(final Collision collision, final PhysicBody bodyA, final PhysicBody bodyB) {
		
	}
	
	/**
	 * Obtains a Vector with velocity components proyected to collision's sense.
	 * In the x axis it returns velocity against the collision
	 * 
	 * @param collision collision
	 * @param body body to be collided
	 * @return velocity components proyected
	 */
	private static Vector2D getVelocityIntoCollisionAxis(final Collision collision, final PhysicBody body) {
		final Vector2D collisionSense = collision.sense;
		final Vector2D collisionNormal = collision.normal;
		
		/*
		 *  Get vector from Polygon's center to collision point
		 */
		final Vector2D relativeCollisionPoint = new Vector2D(collision.position).sub(body.getPosition());
		final Vector2D velocityByAngularRotation = 
			relativeCollisionPoint
			.normalVector() // This returns new Vector2D, so don't copy
			.normalize()
			.mul(relativeCollisionPoint.length() 
					* body.getAngularVelocity()); // / (float) (2f * Math.PI));
		
		/*
		 * Get total body's total velocity at collision point 
		 * NOTE: velocityByAngularRotation is not duplicated since it won't be longer used.
		 */
		final Vector2D totalVelocity = velocityByAngularRotation.add(body.getVelocity()); 
		// Discompose into components
		float velAgainstCollision = 0f;
		if (totalVelocity.dotProduct(relativeCollisionPoint) > 0)
			velAgainstCollision = totalVelocity.dotProduct(collisionSense);
		
		final float velAlongCollision = totalVelocity.dotProduct(collisionNormal);
		
		return new Vector2D(velAgainstCollision, velAlongCollision);
	}
	
}
