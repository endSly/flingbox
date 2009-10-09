package edu.eside.flingbox.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class SceneRenderer implements Renderer {
	
	public class Camera {
		// This will be used by OpenGL
		public float left, rigth, top, bottom;
		boolean isChanged;	// Flag
		
		// This will store camera position;
		private float mX = 0f, mY = 0f, mWidth = 100f, mHeight = 100f;
		private int mSurfaceWidth = 100, mSurfaceHeight = 100;
		
		public Camera() {
			updateGLCamera();
		}
		
		public void setSurfaceSize(int surfaceWidth, int surfaceHeight) {
			mSurfaceWidth = surfaceWidth;
			mSurfaceHeight = surfaceHeight;
			
			mHeight = mWidth * surfaceHeight / surfaceWidth;
			
			updateGLCamera();
		}
		
		public void setPosition(float x, float y, final float width) {
			mX = x;
			mY = y;
			mWidth = width;
			mHeight = width * mSurfaceHeight / mSurfaceWidth;
			
			updateGLCamera();
		}
	
		private void updateGLCamera() {
			this.left = mX - mWidth / 2;
			this.rigth = mX + mWidth / 2;
			this.bottom = mY - mHeight / 2;
			this.top = mY + mHeight / 2;
			
			this.isChanged = true;
		}
		
		public float getX() {
			return mX;
		}
		
		public float getY() {
			return mY;
		}
		
		public float getWidth() {
			return mWidth;
		}
		
		public float getHeight() {
			return mHeight;
		}
	}
	
	private ArrayList<Renderizable> mGraphicsToRender;
	
	private Camera mCamera;
	
	/**
	 * Default Constructor
	 * @param graphicsToRender	Delegate with ArrayList of Renderizable
	 */
	public SceneRenderer(ArrayList<Renderizable> graphicsToRender) {
		mGraphicsToRender = graphicsToRender;
		mCamera = new Camera();
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	/**
	 * Called to draw the current frame.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (mCamera.isChanged) {
			// Set camera. 
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(mCamera.left, mCamera.rigth, mCamera.bottom, mCamera.top, 0, 1);
			gl.glShadeModel(GL10.GL_FLAT);
			// gl.glFrustrumf(...); // We are working with orthogonal projection
			mCamera.isChanged = false;
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
