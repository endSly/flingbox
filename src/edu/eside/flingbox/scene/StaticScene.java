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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import edu.eside.flingbox.Preferences;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.RenderCamera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.bodies.Body;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.ScenePhysics;
import edu.eside.flingbox.physics.gravity.GravitySource;

public class StaticScene implements OnInputListener {

	
	// TODO Support for all screen sizes
	protected float mDisplayWidth = 320f;
	protected float mDisplayHeight = 480f;
	
	// Vibrator instance
	protected Vibrator mVibrator;
	
	protected RenderCamera mCamera;

	protected final SceneRenderer mSceneRenderer;
	protected final ScenePhysics mScenePhysics;
	protected final SceneGestureDetector mGestureDetector;
	
	protected final ArrayList<Body> mOnSceneBodies;
	
	protected final Context mContext;
	
	public final static int SCENE_MODE_NONE = 0;
	
	private int mMode = SCENE_MODE_NONE;
	
	public StaticScene(Context c) {
		mContext = c;
		
		GravitySource gravity;
		if (Preferences.useAcelerometerBasedGravity)
			try {
				gravity = GravitySource.getAccelerometerBasedGravity(c);
			} catch (Exception ex) {
				/* We don't have accelerometers */
				gravity = GravitySource.getStaticGravity(0f, -SensorManager.GRAVITY_EARTH);
			}
		else 
			gravity = GravitySource.getStaticGravity(0f, -SensorManager.GRAVITY_EARTH);
		
		/* Create list of objects. */
		mOnSceneBodies = new ArrayList<Body>();
		mSceneRenderer = new SceneRenderer();
		mScenePhysics = new ScenePhysics(gravity);
		/*
		mSceneRenderer.add(new BackgroundRender(
				SCENE_LEFT_BORDER, SCENE_RIGHT_BORDER,
				SCENE_TOP_BORDER, SCENE_BOTTOM_BORDER));
		*/
		mGestureDetector = new SceneGestureDetector(c, this);
		
		mCamera = mSceneRenderer.getCamera();
		
		mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		
		if (c instanceof Activity) {
		    DisplayMetrics dm = new DisplayMetrics(); 
		    ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm); 
		    mDisplayHeight = dm.heightPixels; 
		    mDisplayWidth = dm.widthPixels; 
		}
		
		System.gc();	// This is a good moment to call to Garbage Collector.
	}
	
	public void setSceneMode(int mode) {
	    mMode = mode;
	}
	
	public int getSceneMode() {
        return mMode;
    }
	
	public void add(Body body) {
		mOnSceneBodies.add(body);
		mSceneRenderer.add(body.getRender());
		mScenePhysics.add(body.getPhysics());
	}
	
	public boolean remove(Body body) {
		boolean removed = mOnSceneBodies.remove(body);
		removed &= mSceneRenderer.remove(body.getRender());
		removed &= mScenePhysics.remove(body.getPhysics());
		return removed;
	}
	
	   /**
     * clear scene
     */
    public void clearScene() {
        ArrayList<Body> bodies = mOnSceneBodies;
        while(!bodies.isEmpty())
            remove(bodies.get(0));
    }
	
	public Renderer getSceneRenderer() {
		return mSceneRenderer;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}


	@Override
	public boolean onUp(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

    @Override
    public boolean onMultitouchScroll(MotionEvent ev1, MotionEvent ev2,
            float dX, float dY) {
        final Vector2D distance = mCamera.scale(new Vector2D(dX, dY));
        mCamera.setPosition(mCamera.getPosition().add(distance));
        return true;
    }

    @Override
    public boolean onMultitouchZoom(MotionEvent ev1, MotionEvent ev2,
            float scale) {
        mCamera.setAperture(mCamera.getAperture().mul(scale));
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float dX,
            float dY) {
        final Vector2D distance = mCamera.scale(new Vector2D(dX, dY));
        mCamera.setPosition(mCamera.getPosition().add(distance));
        return true;
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return false;
    }

}
