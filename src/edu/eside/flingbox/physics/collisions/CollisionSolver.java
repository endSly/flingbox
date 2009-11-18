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
		
		Vector2D velA = getVelocityIntoCollisionAxis(collision, bodyA);
		Vector2D velB = getVelocityIntoCollisionAxis(collision, bodyB);
		
		if (!bodyA.isFixed()) { 
			final float vFinalA = mB < Float.MAX_VALUE  
						? ((1 + restit) * mB * velB.i + velA.i * (mA + restit * mB)) / (mA + mB)
						: -velA.i * restit;
						
			final float normalModule = (vFinalA - velA.i) * mA * 1000  / DIFFERENTIAL_TIME;
			
			final Vector2D normal = new Vector2D(collisionSense).mul(normalModule);
			final Vector2D collisionRelativePoint = new Vector2D(bodyA.getPosition()).sub(collisionPosition);
			
			bodyA.applyForce(normal, collisionRelativePoint, DIFFERENTIAL_TIME);
			solveFrictions(bodyA, normalModule, collision, velA);
		}
		
		if (!bodyB.isFixed()) { 
			final float vFinalB = mA < Float.MAX_VALUE 
						? ((1 + restit) * mA * velA.i + velB.i * (mB + restit * mA)) / (mA + mB)
						: -velB.i * restit;
						
			final float normalModule = (vFinalB - velB.i) * mB * 1000  / DIFFERENTIAL_TIME;
			
			final Vector2D normal = new Vector2D(collisionSense).mul(normalModule);
			final Vector2D collisionRelativePoint = new Vector2D(bodyB.getPosition()).sub(collisionPosition);
			
			bodyB.applyForce(normal, collisionRelativePoint, DIFFERENTIAL_TIME);
			solveFrictions(bodyB, normalModule, collision, velB);
		} 
		
	}
	
	private static void solveFrictions(final PhysicBody body, 
			final float normal, final Collision collision, 
			final Vector2D bodyProyectedVelocity) {
		float parallelVel = bodyProyectedVelocity.j;
		
		if (parallelVel == 0.0f) {
			float FrictionCoef = body.getStaticFrictionCoeficient();
			
		} else {
			float FrictionCoef = body.getDinamicFrictionCoeficient();
			
		}
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
