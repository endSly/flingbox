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

package edu.eside.flingbox.physics;

import edu.eside.flingbox.math.Point;

public abstract class PhysicObject {
	protected final static float MAX_MASS = Float.MAX_VALUE;
	
	protected final float mBodyMass;
	protected final Point mPosition;
	
	public PhysicObject(final float bodyMass, final Point position) {
		mBodyMass = bodyMass;
		mPosition = position;
	}
	
	public float getBodyMass() {
		return mBodyMass;
	}
	
	public Point getPosition() {
		return mPosition;
	}
}
