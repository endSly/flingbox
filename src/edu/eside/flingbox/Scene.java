package edu.eside.flingbox;

import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

import android.content.Context;
import android.view.MotionEvent;


public class Scene extends DrawableScene implements OnInputListener {

	public Scene(Context c) {
		super(c);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
