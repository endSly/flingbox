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

import edu.eside.flingbox.objects.AtomicBody;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class ObjectSettingsDialog extends Dialog implements OnClickListener {

	private Context mContext;
	
	protected AtomicBody mBody;
	
	private CheckBox mLocked;

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
    	setContentView(R.layout.body_settings);

    	// Sets dialog title
		setTitle(R.string.body_settings_name);
		
		mLocked = (CheckBox)findViewById(R.id.checkbox_lock_body);
		mLocked.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {

		
	}

}
