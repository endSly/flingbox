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
	
	public static void solveCollision(Collision collision, PhysicBody bodyA, PhysicBody bodyB) {
		final Vector2D collisionSense = collision.sense.normalize();
		final Vector2D collisionNormal = collisionSense.normalVector();
		final Vector2D collisionPosition = collision.position;
		
		final float restit = bodyA.getRestitutionCoeficient() * bodyB.getRestitutionCoeficient();
		final float mA = bodyA.getBodyMass();
		final float mB = bodyB.getBodyMass();
		
		if (mA >= Float.MAX_VALUE) {
			Vector2D velB = getVelocityIntoCollisionAxis(collision, bodyB);
			float vFinalB = -velB.i * restit;
			float forceToApply = (vFinalB - velB.i) * mB * 1000  / DIFFERENTIAL_TIME;
			
			bodyB.applyForce(new Vector2D(collisionSense).mul(forceToApply), 
					new Vector2D(bodyB.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
		} else if (mB >= Float.MAX_VALUE) {
			Vector2D velA = getVelocityIntoCollisionAxis(collision, bodyA);
			float vFinalA = -velA.i * restit;
			float forceToApply = (vFinalA - velA.i) * mA * 1000  / DIFFERENTIAL_TIME;
			
			bodyB.applyForce(new Vector2D(collisionSense).mul(forceToApply), 
					new Vector2D(bodyB.getPosition()).sub(collisionPosition),
					DIFFERENTIAL_TIME);
		} else  {
			/* TODO: Bad code, it won't work
			float vFinalA = ((1 + restit) * mB * vB + vA * (mA + restit * mB)) / (mA + mB);
			Vector2D finalVelocityA = new Vector2D(collisionSense).mul(vFinalA);
			finalVelocityA.add(new Vector2D(collisionNormal).mul(vNormalA));
			bodyA.setVelocity(finalVelocityA.i, finalVelocityA.j);
			
			float vFinalB = ((1 + restit) * mA * vA + vB * (mB + restit * mA)) / (mA + mB);
			Vector2D finalVelocityB = new Vector2D(collisionSense).mul(vFinalB);
			finalVelocityB.add(new Vector2D(collisionNormal).mul(vNormalB));
			bodyB.setVelocity(finalVelocityB.i, finalVelocityB.j);
			*/
		}

		
	}
	
	private static Vector2D getVelocityIntoCollisionAxis(Collision collision, PhysicBody body) {
		final Vector2D collisionSense = collision.sense.normalize();
		final Vector2D collisionNormal = collisionSense.normalVector();
		
		/*
		 *  Get vector from Polygon's center to collision point
		 */
		final Vector2D relativeCollisionPoint = new Vector2D(collision.position).sub(body.getPosition());
		final Vector2D velocityByAngularRotation = 
			relativeCollisionPoint
			.normalVector() // This returns new Vector2D, so don't copy
			.normalize()
			.mul(relativeCollisionPoint.length() 
					* body.getAngularVelocity());
		
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
