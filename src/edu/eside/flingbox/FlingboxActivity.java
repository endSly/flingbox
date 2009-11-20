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

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import edu.eside.flingbox.scene.Scene;

/**
 * Flingbox main activity.
 * Shows scene at full screen.
 */
public class FlingboxActivity extends Activity {
	// TODO Play and pause two menus separated
	private final static int MENU_PLAY_PAUSE = 0;
	private final static int MENU_PREFERENCES = 1;
	private final static int MENU_HELP = 2;
	
	private final static int MENU_NEW_SCENE = 10;
	private final static int MENU_LOAD_SCENE = 11;
	private final static int MENU_SAVE_SCENE = 12;
	
	
	private GLSurfaceView mSurface;
	private Scene mScene; 
	
    /** 
     * Called when the activity is first created. 
     */
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
     * Creates the menu items 
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu
        	.add(0, MENU_PLAY_PAUSE, 0, R.string.simulate);
        menu
        	.add(1, MENU_PREFERENCES, 1, R.string.preferences)
        	.setIcon(android.R.drawable.ic_menu_preferences);
        menu
    		.add(1, MENU_HELP, 3, R.string.help)
    		.setIcon(android.R.drawable.ic_menu_help);
        menu
    		.add(10, MENU_NEW_SCENE, 4, R.string.new_scene);
        menu
        	.add(10, MENU_LOAD_SCENE, 5, R.string.load_scene);
        menu
        	.add(10, MENU_SAVE_SCENE, 6, R.string.save_scene)
        	.setIcon(android.R.drawable.ic_menu_save);
        
        return true;
    }
    
    /**
     * Handles item selections 
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_PLAY_PAUSE:
        	if (mScene.isSimulating())
        		mScene.stopSimulation();
        	else
        		mScene.startSimulation();
            return true;
        case MENU_PREFERENCES:
            return true;
        case MENU_LOAD_SCENE:
        	return true;
        case MENU_SAVE_SCENE:
        	return true;
        }
        return false;
    }
    
    /**
     * Called when activity Pause
     */
    public void onPause() {
    	super.onPause();
    }
    
    /**
     * Called only wehn orientation changed.
     * This is called because the android:configChanges="orientation"
     * in AndroidManifest.xml
     */
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
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