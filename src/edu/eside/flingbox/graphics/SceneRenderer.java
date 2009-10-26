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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

/**
 * {@link SceneRenderer} handles functions to render 
 * {@link Scene} into {@link GLSurfaceView} space by 
 * {@link Renderer} interface.
 *
 * Defines {@link Renderizable} interface witch should be 
 * implemented by on scene bodys to be rendered.
 */
public class SceneRenderer implements Renderer {
	
	/**
	 * Specifies OpenGL camera interface.
	 * By setting camera's position and width camera could 
	 * be moved.
	 */
	public class Camera {
		// This will be used by OpenGL
		public float left, rigth, top, bottom;
		boolean isChanged;	// Flag
		
		// This will store camera position;
		private float mX = 0f, mY = 0f, mWidth = 256f, mHeight = 256f;
		private int mSurfaceWidth = 100, mSurfaceHeight = 100;
		
		/**
		 * Default constructor
		 */
		Camera() {
			updateGLCamera();
		}
		
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
	
		/**
		 * Sets coordinates needed by OpenGL from camera's position
		 */
		private void updateGLCamera() {
			final float halfWidth = mWidth / 2;
			final float halfHeight = mHeight / 2;
			
			this.left = mX - halfWidth;
			this.rigth = mX + halfWidth;
			this.bottom = mY - halfHeight;
			this.top = mY + halfHeight;
			
			this.isChanged = true;
		}
		
		/**
		 * @return	x of camera's position
		 */
		public float getX() {
			return mX;
		}
		
		/**
		 * @return	y of camera's position
		 */
		public float getY() {
			return mY;
		}
		
		/**
		 * @return	width of camera's frame
		 */
		public float getWidth() {
			return mWidth;
		}
		
		/**
		 * @return	height of camera's frame
		 */
		public float getHeight() {
			return mHeight;
		}
	}
	
	// Stores objects that will be renderized
	private final ArrayList<Render> mGraphicsToRender;
	
	// Stores camera for this scene
	private final Camera mCamera;
	
	/**
	 * Default Constructor
	 * Creates an Render Scene without any object
	 */
	public SceneRenderer() {
		mGraphicsToRender = new ArrayList<Render>();
		mCamera = new Camera();
	}
	
	/**
	 * Default Constructor
	 * Creates an Render Scene with one object.
	 * 
	 * @param render Object to be rendered
	 */
	public SceneRenderer(Render render) {
		mGraphicsToRender = new ArrayList<Render>();
		mCamera = new Camera();
		mGraphicsToRender.add(render);
	}
	
	/**
	 * Default Constructor
	 * Creates an Render Scene with one object.
	 * 
	 * @param renders Objects to be rendered
	 */
	public SceneRenderer(Render[] renders) {
		mGraphicsToRender = new ArrayList<Render>();
		mCamera = new Camera();
		for (Render r : renders)
			mGraphicsToRender.add(r);
	}

	/**
	 * Adds one object to be rendered.
	 * 
	 * @param render object
	 */
	public void add(Render render) {
		mGraphicsToRender.add(render);
	}
	
	/**
	 * Adds an array of renderizable objects to scene.
	 * 
	 * @param renders array of objects
	 */
	public void add(Render[] renders) {
		for (Render r : renders)
			mGraphicsToRender.add(r);
	}
	
	/**
	 * Removes object from scene
	 * 
	 * @param render Render to be removed
	 * @return true if removed, else false
	 */
	public boolean remove(Render render) {
		return mGraphicsToRender.remove(render);
	}
	
	/**
	 * @return	Camera for current scene
	 */
	public Camera getCamera() {
		return mCamera;
	}
	
	/**
	 * Called to draw the current frame.
	 */
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		try{
			/*  Due multi-threaded design of GL10 it is very possible
			 *	to throw an {@link ConcurrentModificationException}.
			 */
			if (mCamera.isChanged) {
				// Set camera. 
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				gl.glOrthof(mCamera.left, mCamera.rigth, mCamera.bottom, mCamera.top, 0, 1);
				gl.glShadeModel(GL10.GL_FLAT);
				// gl.glFrustrumf(...); // We are working with orthogonal projection
				mCamera.isChanged = false;
			}
			
			// Set up OpenGL's Scene
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glLoadIdentity();

			// Set background color
			gl.glClearColor(0.4f, 0.4f, 0.8f, 1.0f);

			// Render All objectsCount = 
			final ArrayList<Render> renders = mGraphicsToRender;
			for (Render r : renders) {
				// Work with new stacked matrix
				gl.glPushMatrix();
				gl.glLoadIdentity();
				r.onRender(gl);
				gl.glPopMatrix();
			}
			// End drawing
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		} catch (ConcurrentModificationException ex) {
			/* Do Nothing.
			 * Just skip drawing until next frame.
			 * This will origin image blink.
			 * TODO Add semaphore
			 */
		}
		
	}

	/**
	 * Called when the surface is resized and after onSurfaceCreated.
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { 
		// Set surface size to camera
		mCamera.setSurfaceSize(width, height);
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glOrthof(mCameraLeft, mCameraRigth, mCameraBottom, mCameraTop, 0, 1);
		gl.glShadeModel(GL10.GL_FLAT);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * First called when Surface is Created.
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Disable some features that we won't need in 2D,
		// Just for better performance.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_TEXTURE_2D);	// We won't need textures

		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

	}
	
}
