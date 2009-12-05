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

package edu.eside.flingbox.graphics;

import javax.microedition.khronos.opengles.GL10;

/**
 * {@link Renderizable} interface witch should be 
 * implemented by on scene bodys to be rendered.
 */
public abstract class RenderBody {
	/**
	 * Called when object has to be rendered.
	 * When {link onRender} called a new OpenGL's matrix has
	 * been pushed to stack, so do not use gl.glPushMatrix() or
	 * gl.glPopMatrix().
	 * 
	 * @param gl	OpenGL's space
	 * @return		true if render consumed
	 */
	public abstract boolean onRender(GL10 gl);
}
