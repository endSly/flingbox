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
public class Camera {
	/** Default camera aperture */
	private final static float DEFAULT_WIDTH = 256f;
	
	/** Used by OpenGL */
	public float left, rigth, top, bottom;
	/** Flag to change OpenGL's camera */
	boolean isChanged = true;
	
	/** Camera position and aperture */
	private float mX, mY, mWidth = DEFAULT_WIDTH, mHeight;
	/** Surface size */
	private int mSurfaceWidth = 100, mSurfaceHeight = 100;

	/**
	 * Set surface and calculates camera's position and width
	 * to kept aspect ratio.
	 * 
	 * @param surfaceWidth Surface's width
	 * @param surfaceHeight Surface's height
	 */
	public void setSurfaceSize(int surfaceWidth, int surfaceHeight) {
		mSurfaceWidth = surfaceWidth;
		mSurfaceHeight = surfaceHeight;
		
		mHeight = mWidth * surfaceHeight / surfaceWidth;
		
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
	public void setPosition(float x, float y, final float width) {
		mX = x;
		mY = y;
		mWidth = width;
		mHeight = width * mSurfaceHeight / mSurfaceWidth;
		
		updateGLCamera();
	}

	/** Sets coordinates needed by OpenGL from camera's position */
	private void updateGLCamera() {
		final float halfWidth = mWidth / 2;
		final float halfHeight = mHeight / 2;
		
		this.left = mX - halfWidth;
		this.rigth = mX + halfWidth;
		this.bottom = mY - halfHeight;
		this.top = mY + halfHeight;
		
		this.isChanged = true;
	}
	
	/** @return projected vector */
	public Vector2D project(Vector2D v) {
		return v.set(left + (v.i * mWidth / mSurfaceWidth), 
				top - (v.j * mHeight / mSurfaceHeight));
	}
	
	/** @return	x of camera's position */
	public float getX() {
		return mX;
	}
	
	/** @return	y of camera's position */
	public float getY() {
		return mY;
	}
	
	/** @return	width of camera's frame */
	public float getWidth() {
		return mWidth;
	}
	
	/** @return	height of camera's frame */
	public float getHeight() {
		return mHeight;
	}
}