package edu.eside.flingbox;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(mSurface);
    }
    
    public boolean onTouchEvent(MotionEvent ev) {
    	return mScene.onTouchEvent(ev);
    }
}