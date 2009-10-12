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

import edu.eside.flingbox.scene.Scene;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Flingbox main activity.
 * Shows scene at full screen.
 */
public class FlingboxActivity extends Activity {
	private GLSurfaceView mSurface;
	private Scene mScene; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene = new Scene(this);
        
        mSurface = new GLSurfaceView(this);
        mSurface.setRenderer(mScene.getSceneRenderer());

        // Request full screen view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set OpenGL's surface
        setContentView(mSurface);
    }
    
    /** 
     * Called when touch event occurs. 
     */
    public boolean onTouchEvent(MotionEvent ev) {
    	return mScene.onTouchEvent(ev);
    }
    
    /**
     * Called when trackball scroll event occurs
     */
    public boolean onTrackballEvent(MotionEvent ev) {
    	return mScene.onTrackballEvent(ev);
    }
    
    
}