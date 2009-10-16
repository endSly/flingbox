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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLException;
import android.view.MotionEvent;

import edu.eside.flingbox.graphics.Render;
import edu.eside.flingbox.input.SceneMTGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.objects.Polygon;

/**
 * Implements drawing methods to {@link StaticScene}
 */
public abstract class DrawableScene extends StaticScene implements OnInputListener {
	
	/**
	 * {@link Renderizable} Object witch handles drawing pattern
	 * and show it to OpenGL's space.
	 */
	private class DrawingRender extends Render {

		private final ArrayList<Point> mDrawingPattern;
		
		/**
		 * Default constructor.
		 * @param drawingPattern	Actual drawing patter.
		 */
		public DrawingRender(final ArrayList<Point> drawingPattern) {
			mDrawingPattern = drawingPattern;
		}
		
		/**
		 * Renderizes pattern to {@link GL10}.
		 */
		public boolean onRender(GL10 gl) {
			// We need two or more points to render
			final int pointsCount = mDrawingPattern.size();
			if (pointsCount < 2)
				return false;
			
			try {
				// Fit points to OpenGL 
				FloatBuffer vertexBuffer = ByteBuffer
					.allocateDirect(4 * 3 * pointsCount)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();

				ShortBuffer indexBuffer = ByteBuffer
					.allocateDirect(2 *  2 * (pointsCount - 1))
					.order(ByteOrder.nativeOrder())
					.asShortBuffer();
			
				// Fit 2D points into 3D space
				ArrayList<Point> pattern = mDrawingPattern;
				for (short i = 0; i < (pointsCount - 1); ) {
					vertexBuffer.put(pattern.get(i).x);
					vertexBuffer.put(pattern.get(i).y);
					vertexBuffer.put(0.0f);
				
					indexBuffer.put(i++);	// Set indexes
					indexBuffer.put(i);
				}
				vertexBuffer.put(pattern.get(pointsCount - 1).x);	// Put also 
				vertexBuffer.put(pattern.get(pointsCount - 1).y);	// last point.
				vertexBuffer.put(0.0f);

				vertexBuffer.position(0);
				indexBuffer.position(0);
			
				// Draw it to OpenGL's space
				try {
					gl.glColor4f(0.0f, 0.0f, 0.8f, 1.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
					gl.glDrawElements(GL10.GL_LINES, 2 * (pointsCount - 1), 
							GL10.GL_UNSIGNED_SHORT, indexBuffer);
				} catch (GLException ex) {
					// Do nothing
				}
			} catch (Exception ex) {
				// Do nothing. Just skip drawing
			}
	    			
	    	return true;
		}
		
	}
	
	private DrawingRender mDrawingRender;
	private ArrayList<Point> mDrawingPattern;
	
	private boolean mIsDrawing = false;
	private boolean mIsDrawingLocked = false;	// Drawing can be locked
	
	/**
	 * Default Constructor for drawing scene
	 * @param c	Application {@link Context}
	 */
	public DrawableScene(Context c) {
		super(c);
	}

	public void onLongPress(MotionEvent e) {
		
		
	}
	
	/**
	 * Called when touch event ends correctly.
	 * Drawable scene creates the polygon for drawing pattern.
	 */
	public boolean onUp(MotionEvent ev) {
		if (!mIsDrawing) 
			return false;
		
		new Thread(new Runnable() { 
			public void run() {
				// Drawing ends
				mIsDrawing = false;
				mIsDrawingLocked = false;
				
				// Remove drawing line
				mSceneRenderer.remove(mDrawingRender);
				mDrawingRender = null;
					
				final int pointsCount = mDrawingPattern.size();
				// if we had points enough
				if (pointsCount >= 3) {
					mDrawingPattern.trimToSize();
					Polygon drawedPolygon;
					drawedPolygon = new Polygon((Point[]) mDrawingPattern.toArray(new Point[0]));
					drawedPolygon.setRandomColor();
					
					add(drawedPolygon);
					
					// Vibrate as haptic feedback
					//mVibrator.vibrate(150);
				}
				mDrawingPattern = null;

				// Good moment to call garbage collector
				System.gc();
			} 
		}).start(); 

		return true;
	}
	
	public void onSingletouchCancel() {
		// lock drawing until up event
		mIsDrawingLocked = true;
		mIsDrawing = false;
		
		// Remove actual drawing
		mOnSceneBodys.remove(mDrawingRender);
		mDrawingRender = null;
		
		// Good moment to call to Garbage Collector
		System.gc();
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		// Drawing can be locked until Up event
		if (mIsDrawingLocked || !mIsDrawing)
			return super.onScroll(downEv, ev, distanceX, distanceY);
		
		// Gets screen projection into the OpenGL space
		final float x = mCamera.left + (ev.getX() * mCamera.getWidth() / mDisplayWidth);
		final float y = mCamera.top - (ev.getY() * mCamera.getHeight() / mDisplayHeight);

		// Start drawing if not drawing.
		// ONLY FOR MULTITOUCH
		if (!mIsDrawing 
				&& (mGestureDetector instanceof SceneMTGestureDetector)) {
			
			mIsDrawing = true;
			
			mDrawingPattern = new ArrayList<Point>(40);
			mDrawingRender = new DrawingRender(mDrawingPattern);
			
			final float onDownX = mCamera.left + (downEv.getX() * mCamera.getWidth() / mDisplayWidth);
			final float onDownY = mCamera.top - (downEv.getY() * mCamera.getHeight() / mDisplayHeight);

			mDrawingPattern.add(new Point(onDownX, onDownY));

			// We only need render, no physics
			mSceneRenderer.add(mDrawingRender);
		}
		
		mDrawingPattern.add(new Point(x, y));
		return true;
	}
	
	

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// Start drawing
		mIsDrawing = true;
		
		mDrawingPattern = new ArrayList<Point>(40);
		mDrawingRender = new DrawingRender(mDrawingPattern);
		
		final float onDownX = mCamera.left + (e.getX() * mCamera.getWidth() / mDisplayWidth);
		final float onDownY = mCamera.top - (e.getY() * mCamera.getHeight() / mDisplayHeight);

		mDrawingPattern.add(new Point(onDownX, onDownY));

		mSceneRenderer.add(mDrawingRender);
		
		return true;
	}

}
