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

package edu.eside.flingbox.scene;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.SceneRenderer.Camera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

public abstract class StaticScene implements OnInputListener {
	
	final static float SCENE_LEFT_BORDER = -1024f;
	final static float SCENE_RIGTH_BORDER = 1024f;
	final static float SCENE_BOTTOM_BORDER = -1024f;
	final static float SCENE_TOP_BORDER = 1024f;
	
	// TODO Support for all screen sizes
	final protected float mDisplayWidth = 320f;
	final protected float mDisplayHeight = 480f;
	
	protected Camera mCamera;
	
	protected SceneRenderer mSceneRenderer;
	protected SceneGestureDetector mGestureDetector;
	
	protected ArrayList<Renderizable> mOnSceneBodys;
	
	public StaticScene(Context c) {
		// Create list of objects.
		mOnSceneBodys = new ArrayList<Renderizable>();
		
		mSceneRenderer = new SceneRenderer(mOnSceneBodys);
		
		mGestureDetector = new SceneGestureDetector(c, this);
		
		mCamera = mSceneRenderer.getCamera();
		
		System.gc();	// This is a good moment to call to Garbage Collector.
	}
	
	public Renderer getSceneRenderer() {
		return mSceneRenderer;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean onMultitouchScroll(MotionEvent downEvent, MotionEvent ev,
			float dX, float dY) {
		// Fit dX and dY into the openGL space
		final float width = mCamera.getWidth();
		final float newX = mCamera.getX() - (dX * width / mDisplayWidth);
		final float newY = mCamera.getY() + (dY * width / mDisplayWidth); 	// Maintain aspect radio
		
		mCamera.setPosition(newX, newY, width);
		
		return true;
	}

	@Override
	public boolean onZoom(float x, float y, float scale) {
		// Fit dX and dY into the openGL space
		final float newWidth = mCamera.getWidth() * scale;
		final float newX = mCamera.getX();
		final float newY = mCamera.getY();
		
		mCamera.setPosition(newX, newY, newWidth);
		
		return true;
	}




}
