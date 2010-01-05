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

import org.xmlpull.v1.XmlSerializer;

import edu.eside.flingbox.BodySettingsDialog;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.XmlExporter.XmlSerializable;
import edu.eside.flingbox.bodies.Body;

import android.content.Context;
import android.os.Environment;
import android.view.MotionEvent;

/**
 * Scene Descriptor.
 *
 */
public class Scene extends DrawableScene implements OnInputListener, XmlSerializable {
	
	private Body mSelectedBody = null;

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
			final float cameraScale = mCamera.getAperture().i / mDisplayWidth;
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
			BodySettingsDialog dialog = new BodySettingsDialog(mContext, mSelectedBody, this);
			dialog.show();
		}
		super.onLongPress(e);
	}

	public boolean onDown(MotionEvent e) {
		final float onDownX = mCamera.left + (e.getX() * mCamera.getAperture().i / mDisplayWidth);
		final float onDownY = mCamera.top - (e.getY() * mCamera.getAperture().j / mDisplayHeight);
		final Vector2D p = new Vector2D(onDownX, onDownY);
		
		/* Check selected body */
		for (Body object : mOnSceneBodies)
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
		if (mSelectedBody == null) 
			return super.onScroll(downEv, e, distanceX, distanceY);
		
		PhysicBody selectedPhysics = mSelectedBody.getPhysics();
		Vector2D touchPosition = mCamera.project(new Vector2D(e.getX(), e.getY()));
		if (mScenePhysics.isSimulating()) {
			Vector2D downPosition = mCamera.project(new Vector2D(downEv.getX(), downEv.getY()));
			/* Apply force to the position */
			Vector2D movementImpulse = touchPosition.sub(downPosition).mul(selectedPhysics.getBodyMass());
			
			selectedPhysics.applyImpulse(movementImpulse);
		} else 
			/* Just move the body */
			selectedPhysics.setPosition(touchPosition);

		return true;

	}
	
	public boolean onDragBody(MotionEvent e, Body b) {
		
		return false;
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		boolean handled = false;
		if (ev.getAction() == MotionEvent.ACTION_DOWN) 
			if (mScenePhysics.isSimulating())
				handled |= stopSimulation();
			else
				handled |= startSimulation();
			
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
	

	@Override
	public boolean writeXml(XmlSerializer serializer) {
		
		return false;
	}
	
}
