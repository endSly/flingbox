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

package edu.eside.flingbox.scene;

import java.io.File;
import java.io.IOException;

import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.objects.AtomicBody;

import android.content.Context;
import android.os.Environment;
import android.view.MotionEvent;


public class Scene extends DrawableScene implements OnInputListener {
	
	private AtomicBody mSelectedBody = null;

	public Scene(Context c) {
		super(c);
	}

	public boolean onFling(MotionEvent onDownEv, MotionEvent ev, float velocityX,
			float velocityY) {
		/** TEST FOR SIMULATOR */
		if (mScenePhysics.isSimulating())
			mScenePhysics.stopSimulation();
		else 
			mScenePhysics.startSimulation();
		return false;
	}
	
	public void onLongPress(MotionEvent e) {
		if (mSelectedBody != null) {
			mSelectedBody.fixObject();
			// vibrate as haptic feedback
			mVibrator.vibrate(60);
		}
		super.onLongPress(e);
		
	}

	public boolean onDown(MotionEvent e) {
		final float onDownX = mCamera.left + (e.getX() * mCamera.getWidth() / mDisplayWidth);
		final float onDownY = mCamera.top - (e.getY() * mCamera.getHeight() / mDisplayHeight);
		final Point p = new Point(onDownX, onDownY);
		
		for (AtomicBody object : mOnSceneBodys)
			if (object.isPointOver(p)) {
				mSelectedBody = object;
				return true;
			}
		
		return super.onDown(e);
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		mSelectedBody = null;
		return super.onUp(e);
	}
	
	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		if (mSelectedBody != null) {
			
		}
		
		return super.onScroll(downEv, ev, distanceX, distanceY);
		
	}
	
	public boolean onDragObject(MotionEvent e, AtomicBody o) {
		
		return false;
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		boolean handled = false;
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (mScenePhysics.isSimulating())
				mScenePhysics.stopSimulation();
			else 
				mScenePhysics.startSimulation();
			handled = true;
		}
			
		return handled | super.onTrackballEvent(ev);
	}
	
	
	public boolean onSaveScene() {
		File savedFile = new File(Environment.getExternalStorageDirectory() 
				+ "flingbox/saved.xml");
		try{
			savedFile.createNewFile(); 
		} catch (IOException ex) {
				
		}
		
		return false;
	}
	
}
