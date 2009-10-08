package edu.eside.flingbox;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.objects.Polygon;

public abstract class DrawableScene extends StaticScene implements OnInputListener {
	
	private ArrayList<PointF> mDrawingPattern;
	private boolean mIsDrawing;
	private boolean mIsDrawingLocked;
	
	public DrawableScene(Context c) {
		super(c);
		mIsDrawing = false;
		mIsDrawingLocked = false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onUp(MotionEvent ev) {
		if (mIsDrawing) {
			// Drawing ends
			mIsDrawing = false;
			mIsDrawingLocked = false;
			
			final int pointsCount = mDrawingPattern.size();
			// if we had points enough
			if (pointsCount >= 3) {
				float[] points = new float[pointsCount * 2];
				for (int i = 0; i < pointsCount; i++) {
					points[2 * i] = mDrawingPattern.get(i).x;
					points[2 * i + 1] = mDrawingPattern.get(i).y;
				}
				Polygon drawedPolygon = new Polygon(points);
				drawedPolygon.setRandomColor();
				mOnSceneBodys.add(drawedPolygon);
			}
			mDrawingPattern = null;
			
			// Good moment to call garbage collector
			System.gc();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		// Drawing can be locked until Up event
		if (mIsDrawingLocked)
			return false;
		
		if (!mIsDrawing) {
			mDrawingPattern = new ArrayList<PointF>();
			mDrawingPattern.add(new PointF(downEv.getX(), downEv.getY()));
			mIsDrawing = true;
		}
		
		mDrawingPattern.add(new PointF(ev.getX(), ev.getY()));
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
