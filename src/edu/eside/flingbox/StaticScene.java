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

package edu.eside.flingbox;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.SceneRenderer.Camera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

public abstract class StaticScene implements OnInputListener {
	
	protected Camera mCamera;
	
	protected SceneRenderer mSceneRenderer;
	protected SceneGestureDetector mGestureDetector;
	
	protected ArrayList<Renderizable> mOnSceneBodys;
	
	public StaticScene(Context c) {
		
		mOnSceneBodys = new ArrayList<Renderizable>();
		
		mSceneRenderer = new SceneRenderer(mOnSceneBodys);
		
		mGestureDetector = new SceneGestureDetector(c, this);
		
		mCamera = mSceneRenderer.getCamera();
		
		System.gc();	// This is a good moment to call to Garbage Collector.
	}
	
	public Renderer getSceneRenderer() {
		return mSceneRenderer;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean onMultitouchScroll(MotionEvent downEvent, MotionEvent ev,
			float dX, float dY) {
		// TODO dX and dY should be fit into the openGL space
		mCamera.setPosition(mCamera.getX() + dX, mCamera.getY() + dY, 
				mCamera.getWidth());
		return true;
	}

	@Override
	public boolean onZoom(float x, float y, float scale) {
		mCamera.setPosition(mCamera.getX(), mCamera.getY(), 
				mCamera.getWidth() * scale);
		return true;
	}




}
