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

import edu.eside.flingbox.math.Vector2D;

/**
 * Specifies OpenGL camera interface.
 * By setting camera's position and width camera could 
 * be moved.
 */
public class RenderCamera {
	/** Used by OpenGL */
	public float left, rigth, top, bottom;
	
	/** Flag to change OpenGL's camera */
	public boolean isChanged;
	
	/** Camera position and aperture */
	private final Vector2D mPosition, mAperture;
	
	/** Surface size */
	private float mSurfaceWidth, mSurfaceHeight;
	private float mAspectRatio;

	public RenderCamera(float surfaceWidth, float surfaceHeight) {
		mPosition = new Vector2D();
		mAperture = new Vector2D(surfaceWidth, surfaceHeight);
		
		this.setSurface(surfaceWidth, surfaceHeight);
	}
	
	public void setSurface(float surfaceWidth, float surfaceHeight) {
		mSurfaceWidth = surfaceWidth;
		mSurfaceHeight = surfaceHeight;
		
		mAspectRatio = surfaceWidth / surfaceHeight;
		
		updateGLCamera();
	}
	
	/**
	 * Sets Camera's position.
	 * 
	 * @param x Center of the focus, x
	 * @param y Center of the focus, y
	 * @param width Width of camera's frame.
	 * 		height is calculated to keep aspect ratio
	 */
	public void setPosition(final Vector2D newPosition) {
		mPosition.set(newPosition);

		updateGLCamera();
	}
	
	public void setAperture(float horizontalAperture) {
		mAperture.set(horizontalAperture, horizontalAperture / mAspectRatio);
		
		updateGLCamera();
	}

	/** Sets coordinates needed by OpenGL from camera's position */
	private void updateGLCamera() {
		final float halfWidth = mAperture.i / 2;
		final float halfHeight = mAperture.j / 2;
		
		this.left = mPosition.i - halfWidth;
		this.rigth = mPosition.i + halfWidth;
		this.bottom = mPosition.j - halfHeight;
		this.top = mPosition.j + halfHeight;
		
		this.isChanged = true;
	}
	
	/** @return projected vector */
	public Vector2D project(Vector2D v) {
		return v.set(left + (v.i * mAperture.i / mSurfaceWidth), 
				top - (v.j * mAperture.j / mSurfaceHeight));
	}
	
	/** @return	camera's position */
	public Vector2D getPosition() {
		return mPosition;
	}
	
	/** @return	camera's aperture */
	public Vector2D getAperture() {
		return mAperture;
	}
}
