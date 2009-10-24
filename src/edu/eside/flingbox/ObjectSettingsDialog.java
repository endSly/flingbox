package edu.eside.flingbox;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class ObjectSettingsDialog extends Dialog implements OnClickListener {
	
	ListView mList;
	
	Context mContext;

	public ObjectSettingsDialog(Context context) {
		super(context);
		mContext = context;
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
		
		mList = (ListView) findViewById(R.id.settings_list);
		
		//mList.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		
		
	}

}
