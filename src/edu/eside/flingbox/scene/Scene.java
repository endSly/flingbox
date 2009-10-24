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

import edu.eside.flingbox.ObjectSettingsDialog;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.Point;
import edu.eside.flingbox.objects.AtomicBody;

import android.content.Context;
import android.os.Environment;
import android.view.MotionEvent;


public class Scene extends DrawableScene implements OnInputListener {
	
	private AtomicBody mSelectedBody = null;

	/**
	 * Default constructor for an scene
	 * 
	 * @param c context
	 */
	public Scene(Context c) {
		super(c);
		
	}
	
	/**
	 * Starts physical simulation
	 * 
	 * @return true if simulation is started
	 */
	public boolean startSimulation() {
		if (mScenePhysics.isSimulating())
			return false;
		mScenePhysics.startSimulation();
		return true;
	}
	
	public boolean stopSimulation() {
		if (!mScenePhysics.isSimulating())
			return false;
		mScenePhysics.stopSimulation();
		return true;
	}
	
	public boolean isSimulating() {
		return mScenePhysics.isSimulating();
	}

	public boolean onFling(MotionEvent onDownEv, MotionEvent e, float velocityX,
			float velocityY) {
		boolean handled = false;
		if (mSelectedBody != null) {
			final float cameraScale = mCamera.getWidth() / mDisplayWidth;
			final float vx = (velocityX * cameraScale);
			final float vy = (velocityY * cameraScale);
			
			mSelectedBody.getPhysics().setVelocity(vx, vy);
			handled = true;
		}
			
		return handled;
	}
	
	public void onLongPress(MotionEvent e) {
		if (mSelectedBody != null) {
			mVibrator.vibrate(50); // vibrate as haptic feedback
			ObjectSettingsDialog dialog = new ObjectSettingsDialog(mContext);
			dialog.show();
			
			mSelectedBody.fixObject();
		}
		super.onLongPress(e);
		
	}

	public boolean onDown(MotionEvent e) {
		final float onDownX = mCamera.left + (e.getX() * mCamera.getWidth() / mDisplayWidth);
		final float onDownY = mCamera.top - (e.getY() * mCamera.getHeight() / mDisplayHeight);
		final Point p = new Point(onDownX, onDownY);
		
		for (AtomicBody object : mOnSceneBodys)
			if (object.getPhysics().contains(p)) {
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
	public boolean onScroll(MotionEvent downEv, MotionEvent e, float distanceX,
			float distanceY) {
		boolean handled = false;
		if (mSelectedBody != null) {
			final float onDownX = mCamera.left + (e.getX() * mCamera.getWidth() / mDisplayWidth);
			final float onDownY = mCamera.top - (e.getY() * mCamera.getHeight() / mDisplayHeight);
			
			mSelectedBody.getPhysics().setPosition(onDownX, onDownY);
			handled = true;
		}

		return handled ? true : super.onScroll(downEv, e, distanceX, distanceY);
		
	}
	
	public boolean onDragObject(MotionEvent e, AtomicBody o) {
		
		return false;
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		boolean handled = false;
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			handled |= startSimulation();
		}
			
		return handled ? true : super.onTrackballEvent(ev);
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
