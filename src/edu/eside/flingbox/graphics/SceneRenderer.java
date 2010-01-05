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
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.eside.flingbox.Preferences;

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
	/** Stores objects that will be renderized */
	private final ArrayList<RenderBody> mGraphicsToRender = new ArrayList<RenderBody>();
	/** Manages mGraphicsToRender access */
	private final Semaphore mGraphicsToRenderMutex = new Semaphore(1, true);
	
	/** Camera for this scene */
	private RenderCamera mCamera = new RenderCamera(100, 100);
	
	/**
	 * Adds one object to be rendered.
	 * 
	 * @param render object
	 */
	public void add(RenderBody render) {
		try { // Wait until everything rendered
			mGraphicsToRenderMutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mGraphicsToRender.add(render);
		mGraphicsToRenderMutex.release();
	}
	
	/**
	 * Removes object from scene
	 * 
	 * @param render Render to be removed
	 * @return true if removed, else false
	 */
	public boolean remove(RenderBody render) {
		try { // Wait until everything rendered
			mGraphicsToRenderMutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean succeed = mGraphicsToRender.remove(render);
		mGraphicsToRenderMutex.release();
		return succeed;
	}
	
	/**
	 * @return	Camera for current scene
	 */
	public RenderCamera getCamera() {
		return mCamera;
	}
	
	/**
	 * Called to draw the current frame.
	 */
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		if (mCamera.isChanged) {
			/* Set camera. */
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(mCamera.left, mCamera.rigth, mCamera.bottom, mCamera.top, 0, 1);
			gl.glShadeModel(GL10.GL_FLAT);
			// gl.glFrustrumf(...); // We are working with orthogonal projection
			mCamera.isChanged = false;
		}
			
		/* Set up OpenGL's Scene */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glLoadIdentity();

		/* Set background color */
		gl.glClearColor(Preferences.backgroundColor[0], Preferences.backgroundColor[1], 
				Preferences.backgroundColor[2], 1.0f);

		/* Render All objectsCount */
		final ArrayList<RenderBody> renders = mGraphicsToRender;
			
		try {
			mGraphicsToRenderMutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (RenderBody r : renders) {
			/* Work with new stacked matrix */
			gl.glPushMatrix();
			gl.glLoadIdentity();
			r.onRender(gl);
			gl.glPopMatrix();
		}
		mGraphicsToRenderMutex.release();
		/* End drawing */
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	/**
	 * Called when the surface is resized and after onSurfaceCreated.
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { 
		/* Create camera for current surface */
		mCamera.setSurface(width, height);
		
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
