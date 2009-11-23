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

package edu.eside.flingbox.objects;

import edu.eside.flingbox.graphics.Render;
import edu.eside.flingbox.physics.PhysicBody;

/**
 * Body is a general abstraction class which handles 
 * basic data that any object should have.
 * Any physical object on scene should inherit from
 * {@link Body}.
 * 
 * Bodies which will be rendered should inherit from
 * {@link Body} too.
 */
public abstract class Body {
	
	// Instance of the body to be rendered
	protected Render mRender;
	// Instance of the body in the physic space
	protected PhysicBody mPhysics;
	
	/**
	 * Local constructor for any body.
	 * 
	 * @param render {@link Render} instance of body
	 * @param physics {@link PhysicObject} instance of body
	 * @hide
	 */
	protected Body(Render render, PhysicBody physics) {
		mRender = render;
		mPhysics = physics;
	}
	
	/**
	 * @return object's render instance. NOTE: Can be null!
	 */
	public Render getRender() {
		return mRender;
	}
	
	/**
	 * @return object's physics instance. NOTE: Can be null!
	 */
	public PhysicBody getPhysics() {
		return mPhysics;
	}
	
}
