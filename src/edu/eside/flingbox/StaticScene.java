package edu.eside.flingbox;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.objects.Polygon;

public abstract class StaticScene implements OnInputListener {
	protected SceneRenderer mSceneRenderer;
	protected SceneGestureDetector mGestureDetector;
	
	protected ArrayList<Renderizable> mOnSceneBodys;
	
	public StaticScene(Context c) {
		
		mOnSceneBodys = new ArrayList<Renderizable>();
		
		mSceneRenderer = new SceneRenderer(mOnSceneBodys, 
				new SceneRenderer.Camera(-160, -240, 320, 480));
		
		mGestureDetector = new SceneGestureDetector(c, this);
		
		/**  ***** TESTS */
		float[] points1  = {
				-64f, -64f,
				-8f, -64f,
				-8f, -32f,
				0, -32f,
				8f, -32f,
				8f, -64f,
				64f, -64f,
				64f, 64f, 
				0f, 96f,
				-64f, 64f
		};
		float[] points2  = {
				-80f, 0f,
				-160f, 0f,
				-120f, 80f
		};
		
		Polygon p1 = new Polygon(points1);
		Polygon p2 = new Polygon(points2);
		mOnSceneBodys.add((Renderizable)p1);
		mOnSceneBodys.add((Renderizable)p2);

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
		mSceneRenderer.setCamera(new SceneRenderer
				.Camera(-ev.getX(), -ev.getY(), 320, 480));
		return true;
	}

	@Override
	public boolean onZoom(float x, float y, float scale) {
		mSceneRenderer.setCamera(new SceneRenderer
				.Camera(-x, -y, 320 / scale, 480 / scale));
		return true;
	}




}
