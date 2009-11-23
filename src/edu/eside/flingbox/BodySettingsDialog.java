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

import edu.eside.flingbox.objects.Body;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BodySettingsDialog extends Dialog {

	//private Context mContext;
	
	/** Contains Body to change properties */
	protected Body mBody;

	public BodySettingsDialog(Context context, Body body) {
		super(context);
		//mContext = context;
		mBody = body;
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
		
		/*
		 * Lock Body Checkbox
		 */
		CheckBox lockBodyCheckbox = (CheckBox)findViewById(R.id.checkbox_lock_body);
		lockBodyCheckbox.setChecked(mBody.getPhysics().isFixed());
		lockBodyCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				mBody.getPhysics().setBodyFixed(arg1);
			}
			
		});
		
		/*
		 * Density SeekBar 
		 */
		SeekBar densitySeekBar = (SeekBar)findViewById(R.id.seek_bodys_density);
		densitySeekBar.setMax(400); /* Density is between [-200, 199] */
		densitySeekBar.setProgress((int) (Math.log( /* Logaritmical progress */
				mBody.getPhysics().getDensity()) * 10f + 200f));

		densitySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (!fromUser)
					return;
				/* Density is adjusted exponentially */
				mBody.getPhysics().setDensity((float) Math.exp( 
						((float) seekBar.getProgress() - 200f) / 10f));
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		
    }


}
