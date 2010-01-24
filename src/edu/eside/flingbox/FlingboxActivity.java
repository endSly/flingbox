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
import android.app.Dialog;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import edu.eside.flingbox.scene.Scene;

/**
 * Flingbox main activity.
 * Shows scene at full screen.
 */
public class FlingboxActivity extends Activity {
	private final static String KEY_FIRST_BOOT_DONE = "FIRST_BOOT_DONE";
	
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
        /* Request full screen view */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mScene = new Scene(this);
        
        mSurface = new GLSurfaceView(this);
        mSurface.setRenderer(mScene.getSceneRenderer());
        
        // Set OpenGL's surface
        setContentView(mSurface);
        
        showHelp();
    }
    
    /**
     * Creates the menu items 
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu
        	.add(0, MENU_PLAY_PAUSE, 0, R.string.simulate)
        	.setIcon(this.getResources().getDrawable(R.drawable.ic_menu_flash));
        menu
        	.add(1, MENU_PREFERENCES, 1, R.string.preferences)
        	.setIcon(android.R.drawable.ic_menu_preferences);
        menu
    		.add(1, MENU_HELP, 3, R.string.help)
    		.setIcon(android.R.drawable.ic_menu_help);
        menu
    		.add(10, MENU_NEW_SCENE, 4, R.string.new_scene)
    		.setIcon(this.getResources().getDrawable(R.drawable.ic_menu_globe));
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
        case MENU_HELP:
        	showHelp();
        	return true;
        case MENU_NEW_SCENE:
        	mSurface.onPause();
        	mScene.stopSimulation();
        	mScene.clearScene();
        	System.gc();
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
    @Override
    public void onPause() {
    	super.onPause();
    	mScene.stopSimulation();
    	mSurface.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mSurface.onResume();
    }
    
    /**
     * Called when activity Stops
     */
    public void onStop() {
    	super.onStop();
    	mScene.stopSimulation();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	showHelp();
    	if (savedInstanceState == null)
        	showHelp();
    	else if (!savedInstanceState.containsKey(KEY_FIRST_BOOT_DONE)) 
    		showHelp();
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	Preferences.onSavePreferences(outState);
    }
    
    /**
     * Called only when orientation changed.
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
    
    /**
     * Show help dialog
     */
    private void showHelp() {
    	final Dialog helpDialog = new Dialog(this);
    	helpDialog.setTitle(R.string.help);
    	helpDialog.setContentView(R.layout.help);
    	helpDialog.show();
    	
    	Button aboutButton = (Button) helpDialog.findViewById(R.id.about_button);
    	if (aboutButton == null)
    		return;
    	
    	aboutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				helpDialog.dismiss();
				showAboutDialog();
			}
    	});
    	
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
    	Dialog aboutDialog = new Dialog(this);
    	aboutDialog.setTitle(R.string.help_about);
    	aboutDialog.setContentView(R.layout.about_dialog);
    	aboutDialog.show();
    }
    
    
}