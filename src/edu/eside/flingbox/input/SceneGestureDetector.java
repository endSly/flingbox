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
	private OnInputListener mListener;
	
	/** Last Size of MotionEvent, used for  */
	private float mLastSize = 0.0f;
	private float mLastX = 0f;
	private float mLastY = 0f;
	private MotionEvent mLastDownEvent;
	 // Flag to skip one-touch events after Multitouch event.
	private boolean mIsMultitouchEvent = false;
	
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

	/**
	 * TODO Improve this function!!!
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float size = ev.getSize();
        final float y = ev.getY();
        final float x = ev.getX();

        boolean handled = false;
        
        if (size > 0.0f)	// Set multitouch flag
        	mIsMultitouchEvent = true;
        
        if (size > 0.0f && mLastSize > 0.0f) {
        	final float scale = mLastSize / size;
        	if (scale > 0.625f && scale < 1.6f)	// Skip big scales. Should be an error
        		handled |= mListener.onZoom(x, y, scale);
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN : 
        	mLastDownEvent = ev;
        	break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
        	handled |= mListener.onUp(ev);
        	mLastSize = 0.0f;
        	mIsMultitouchEvent = false;
        	break;
        
        case MotionEvent.ACTION_MOVE :
        	// We are locking for multitouch Scroll Events
        	if (size > 0.0f && mLastSize > 0.0f) {
        		handled |= mListener.onMultitouchScroll(mLastDownEvent, ev, x - mLastX, y - mLastY);
        		
        	} else if (mIsMultitouchEvent ||  (size > 0.0f && mLastSize == 0.0f))
        		handled = true; // Skip single touch after a multitouch event
        	break;
        }
        
        mLastX = x;
		mLastY = y;
        mLastSize = size;
        
        return handled ? true : super.onTouchEvent(ev);
	}
	
}
