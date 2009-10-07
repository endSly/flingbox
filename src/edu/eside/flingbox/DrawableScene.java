package edu.eside.flingbox;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

public abstract class DrawableScene extends StaticScene implements OnInputListener {
	
	private ArrayList<PointF> mDrawingPattern;
	private boolean mIsDrawing;
	
	public DrawableScene(Context c) {
		super(c);
		mDrawingPattern = new ArrayList<PointF>();
		mIsDrawing = false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		if (!mIsDrawing) {
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
