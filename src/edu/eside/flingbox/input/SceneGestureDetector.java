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
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector;

/**
 * Custom gesture detection class
 * 
 * Listener should implement {@link OnInputListener}.
 */
public class SceneGestureDetector extends GestureDetector {
	
	public interface OnInputListener extends OnGestureListener {
		/**
		 * Called when user stops scrolling
		 * @param ev
		 * @return
		 */
		public boolean onUp(MotionEvent ev);
		
		public boolean onMultitouchZoom(MotionEvent ev1, MotionEvent ev2, float scale);
		
		public boolean onMultitouchScroll(MotionEvent ev1, MotionEvent ev2, float dX, float dY);
	}
	
	/** Handler of processed gesture events */
	private final OnInputListener mListener;
	
	private MotionEvent mLastMotionEvent;

	/**
	 * @param context
	 * @param listener
	 */
	public SceneGestureDetector(Context context, OnInputListener listener) {
		super(context, listener);
		mListener = listener;
	}

	/**
	 * Should be called on touch event
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		boolean handled = false;
		
		// Check for an Up event
		if (ev.getAction() == MotionEvent.ACTION_UP)
			handled |= mListener.onUp(ev);
		
		// Check for multitouch event
		if (ev.getPointerCount() > 1)
		    handled |= this.onMultitouchEvent(ev);
		
		mLastMotionEvent = ev;
        return handled ? true : super.onTouchEvent(ev);
	}
	
	public boolean onMultitouchEvent(MotionEvent ev) {
	    if (ev.getPointerCount() < 2)
	        return false;
	    
	    switch (ev.getAction()) {
	    case MotionEvent.ACTION_MOVE:
	        if (mLastMotionEvent == null || mLastMotionEvent.getPointerCount() < 2)
	            break;
	        final float lastX0 = ev.getHistoricalX(0, 1), lastX1 = ev.getHistoricalX(1, 1),
	                lastY0 = ev.getHistoricalY(0, 1), lastY1 = ev.getHistoricalY(1, 1);
	        final float x0 = ev.getX(0), x1 = ev.getX(1), 
	                y0 = ev.getY(0), y1 = ev.getY(1);
	        
	        if (lastX0 == 0 || lastX1 == 0 || lastY0 == 0 || lastY1 == 0
	                || x0 == 0 || x1 == 0 || y0 == 0 || y1 == 0)
	            return false;
	        
	        
	        final float dx = -((x0 - lastX0) + (x1 - lastX1)) / 2;
	        final float dy = -((y0 - lastY0) + (y1 - lastY1)) / 2;
	        
	        if (dx > -40 && dx < 40 && dy > -40 && dy < 40)
	        mListener.onMultitouchScroll(mLastMotionEvent, ev, dx, dy);
	       
	        final float scale = (float) Math.sqrt(
                    ((lastX1 - lastX0) * (lastX1 - lastX0) + (lastY1 - lastY0) * (lastY1 - lastY0))
                    / ((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))
                    );
	        
	        if (scale < 1.5 && scale > 0.6667)
	            mListener.onMultitouchZoom(mLastMotionEvent, ev, scale);
	        return true;
	    }
	    return false;
	}
	
}
