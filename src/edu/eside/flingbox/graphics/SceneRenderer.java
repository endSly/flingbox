package edu.eside.flingbox.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class SceneRenderer implements Renderer {
	
	private ArrayList<Renderizable> mGraphicsToRender;
	
	private int mSurfaceWidth = 100, mSurfaceHeight = 100;
	
	private float mCameraX, mCameraY;
	private float mCameraWidth, mCameraHeight;
	private boolean mIsCameraChanged = false;
	
	/**
	 * Default Constructor
	 * @param graphicsToRender	Delegate with ArrayList of Renderizable
	 */
	public SceneRenderer(ArrayList<Renderizable> graphicsToRender) {
		mGraphicsToRender = graphicsToRender;
	}
	
	public void setCamera(float x, float y, float width, float height) {
		mCameraX = x;
		mCameraY = y;
		mCameraWidth = width;
		mCameraHeight = height; 
		
		mIsCameraChanged = true;
	}
	
	public void setCamera(float x, float y, float width) {
		mCameraX = x;
		mCameraY = y;
		mCameraWidth = width;
		mCameraHeight = -1.0f; 
		
		mIsCameraChanged = true;
	}
	
	/**
	 * Called to draw the current frame.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (mIsCameraChanged) {
			// Keep aspect radio
			final float height = mCameraHeight < 0.0f 
				? mCameraWidth * mSurfaceHeight / mSurfaceWidth 
				: mCameraHeight;
			
			final float left = mCameraX - (mCameraWidth / 2);
			final float rigth = mCameraX + (mCameraWidth / 2);
			final float top = mCameraY + (height / 2);
			final float bottom = mCameraY - (height / 2);
			
			// Set camera. 
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(left, rigth, bottom, top, 0, 1);
			gl.glShadeModel(GL10.GL_FLAT);
			// gl.glFrustrumf(...); // We are working with orthogonal projection
			mIsCameraChanged = false;
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
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glOrthof(mCameraLeft, mCameraRigth, mCameraBottom, mCameraTop, 0, 1);
		gl.glShadeModel(GL10.GL_FLAT);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		mIsCameraChanged = true;
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
