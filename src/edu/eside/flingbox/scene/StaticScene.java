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
import android.os.Vibrator;
import android.view.MotionEvent;

import edu.eside.flingbox.graphics.RenderPolygon;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.SceneRenderer.Camera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.objects.AtomicBody;
import edu.eside.flingbox.physics.ScenePhysics;
import edu.eside.flingbox.physics.gravity.GravitySource;

public class StaticScene implements OnInputListener {
	
	protected final class BackgroundRender extends RenderPolygon {
		
		private final static float BACKGROUND_COLOR_R = 0.6f;
		private final static float BACKGROUND_COLOR_G = 0.6f;
		private final static float BACKGROUND_COLOR_B = 1.0f;
		
		public BackgroundRender(float left, float right, float top, float bottom) {
			super(new Point[] {
					new Point(left, top), 
					new Point(left, bottom),
					new Point(right, top),
					new Point(right, bottom)
					
			}, new short[] {
					0, 1, 2, 
					1, 2, 3
			});
			setColor(BACKGROUND_COLOR_R, BACKGROUND_COLOR_G, BACKGROUND_COLOR_B, 1.0f);
		}

	}
	
	// Constant limits for the Scene borders
	final static float SCENE_LEFT_BORDER = -1024f;
	final static float SCENE_RIGHT_BORDER = 1024f;
	final static float SCENE_BOTTOM_BORDER = -1024f;
	final static float SCENE_TOP_BORDER = 1024f;
	
	// Constants to Zoom
	final static float SCENE_MAX_WIDTH = SCENE_RIGHT_BORDER - SCENE_LEFT_BORDER;
	final static float SCENE_MIN_WIDTH = 128f;
	
	// TODO Support for all screen sizes
	protected final float mDisplayWidth = 320f;
	protected final float mDisplayHeight = 480f;
	
	// Vibrator instance
	protected Vibrator mVibrator;
	
	protected Camera mCamera;
	
	protected final SceneRenderer mSceneRenderer;
	protected final ScenePhysics mScenePhysics;
	protected final SceneGestureDetector mGestureDetector;
	
	protected final ArrayList<AtomicBody> mOnSceneBodys;
	
	protected final Context mContext;
	
	public StaticScene(Context c) {
		mContext = c;
		
		// Create list of objects.
		mOnSceneBodys = new ArrayList<AtomicBody>();
		mSceneRenderer = new SceneRenderer();
		mScenePhysics = new ScenePhysics(GravitySource.getAccelerometerBasedGravity(c));
		
		mSceneRenderer.add(new BackgroundRender(
				SCENE_LEFT_BORDER, SCENE_RIGHT_BORDER,
				SCENE_TOP_BORDER, SCENE_BOTTOM_BORDER));
		
		mGestureDetector = new SceneGestureDetector(c, this);
		
		mCamera = mSceneRenderer.getCamera();
		
		mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		
		System.gc();	// This is a good moment to call to Garbage Collector.
	}
	
	public void add(AtomicBody body) {
		mOnSceneBodys.add(body);
		mSceneRenderer.add(body.getRender());
		mScenePhysics.add(body.getPhysics());
	}
	
	public Renderer getSceneRenderer() {
		return mSceneRenderer;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}
	
	public boolean onTrackballEvent(MotionEvent ev) {
		/*
		final float width = mCamera.getWidth();
		float newX = mCamera.getX() - (ev.getX() * width / mDisplayWidth * 16);
		float newY = mCamera.getY() + (ev.getY() * width / mDisplayWidth * 16); 	// Maintain aspect radio
		
		// Set positions
		mCamera.setPosition(newX, newY, width);
		*/
		
		onZoom(0f, 0f, 1f + ev.getY() / 8f);
		
		return true;
	}


	public boolean onMultitouchScroll(MotionEvent downEvent, MotionEvent ev,
			float dX, float dY) {
		// Fit dX and dY into the openGL space
		final float width = mCamera.getWidth();
		final float newX = mCamera.getX() - (dX * width / mDisplayWidth);
		final float newY = mCamera.getY() + (dY * width / mDisplayWidth); 	// Maintain aspect radio
		
		// Set positions
		mCamera.setPosition(newX, newY, width);

		return true;
	}

	/**
	 * Called when multitouch zoom occurs.
	 * Also corrects to fit camera to scene.
	 */

	public boolean onZoom(float x, float y, float scale) {
		// Fit dX and dY into the openGL space
		final float newWidth = mCamera.getWidth() * scale;
		final float newX = mCamera.getX();
		final float newY = mCamera.getY();
	
		// Set new position
		mCamera.setPosition(newX, newY, newWidth);
		
		return true;
	}
	
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		final Camera cam = mCamera;
		// Fit dX and dY into the openGL space
		final float width = cam.getWidth();
		final float newX = cam.getX() + (distanceX * width / mDisplayWidth);
		final float newY = cam.getY() - (distanceY * width / mDisplayWidth); 	// Maintain aspect radio
		
		// Set positions
		cam.setPosition(newX, newY, width);

		return true;
	}

	@Override
	public boolean onUp(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	

}
