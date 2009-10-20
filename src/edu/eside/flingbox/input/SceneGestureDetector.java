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
 * SceneGestureDetector class  extends {@link GestureDetector}
 * with basic multitouch scroll and zoom Operations.
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
	}
	
	/** Handler of processed gesture events */
	private OnInputListener mListener;

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
	 * Should be called on touch event
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		boolean handled = false;
		if (ev.getAction() == MotionEvent.ACTION_UP)
			handled |= mListener.onUp(ev);
        return handled ? true : super.onTouchEvent(ev);
	}
	
}
