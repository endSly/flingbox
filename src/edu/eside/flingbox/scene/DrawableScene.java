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

import edu.eside.flingbox.graphics.RenderBody;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.bodies.Polygon;

/**
 * Implements drawing methods to {@link StaticScene}
 */
public class DrawableScene extends StaticScene implements OnInputListener {
	
	/**
	 * {@link Renderizable} Object witch handles drawing pattern
	 * and show it to OpenGL's space.
	 */
	private class DrawingRender extends RenderBody {

		/** Array of points to be drawn */
		private final ArrayList<Vector2D> mDrawingPattern;
		
		/** Flag to lock drawing */
		private boolean mDoRender = true;
		
		/**
		 * Default constructor.
		 * @param drawingPattern	Actual drawing patter.
		 */
		public DrawingRender(final ArrayList<Vector2D> drawingPattern) {
			mDrawingPattern = drawingPattern;
		}
		
		/**
		 * Renderizes pattern to {@link GL10}.
		 */
		public boolean onRender(GL10 gl) {
			// We need two or more points to render
			final int pointsCount = mDrawingPattern.size();
			if (pointsCount < 2 || !mDoRender)
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
				final ArrayList<Vector2D> pattern = mDrawingPattern;
				for (short i = 0; i < (pointsCount - 1); ) {
					vertexBuffer.put(pattern.get(i).i);
					vertexBuffer.put(pattern.get(i).j);
					vertexBuffer.put(0.0f);
				
					indexBuffer.put(i++);	// Set indexes
					indexBuffer.put(i);
				}
				vertexBuffer.put(pattern.get(pointsCount - 1).i);	// Put also 
				vertexBuffer.put(pattern.get(pointsCount - 1).j);	// last point.
				vertexBuffer.put(0.0f);

				vertexBuffer.position(0);
				indexBuffer.position(0);
			
				// Draw it to OpenGL's space
				try {
					gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
					gl.glDrawElements(GL10.GL_LINES, 2 * (pointsCount - 1), 
							GL10.GL_UNSIGNED_SHORT, indexBuffer);
				} catch (GLException ex) {
					// Do nothing
					return false;
				}
			} catch (Exception ex) {
				// Do nothing. Just skip drawing this frame
				return false;
			}
	    	return true;
		}
	}
	
	private DrawingRender mDrawingRender;
	private ArrayList<Vector2D> mDrawingPattern = new ArrayList<Vector2D>();
	
	private boolean mIsDrawing = false;
	private boolean mIsDrawingLocked = false;	// Drawing can be locked
	
	/**
	 * Default Constructor for drawing scene
	 * @param c	Application {@link Context}
	 */
	public DrawableScene(Context c) {
		super(c);
		
		mDrawingRender = new DrawingRender(mDrawingPattern);
	}

	public void onLongPress(MotionEvent e) {
		
		
	}
	
	/**
	 * Called when touch event ends correctly.
	 * Drawable scene creates the polygon for drawing pattern.
	 */
	public boolean onUp(MotionEvent ev) {
		if (!mIsDrawing || mDrawingPattern == null) 
			return false;
		
		new Thread(new Runnable() { 
			public void run() {
				/* Drawing ends */
				mIsDrawing = false;
				mIsDrawingLocked = false;
				
				/* Remove drawing line */
				mSceneRenderer.remove(mDrawingRender); // We don't want to draw it
				
				final int pointsCount = mDrawingPattern.size();
				
				if (pointsCount >= 3) { // if we had points enough
					mDrawingPattern.trimToSize();
					/* Optimize points by Douglas-Peucker algorithm */
					Vector2D[] optimizedPoints = PolygonUtils.douglasPeuckerReducer(
							mDrawingPattern.toArray(new Vector2D[0]), mCamera.getAperture().i / 80f);
					
					if (optimizedPoints.length >= 3) { // We have points enough
						Polygon drawedPolygon = new Polygon(optimizedPoints);
						drawedPolygon.setRandomColor();
						add(drawedPolygon);
						
						/* Vibrate as haptic feedback */
						mVibrator.vibrate(50);
					}
				}
				mDrawingPattern.clear();

				/* Good moment to call garbage collector */
				System.gc();
			} 
		}).start(); 

		return true;
	}
	
	public void onSingletouchCancel() {
		/* lock drawing until up event */
		mIsDrawingLocked = true;
		mIsDrawing = false;
		
		/* Remove actual drawing */
		mOnSceneBodies.remove(mDrawingRender);
		mDrawingRender = null;
		
		/* Good moment to call to Garbage Collector */
		System.gc();
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return super.onDown(e);
	}

	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		/* Drawing can be locked until Up event */
		if (mIsDrawingLocked || !mIsDrawing)
			return super.onScroll(downEv, ev, distanceX, distanceY);

		mDrawingPattern.add(mCamera.project(new Vector2D(ev.getX(), ev.getY())));
		return true;
	}
	
	

	@Override
	public void onShowPress(MotionEvent e) {
		super.onShowPress(e);
		
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent e) {
		if (!mDrawingPattern.isEmpty())
			mDrawingPattern.clear();
		
		/* Start drawing */
		mIsDrawing = true;
		mSceneRenderer.add(mDrawingRender); // Add body to be rendered
		/* Now we are ready to start drawing */
		mDrawingPattern.add(mCamera.project(new Vector2D(e.getX(), e.getY()))); // First point
		return true;
	}

}
