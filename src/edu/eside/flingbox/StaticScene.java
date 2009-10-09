package edu.eside.flingbox;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

public abstract class StaticScene implements OnInputListener {
	
	protected class Camera {
		float x, y, width;
	}
	
	protected Camera mCamera;
	
	protected SceneRenderer mSceneRenderer;
	protected SceneGestureDetector mGestureDetector;
	
	protected ArrayList<Renderizable> mOnSceneBodys;
	
	public StaticScene(Context c) {
		
		mOnSceneBodys = new ArrayList<Renderizable>();
		
		mSceneRenderer = new SceneRenderer(mOnSceneBodys);
		
		mGestureDetector = new SceneGestureDetector(c, this);
		
		// TODO Fix this. this works because System.gc() generates some delay
		System.gc();
		
		mSceneRenderer.setCamera(160, 240, 320);
		
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
		mSceneRenderer.setCamera(ev.getX(), ev.getY(), 320);
		return true;
	}

	@Override
	public boolean onZoom(float x, float y, float scale) {
		mSceneRenderer.setCamera(x, y, 320);
		return true;
	}




}
