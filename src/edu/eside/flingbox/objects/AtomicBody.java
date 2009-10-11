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

import edu.eside.flingbox.graphics.SceneRenderer.Renderizable;

/**
 * An AtomicBody is a general abstraction witch handles 
 * basic data that any object should have.
 * Any physical object on scene should inherit from
 * {@link AtomicBody}.
 * 
 * Also Bodys witch will be rendered should inherit from
 * AtomicBody
 */
public abstract class AtomicBody implements Renderizable {
	// Position of the object on scene
	protected float mPositionX, mPositionY;
	protected float mAngle;

}
