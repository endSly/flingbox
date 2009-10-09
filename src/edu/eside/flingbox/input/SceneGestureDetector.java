package edu.eside.flingbox.input;

import android.content.Context;
import android.view.MotionEvent;
import android.view.GestureDetector;

/**
 * SceneGestureListener is an abstract class which performs basic scroll and zoom Operations.
 * Zoom and scroll will be interpreted on 2-fingers events
 * @author endika
 *
 */
public class SceneGestureDetector extends GestureDetector {
	
	public interface OnInputListener extends OnGestureListener {
		/**
		 * Called when user uses multi-touch zoom
		 * @param scale	Scale of zoom
		 * @return		true if the event is consumed, else false
		 */
		public boolean onZoom(float x, float y, float scale);
		
		/**
		 * Called when user stops scrolling
		 * @param ev
		 * @return
		 */
		public boolean onUp(MotionEvent ev);
		
		/**
		 * Called when user scrolls with 2-fingers
		 * @param ev 
		 * @param mLastDownEvent 
		 * @param dX	Differential on X axis
		 * @param dY	Differential on Y axis
		 * @return		true if the event is consumed, else false
		 */
		public boolean onMultitouchScroll(MotionEvent downEvent, MotionEvent ev, float dX, float dY);
		
	}
	
	/** Handler of processed gesture events */
	private OnInputListener mListener = null;
	
	/**
	 * 
	 * @param context
	 * @param listener
	 */
	public SceneGestureDetector(Context context, OnInputListener listener) {
		super(context, listener);
		
		if (listener instanceof OnInputListener) 
			mListener = (OnInputListener) listener;
		
	}
	
	/** Last Size of MotionEvent, used for  */
	private float mLastSize;
	private float mLastX;
	private float mLastY;
	private MotionEvent mLastDownEvent;

	/**
	 * 
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float size = ev.getSize();
        final float y = ev.getY();
        final float x = ev.getX();

        boolean handled = false;
        
        if (size > 0.0f && mLastSize > 0.0f)
        	handled |= mListener.onZoom(x, y, size / mLastSize);


    	if (size > 0.0f && mLastSize > 0.0f) {
    		handled |= mListener.onMultitouchScroll(mLastDownEvent, ev, x - mLastX, y - mLastY);
    		mLastX = x;
    		mLastY = y;
    	}
    	
        switch (action) {
        case MotionEvent.ACTION_DOWN : 
        	mLastDownEvent = ev;
        	break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
        	mListener.onUp(ev);
        	mLastSize = 0.0f;
        	break;
        
        case MotionEvent.ACTION_MOVE :
        	// We are locking for multi-touch Scroll Events
        	if (size > 0.0f && mLastSize > 0.0f) {
        		handled |= mListener.onMultitouchScroll(mLastDownEvent, ev, x - mLastX, y - mLastY);
        		mLastX = x;
        		mLastY = y;
        	}
        	break;
        }
        
        mLastSize = size;
        
        return handled ? true : super.onTouchEvent(ev);
	}
	
}
