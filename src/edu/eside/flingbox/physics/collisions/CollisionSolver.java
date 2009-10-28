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
		
		final float restit = (bodyA.getRestitutionCoeficient() + bodyA.getRestitutionCoeficient()) / 2f;
		
		// Separate velocity into collision components
		final float vA = bodyA.getVelocity().dotProduct(collisionSense);
		final float vNormalA = bodyA.getVelocity().dotProduct(collisionNormal);
		final float mA = bodyA.getBodyMass();
		
		final float vB = bodyB.getVelocity().dotProduct(collisionSense);
		final float vNormalB = bodyB.getVelocity().dotProduct(collisionNormal);
		final float mB = bodyB.getBodyMass();
		
		float forceToApplyMod;
		if (mA >= Float.MAX_VALUE) {
			// We have now resultant A velocity in parallel to collision 
			float vFinalB = -vB * restit;
			// Now, compute force to be applied to the object to generate this velocity
			forceToApplyMod = (vFinalB - vB) * mB / DIFFERENTIAL_TIME;
		} else if (mB >= Float.MAX_VALUE) {
			// We have now resultant A velocity in parallel to collision 
			float vFinalA = -vA * restit;
			// Now, compute force to be applied to the object to generate this velocity
			forceToApplyMod = (vFinalA - vA) * mA / DIFFERENTIAL_TIME;
		} else  {
			// We have now resultant A velocity in parallel to collision 
			float vFinalA = ((1 + restit) * mB * vB + vA * (mA + restit * mB)) / (mA + mB);
			// Now, compute force to be applied to the object to generate this velocity
			forceToApplyMod = (vFinalA - vA) * mA / DIFFERENTIAL_TIME;
		}
		
		
		Vector2D forceToApply = new Vector2D(collisionSense).mul(forceToApplyMod);
		
		bodyB.applyForce(forceToApply, 
				new Vector2D(collisionPosition).sub(bodyB.getPosition()), 
				DIFFERENTIAL_TIME);
		bodyA.applyForce(forceToApply.negate(), 
				new Vector2D(collisionPosition).sub(bodyA.getPosition()), 
				DIFFERENTIAL_TIME);
		
	}
	
}
