package edu.eside.flingbox.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class SceneRenderer implements Renderer {
	
	/**
	 * 
	 * @author endika
	 */
	public static class Camera {
		float mX, mY, mWidth, mHeight;
		
		public Camera(float x, float y, float width, float height) {
			mX = x;
			mY = y;
			mWidth = width;
			mHeight = height;
		}
		
		
	}
	
	private ArrayList<Renderizable> mGraphicsToRender;
	private Camera mCamera;
	
	/**
	 * Default Constructor
	 * @param graphicsToRender	Delegate with ArrayList of Renderizable
	 */
	public SceneRenderer(ArrayList<Renderizable> graphicsToRender, Camera camera) {
		mGraphicsToRender = graphicsToRender;
		mCamera = camera;
	}
	
	/**
	 * Sets camera's focus
	 */
	public void setCamera(Camera camera) {
		mCamera = camera;
	}

	/**
	 * Called to draw the current frame.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (mCamera != null) {
			// Set camera. 
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(mCamera.mX, mCamera.mX + mCamera.mWidth, 
					mCamera.mY + mCamera.mHeight, mCamera.mY, 0, 1);
			gl.glShadeModel(GL10.GL_FLAT);
			// gl.glFrustrumf(...); // We are working with orthogonal projection
			mCamera = null;
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glLoadIdentity();

		// Set background color
		gl.glClearColor(0.7f, 0.7f, 1.0f, 1.0f);
		
		// Render All objects
		for (Renderizable r : mGraphicsToRender) {
			gl.glPushMatrix();
			gl.glLoadIdentity();
			r.onRender(gl);
			gl.glPopMatrix();
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

	/**
	 * Called when the surface is resized and after onSurfaceCreated.
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { 
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(mCamera.mX, mCamera.mX + mCamera.mWidth, 
				mCamera.mY + mCamera.mHeight, mCamera.mY, 0, 1);
		gl.glShadeModel(GL10.GL_FLAT);


		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
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
