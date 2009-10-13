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

import edu.eside.flingbox.graphics.Render;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.SceneRenderer.Camera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

public abstract class StaticScene implements OnInputListener {
	
	// Constant limits for the Scene borders
	final static float SCENE_LEFT_BORDER = -1024f;
	final static float SCENE_RIGTH_BORDER = 1024f;
	final static float SCENE_BOTTOM_BORDER = -1024f;
	final static float SCENE_TOP_BORDER = 1024f;
	
	// Constants to Zoom
	final static float SCENE_MAX_WIDTH = SCENE_RIGTH_BORDER - SCENE_LEFT_BORDER;
	final static float SCENE_MIN_WIDTH = 128f;
	
	// TODO Support for all screen sizes
	protected final float mDisplayWidth = 320f;
	protected final float mDisplayHeight = 480f;
	
	// Vibrator instance
	//protected Vibrator mVibrator;
	
	protected Camera mCamera;
	
	protected SceneRenderer mSceneRenderer;
	protected SceneGestureDetector mGestureDetector;
	
	protected ArrayList<Render> mOnSceneBodys;
	
	public StaticScene(Context c) {
		//mVibrator = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
		
		// Create list of objects.
		mOnSceneBodys = new ArrayList<Render>();
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
	
	public boolean onTrackballEvent(MotionEvent ev) {
		final float width = mCamera.getWidth();
		float newX = mCamera.getX() - (ev.getX() * width / mDisplayWidth * 16);
		float newY = mCamera.getY() + (ev.getY() * width / mDisplayWidth * 16); 	// Maintain aspect radio
		
		// Set positions
		mCamera.setPosition(newX, newY, width);
		
		fitCameraToScene();
		
		return true;
	}

	@Override
	public boolean onMultitouchScroll(MotionEvent downEvent, MotionEvent ev,
			float dX, float dY) {
		// Fit dX and dY into the openGL space
		final float width = mCamera.getWidth();
		float newX = mCamera.getX() - (dX * width / mDisplayWidth);
		float newY = mCamera.getY() + (dY * width / mDisplayWidth); 	// Maintain aspect radio
		
		// Set positions
		mCamera.setPosition(newX, newY, width);
		
		fitCameraToScene();
		
		return true;
	}

	/**
	 * Called when multitouch zoom occurs.
	 * Also corrects to fit camera to scene.
	 */
	@Override
	public boolean onZoom(float x, float y, float scale) {
		// Fit dX and dY into the openGL space
		float newWidth = mCamera.getWidth() * scale;
		float newX = mCamera.getX();
		float newY = mCamera.getY();
		
		// Check that zoom is in the range
		if (newWidth > SCENE_MAX_WIDTH) {
			newWidth = SCENE_MAX_WIDTH;
		} else if (newWidth < SCENE_MIN_WIDTH) {
			newWidth = SCENE_MIN_WIDTH;
		}
			
		// Set new position
		mCamera.setPosition(newX, newY, newWidth);
		
		fitCameraToScene();

		return true;
	}
	
	private void fitCameraToScene() {
		float newX = mCamera.getX();
		float newY = mCamera.getY();
		float width = mCamera.getWidth();
		
		boolean doSceneFit = false; 
		// Now check borders. Correct if goes out of range
		if (mCamera.left < SCENE_LEFT_BORDER) {
			newX += SCENE_LEFT_BORDER - mCamera.left;
			doSceneFit = true;
		} else if (mCamera.rigth > SCENE_RIGTH_BORDER) {
			newX -= mCamera.rigth - SCENE_RIGTH_BORDER;
			doSceneFit = true;
		}
		if ((mCamera.bottom < SCENE_BOTTOM_BORDER) 
				&& (mCamera.top < SCENE_TOP_BORDER)) {
			newY += SCENE_BOTTOM_BORDER - mCamera.bottom;
			
			doSceneFit = true;
		} else if ((mCamera.bottom > SCENE_BOTTOM_BORDER) 
				&& (mCamera.top > SCENE_TOP_BORDER)) {
			newY -= mCamera.top - SCENE_TOP_BORDER;
			doSceneFit = true;
		}
		
		// Set new corrected position
		if (doSceneFit)
			mCamera.setPosition(newX, newY, width);
		
	}


}
