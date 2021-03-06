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
 * Contact solver solves contacts between two bodies and applies necessaries
 * forces over each body. This class has all members static due performance
 * improvement
 */
public class ContactSolver {

    /** Prevent solver creation */
    private ContactSolver() {
    }

    /**
     * Computes contacts effects to body's Conditions for a contact solver (I
     * total) = (I total at the end) Vel diference at end = (Vel diference at
     * begin) * e so: (I1 + I2) = (I1f + I2f) (v1f - v2f) = (v1 - v2) * e
     * 
     * @param contact
     *            contact descriptor
     * @param bodyA
     *            first colliding body
     * @param bodyB
     *            second colliding body
     */
    public static void solveCollision(final Contact contact) {
        final PhysicBody collidingBody = contact.collidingBody; // Colliding
                                                                // body is
                                                                // movable
        final PhysicBody collidedBody = contact.collidedBody; // Collided body
                                                              // can or cannot
                                                              // be movable

        final float restit = (collidingBody.getRestitutionCoeficient() * collidedBody
                .getRestitutionCoeficient());

        /* Get velocity and mass of colliding body */
        final Vector2D relativeVel = contact.getRelativeVelocity();
        final Vector2D relativeAgainstVel = new Vector2D(contact.normal)
                .mul(relativeVel.projectOver(contact.normal));

        /* Compute final velocity */
        final Vector2D impulseToApply = new Vector2D();
        if (collidedBody.isFixed()) // Other body is fixed
            /* Same as down but collidedMass is infinite, so: */
            impulseToApply.set(relativeAgainstVel).mul(
                    (1 + restit) * collidingBody.getBodyMass());
        else { // If collided body can be moved is a little bit more complicated
            /*
             * We will work with relative velocity see
             * http://en.wikipedia.org/wiki/Inelastic_collision for explanation
             */
            final float collidingMass = collidingBody.getBodyMass();
            final float collidedMass = collidedBody.getBodyMass();
            impulseToApply.set(relativeAgainstVel).mul(
                    collidingMass * collidedMass * (1 + restit)
                            / (collidingMass + collidedMass));
        }

        /* Get resultant impulse as addition of normal and friction */
        final Vector2D frictionImpulse = computeFrictionImpulse(collidingBody,
                impulseToApply.length(),
                relativeVel.projectOver(contact.sense), contact.sense);
        final Vector2D collisionImpuse = impulseToApply.add(frictionImpulse);

        /* Where impulse is applied */
        Vector2D contactRelativePoint = new Vector2D(collidingBody
                .getPosition()).sub(contact.position);
        collidingBody.applyImpulse(collisionImpuse, contactRelativePoint);

        if (!collidedBody.isFixed()) { // Other body also has an impulse
            contactRelativePoint.set(collidedBody.getPosition()).sub(
                    contact.position);
            collidedBody.applyImpulse(collisionImpuse.negate(),
                    contactRelativePoint);
        }
    }

    /**
     * Keeps bodies outside for other bodies
     * 
     * @param contact
     *            contact descriptor
     * @param bodyA
     *            first body in contact
     * @param bodyB
     *            second body in contact
     */
    public static void solvePenetration(Contact contact) {
        final PhysicBody colliding = contact.collidingBody;
        final PhysicBody collided = contact.collidedBody;
        float penetration = contact.getIntersect().getIntersectionDepth();
        final Vector2D penetrationFix = new Vector2D(contact.normal)
                .mul(penetration);
        final Vector2D relativePosition = new Vector2D(colliding.getPosition())
                .sub(collided.getPosition());
        if (!penetrationFix.isAtSameSide(relativePosition))
            penetrationFix.negate();
        colliding.setPosition(colliding.getPosition().add(penetrationFix));
    }

    /**
     * Computes friction force's module for a given contact normal. Friction can
     * be static or dynamic, when body's velocity is not enough to exceed
     * friction, static friction is applied, else dynamic friction is applied
     * 
     * @param body
     *            body witch friction will be computed
     * @param normal
     *            normal force generated by the contact
     * @param bodyVelocity
     *            velocity along contact. Velocity should be decompose before
     *            pass it as a parameter.
     * @param frictionDirection
     *            normalized vector with direction.
     * @return Friction force vector. it has to be applied along to bodyVelocity
     */
    private static Vector2D computeFrictionImpulse(final PhysicBody body,
            float normal, float bodyVelocity, final Vector2D frictionDirection) {
        float staticFrictionForce = body.getStaticFrictionCoeficient() * normal;

        final float currentVel = Math.abs(bodyVelocity);
        final float staticFrictionVelDiff = Math.abs(staticFrictionForce
                / body.getBodyMass());
        float module;
        /* Check if friction makes too much force */
        if (currentVel < staticFrictionVelDiff)
            /* Friction force stops body */
            module = -bodyVelocity * body.getBodyMass();
        else
            /* Friction force can't stop body, and it is constant */
            module = -Math.signum(bodyVelocity)
                    * body.getDynamicFrictionCoeficient() * Math.abs(normal);

        return new Vector2D(frictionDirection).mul(-module);
    }

}
