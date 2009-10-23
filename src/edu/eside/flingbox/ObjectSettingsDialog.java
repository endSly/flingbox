package edu.eside.flingbox;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class ObjectSettingsDialog extends Dialog {

	public ObjectSettingsDialog(Context context) {
		super(context);
	}
	
    /** 
     * Called when the dialog is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// Set dialog content
    	setContentView(R.layout.object_settings);
    	
    	// Sets dialog title
		setTitle(R.string.obj_settings_name);
    }

}
